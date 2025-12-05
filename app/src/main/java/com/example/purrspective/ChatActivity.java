package com.example.purrspective;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import kotlin.Unit;

public class ChatActivity extends AppCompatActivity {

    private ImageButton stickerButton, sendButton;
    private LinearLayout stickerPack;
    private LinearLayout chatContainer;
    private EditText messageInput;
    private ScrollView chatScrollView;

    private int selectedSticker = 0;

    private int contactId;
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
        Button aiButton = findViewById(R.id.aiButton);

        ImageView sticker1 = findViewById(R.id.sticker1);

        contactId = getIntent().getIntExtra("CONTACT_ID", -1);
        contactName = getIntent().getStringExtra("CONTACT_NAME");

        db = AppDatabase.getInstance(getApplicationContext());
        messageDao = db.messageDao();

        new Thread(() -> {
            List<Message> history = messageDao.getMessagesForContact(contactId);
            runOnUiThread(() -> {
                for (Message m : history) {
                    addMessageBubbleToUI(m);
                }
                chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
            });
        }).start();

        backButton.setOnClickListener(view -> finish());

        stickerButton.setOnClickListener(v -> {
            if (stickerPack.getVisibility() == View.GONE) {
                stickerPack.setVisibility(View.VISIBLE);
            } else {
                stickerPack.setVisibility(View.GONE);
            }
        });

        sticker1.setOnClickListener(v -> {
            selectedSticker = R.drawable.cat_sticker3;
            stickerPack.setVisibility(View.GONE);
            sendButton.setVisibility(View.VISIBLE);
        });



        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();

            if (selectedSticker != 0) {
                Message msg = new Message(
                        contactId,
                        messageText,
                        selectedSticker,
                        System.currentTimeMillis()
                );

                addMessageBubbleToUI(msg);
                chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));

                new Thread(() -> messageDao.insert(msg)).start();

                messageInput.setText("");
                selectedSticker = 0;
                sendButton.setVisibility(View.GONE);

                if (!messageText.isEmpty()) {
                    getAIResponse(messageText);
                }
            }
        });

        aiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = messageInput.getText().toString().trim();

                if (!text.isEmpty()) {
                    AIRephrase(text);
                }
            }
        });

        
    }


    private void addMessageBubbleToUI(Message messageObj) {
        String message = messageObj.getText();
        int stickerResId = messageObj.getStickerResId();

        ImageView stickerView = new ImageView(ChatActivity.this);
        stickerView.setImageResource(stickerResId);
        stickerView.setAdjustViewBounds(true);
        stickerView.setMaxWidth(200);
        stickerView.setMaxHeight(200);

        LinearLayout messageLayout = new LinearLayout(ChatActivity.this);
        messageLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = android.view.Gravity.END;
        layoutParams.setMargins(0, 12, 0, 12);
        messageLayout.setLayoutParams(layoutParams);

        messageLayout.setBackgroundResource(R.drawable.message_bubble);

        if (message != null && !message.isEmpty()) {
            android.widget.TextView textView = new android.widget.TextView(ChatActivity.this);
            textView.setText(message);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setTextSize(16);
            textView.setPadding(8, 4, 8, 4);
            messageLayout.addView(textView);
        }

        messageLayout.addView(stickerView);

        chatContainer.addView(messageLayout);
    }

    private void addAIMessage(String text) {
        LinearLayout aiLayout = new LinearLayout(this);
        aiLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = android.view.Gravity.START; // left side
        params.setMargins(0, 12, 0, 12);
        aiLayout.setLayoutParams(params);

        aiLayout.setBackgroundResource(R.drawable.message_bubble);

        android.widget.TextView textView = new android.widget.TextView(this);
        textView.setText(text);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setTextSize(16);
        textView.setPadding(8, 4, 8, 4);

        aiLayout.addView(textView);
        chatContainer.addView(aiLayout);

        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void getAIResponse(String prompt) {
        VertexHelper.INSTANCE.askAsync(this, prompt, reply -> {
            // This runs on the main thread already
            addAIMessage(reply);
            return Unit.INSTANCE;
        });
    }

    private void AIRephrase(String sentence) {
        VertexHelper.INSTANCE.rephrase(this, sentence, reply -> {
            messageInput.setText(reply);
            return Unit.INSTANCE;
        });
    }
}
