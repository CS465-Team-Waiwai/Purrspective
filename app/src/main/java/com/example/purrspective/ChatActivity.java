package com.example.purrspective;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ImageButton stickerButton;
    private ImageButton sendButton;
    private Button aiButton;

    private LinearLayout stickerPack;
    private LinearLayout chatContainer;
    private EditText messageInput;
    private ScrollView chatScrollView;

    // 貼圖 resource id，0 表示沒有貼圖
    private int selectedStickerResId = 0;

    // 聯絡人資訊
    private int contactId = -1;
    private String contactName;

    // Room database
    private AppDatabase db;
    private MessageDao messageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);

        // 取得畫面上的元件
        stickerButton   = findViewById(R.id.stickerButton);
        sendButton      = findViewById(R.id.sendButton);
        aiButton        = findViewById(R.id.aiButton);
        stickerPack     = findViewById(R.id.stickerPack);
        chatContainer   = findViewById(R.id.chatContainer);
        messageInput    = findViewById(R.id.messageInput);
        chatScrollView  = findViewById(R.id.chatScrollView);
        Button backButton = findViewById(R.id.backButton);

        // 一張示範貼圖（如果你有更多，可以照樣再加）
        ImageView sticker1 = findViewById(R.id.sticker1);

        // 從 Intent 拿到這個聊天室對應的 contact
        contactId = getIntent().getIntExtra("contact_id", -1);
        contactName = getIntent().getStringExtra("contact_name");

        // 建立資料庫與 DAO
        db = AppDatabase.getInstance(getApplicationContext());
        messageDao = db.messageDao();

        // 載入這個 contact 的歷史訊息
        loadHistoryMessages();

        // 返回按鈕：結束 Activity
        backButton.setOnClickListener(v -> finish());

        // 打開 / 關閉貼圖列
        stickerButton.setOnClickListener(v -> {
            if (stickerPack.getVisibility() == View.GONE) {
                stickerPack.setVisibility(View.VISIBLE);
            } else {
                stickerPack.setVisibility(View.GONE);
            }
        });

        // 點選貼圖：只記錄選中的貼圖，並顯示 send 按鈕
        sticker1.setOnClickListener(v -> {
            selectedStickerResId = R.drawable.cat_sticker3;
            stickerPack.setVisibility(View.GONE);
            sendButton.setVisibility(View.VISIBLE);
        });

        // Send：送出訊息（會存 DB）
        sendButton.setOnClickListener(v -> sendUserMessage());

        // AI：只改文字，不送、不存
        aiButton.setOnClickListener(v -> onAiButtonClicked());
    }

    /**
     * 讀取這個 contact 的歷史訊息，從 DB 撈出來畫在畫面上
     */
    private void loadHistoryMessages() {
        if (contactId == -1) return;

        new Thread(() -> {
            List<Message> history = messageDao.getMessagesForContact(contactId);
            runOnUiThread(() -> {
                for (Message m : history) {
                    addMessageBubbleToUI(m);
                }
                scrollToBottom();
            });
        }).start();
    }

    /**
     * 真正送出訊息：
     * - 將目前輸入框內容 + 貼圖（如果有）存成 Message
     * - 畫在 UI
     * - 寫進 Room DB
     */
    private void sendUserMessage() {
        String userText = messageInput.getText().toString().trim();

        // 沒有文字 & 沒有貼圖就不送
        if (userText.isEmpty() && selectedStickerResId == 0) {
            return;
        }

        long now = System.currentTimeMillis();

        Message userMsg = new Message(
                contactId,
                userText,
                selectedStickerResId,
                now
        );

        // UI 立刻顯示
        addMessageBubbleToUI(userMsg);
        scrollToBottom();

        // 背景執行存進 DB
        new Thread(() -> messageDao.insert(userMsg)).start();

        // 清空輸入與狀態
        messageInput.setText("");
        selectedStickerResId = 0;
        sendButton.setVisibility(View.GONE);
    }

    /**
     * AI 按鈕：只請 AI 幫忙 rewrite，目前先用假的示範字串，
     * 之後你們可以換成 Vertex / Gemini 的真正回覆
     */
    private void onAiButtonClicked() {
        String original = messageInput.getText().toString().trim();
        if (original.isEmpty()) {
            return; // 沒打東西就不叫 AI
        }

        // TODO：這裡之後改成呼叫你們的 VertexHelper / Gemini API
        // 例如：VertexHelper.rewriteMessage(original, suggestion -> { ... });
        String rewritten = "AI suggestion: " + original;

        // 把輸入框的文字改成 AI 建議
        messageInput.setText(rewritten);
        messageInput.setSelection(messageInput.getText().length());
    }

    /**
     * 把一則 Message 畫成右側對話泡泡
     */
    private void addMessageBubbleToUI(Message messageObj) {
        String text = messageObj.getText();
        int stickerResId = messageObj.getStickerResId();

        if ((text == null || text.isEmpty()) && stickerResId == 0) {
            return;
        }

        LinearLayout bubble = new LinearLayout(this);
        bubble.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.END; // 右對齊
        params.setMargins(0, 12, 0, 12);
        bubble.setLayoutParams(params);

        bubble.setBackgroundResource(R.drawable.message_bubble);

        // 文字部分
        if (text != null && !text.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText(text);
            tv.setTextColor(getResources().getColor(android.R.color.black));
            tv.setTextSize(16);
            tv.setPadding(16, 8, 16, 8);
            bubble.addView(tv);
        }

        // 貼圖部分
        if (stickerResId != 0) {
            ImageView stickerView = new ImageView(this);
            stickerView.setImageResource(stickerResId);
            stickerView.setAdjustViewBounds(true);
            stickerView.setMaxWidth(200);
            stickerView.setMaxHeight(200);
            bubble.addView(stickerView);
        }

        chatContainer.addView(bubble);
    }

    private void scrollToBottom() {
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }
}
