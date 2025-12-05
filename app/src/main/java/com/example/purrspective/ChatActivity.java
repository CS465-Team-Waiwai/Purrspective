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

import kotlin.Unit;

public class ChatActivity extends AppCompatActivity {

    private ImageButton stickerButton, sendButton;
    // Use View so stickerPack can be a HorizontalScrollView or LinearLayout
    private View stickerPack;
    private LinearLayout chatContainer;
    private EditText messageInput;
    private ScrollView chatScrollView;
    private Button aiButton;

    // which sticker we chose (drawable res id)
    private int selectedSticker = 0;

    // contact / DB stuff
    private int contactId = -1;
    private String contactName;

    private AppDatabase db;
    private MessageDao messageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);

        stickerButton   = findViewById(R.id.stickerButton);
        chatContainer   = findViewById(R.id.chatContainer);
        messageInput    = findViewById(R.id.messageInput);
        chatScrollView  = findViewById(R.id.chatScrollView);
        stickerPack     = findViewById(R.id.stickerPack);
        sendButton      = findViewById(R.id.sendButton);
        Button backButton = findViewById(R.id.backButton);
        aiButton        = findViewById(R.id.aiButton);


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

        aiButton.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (!text.isEmpty()) {
                AIRephrase(text);
            }
        });

        setupStickerClick(sticker1, R.drawable.orange_happy);

        sendButton.setOnClickListener(v -> sendUserMessage());
    }


    private void loadHistoryMessages() {
        if (contactId == -1) return;

        new Thread(() -> {
            List<Message> history = messageDao.getMessagesForContact(contactId);

            runOnUiThread(() -> {
                //load them so they are alternating
                for (int i = 0; i < history.size(); i++) {
                    Message m = history.get(i);

                    if (i % 2 == 0) {
                        // user message
                        addMessageBubbleToUI(m);
                    } else {
                        // AI message (text only)
                        addAIMessage(m.getText());
                    }
                }

                scrollToBottom();
            });
        }).start();
    }



    private void sendUserMessage() {
        String userText = messageInput.getText().toString().trim();

        if (userText.isEmpty() && selectedSticker == 0) {
            return;
        }

        long now = System.currentTimeMillis();

        Message userMsg = new Message(
                contactId,
                userText,
                selectedSticker,
                now
        );

        addMessageBubbleToUI(userMsg);
        scrollToBottom();

        new Thread(() -> messageDao.insert(userMsg)).start();

        messageInput.setText("");
        selectedSticker = 0;
        sendButton.setVisibility(View.GONE);

        if (!userText.isEmpty()) {
            getAIResponse(userText);
        }
    }


    private void setupStickerClick(ImageView stickerView, int drawableResId) {
        stickerView.setOnClickListener(v -> {
            selectedSticker = drawableResId;
            stickerPack.setVisibility(View.GONE);      // hide sticker row
            sendButton.setVisibility(View.VISIBLE);    // show send button
        });
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


    private void addAIMessage(String text) {
        if (text == null || text.isEmpty()) return;

        LinearLayout aiLayout = new LinearLayout(this);
        aiLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.START; // AI on left
        params.setMargins(0, 12, 0, 12);
        aiLayout.setLayoutParams(params);

        aiLayout.setBackgroundResource(R.drawable.message_bubble);

        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setTextSize(16);
        textView.setPadding(8, 4, 8, 4);

        aiLayout.addView(textView);
        chatContainer.addView(aiLayout);

        scrollToBottom();
    }

    private void scrollToBottom() {
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }


    private void getAIResponse(String prompt) {
        VertexHelper.INSTANCE.askAsync(this, prompt, reply -> {
            addAIMessage(reply);

            long now = System.currentTimeMillis();
            Message aiMsg = new Message(
                    contactId,
                    reply,
                    0,
                    now
            );
            new Thread(() -> messageDao.insert(aiMsg)).start();

            return Unit.INSTANCE;
        });
    }

    private void AIRephrase(String sentence) {
        VertexHelper.INSTANCE.rephrase(this, sentence, reply -> {
            messageInput.setText(reply);
            messageInput.setSelection(messageInput.getText().length());
            return Unit.INSTANCE;
        });
    }
}
