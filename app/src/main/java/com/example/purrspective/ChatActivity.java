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

    private int selectedStickerResId = 0;

    private int contactId = -1;
    private String contactName;

    private AppDatabase db;
    private MessageDao messageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);

        stickerButton   = findViewById(R.id.stickerButton);
        sendButton      = findViewById(R.id.sendButton);
        aiButton        = findViewById(R.id.aiButton);
        stickerPack     = findViewById(R.id.stickerPack);
        chatContainer   = findViewById(R.id.chatContainer);
        messageInput    = findViewById(R.id.messageInput);
        chatScrollView  = findViewById(R.id.chatScrollView);
        Button backButton = findViewById(R.id.backButton);

        ImageView sticker1 = findViewById(R.id.sticker1);

        contactId = getIntent().getIntExtra("contact_id", -1);
        contactName = getIntent().getStringExtra("contact_name");

        db = AppDatabase.getInstance(getApplicationContext());
        messageDao = db.messageDao();

        loadHistoryMessages();

        backButton.setOnClickListener(v -> finish());

        stickerButton.setOnClickListener(v -> {
            if (stickerPack.getVisibility() == View.GONE) {
                stickerPack.setVisibility(View.VISIBLE);
            } else {
                stickerPack.setVisibility(View.GONE);
            }
        });

        sticker1.setOnClickListener(v -> {
            selectedStickerResId = R.drawable.cat_sticker3;
            stickerPack.setVisibility(View.GONE);
            sendButton.setVisibility(View.VISIBLE);
        });

        sendButton.setOnClickListener(v -> sendUserMessage(false));

        aiButton.setOnClickListener(v -> sendUserMessage(true));
    }


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


    private void sendUserMessage(boolean isFromAiButton) {
        String userText = messageInput.getText().toString().trim();

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

        addMessageBubbleToUI(userMsg);
        scrollToBottom();

        new Thread(() -> messageDao.insert(userMsg)).start();

        messageInput.setText("");
        selectedStickerResId = 0;
        sendButton.setVisibility(View.GONE);

        if (isFromAiButton) {
            requestAiReply(userText);
        }
    }


    private void requestAiReply(String userText) {

        String fakeReply = "AI: " + userText;
        long now = System.currentTimeMillis();

        Message aiMsg = new Message(
                contactId,
                fakeReply,
                0,
                now
        );

        addMessageBubbleToUI(aiMsg);
        scrollToBottom();

        new Thread(() -> messageDao.insert(aiMsg)).start();
    }


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
        params.gravity = Gravity.END;
        params.setMargins(0, 12, 0, 12);
        bubble.setLayoutParams(params);

        bubble.setBackgroundResource(R.drawable.message_bubble);

        if (text != null && !text.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText(text);
            tv.setTextColor(getResources().getColor(android.R.color.black));
            tv.setTextSize(16);
            tv.setPadding(16, 8, 16, 8);
            bubble.addView(tv);
        }

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
