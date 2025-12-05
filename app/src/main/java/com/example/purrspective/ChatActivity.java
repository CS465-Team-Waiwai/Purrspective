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
    private View stickerPack; //changed this from linearlayout to View to allow horizontally scrollable
    private LinearLayout chatContainer;
    private EditText messageInput;
    private ScrollView chatScrollView;
    private ImageView aiButton;

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



        ImageView sticker1 = findViewById(R.id.sticker1); // happy
        ImageView sticker2 = findViewById(R.id.sticker2); // sad
        ImageView sticker3 = findViewById(R.id.sticker3); // angry
        ImageView sticker4 = findViewById(R.id.sticker4); // surprised
        ImageView sticker5 = findViewById(R.id.sticker5); // working
        ImageView sticker6 = findViewById(R.id.sticker6); // sleepy


        contactId = getIntent().getIntExtra("contact_id", -1);
        contactName = getIntent().getStringExtra("contact_name");

        db = AppDatabase.getInstance(getApplicationContext());
        messageDao = db.messageDao();

        ContactDao contactDao = db.contactDao();
        Contact contact = contactDao.getContactById(contactId);

        int style = 0;
        if (contact != null) {
            style = contact.getStyleId();  // the field you added earlier
        }

// Apply the correct sticker pack based on style
        applyStickerPack(style);



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
                sticker1.setVisibility(View.VISIBLE);
                sticker2.setVisibility(View.VISIBLE);
                sticker3.setVisibility(View.VISIBLE);
                sticker4.setVisibility(View.VISIBLE);
                sticker5.setVisibility(View.VISIBLE);
                sticker6.setVisibility(View.VISIBLE);
            }
        });
        


        // once we click on sticker pack, we can see and select stickers and then be able to send
        setupStickerClick(sticker1, R.drawable.orange_happy);
        setupStickerClick(sticker2, R.drawable.orange_sad);
        setupStickerClick(sticker3, R.drawable.orange_angry);
        setupStickerClick(sticker4, R.drawable.orange_surprised);
        setupStickerClick(sticker5, R.drawable.orange_working);
        setupStickerClick(sticker6, R.drawable.orange_sleepy);


        // when Send is pressed, we send message + sticker to chat
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString().trim();

                if (selectedSticker != 0) { // means a sticker was chosen
                    // create an ImageView for the sticker
                    ImageView stickerView = new ImageView(ChatActivity.this);
                    stickerView.setImageResource(selectedSticker);
                    stickerView.setAdjustViewBounds(true);
                    stickerView.setMaxWidth(200);
                    stickerView.setMaxHeight(200);

                    // create a mini container for message + sticker
                    LinearLayout messageLayout = new LinearLayout(ChatActivity.this);
                    messageLayout.setOrientation(LinearLayout.VERTICAL);

                    // right-align the whole bubble inside chat container
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.gravity = android.view.Gravity.END; // aligns to the right
                    layoutParams.setMargins(0, 12, 0, 12); // adds vertical spacing between messages
                    messageLayout.setLayoutParams(layoutParams);

                    messageLayout.setBackgroundResource(R.drawable.message_bubble);

                    // text part (if user typed something)
                    if (!message.isEmpty()) {
                        android.widget.TextView textView = new android.widget.TextView(ChatActivity.this);
                        textView.setText(message);
                        textView.setTextColor(getResources().getColor(android.R.color.black));
                        textView.setTextSize(16);
                        textView.setPadding(8, 4, 8, 4);
                        messageLayout.addView(textView);
                    }

                    // sticker part
                    messageLayout.addView(stickerView);

                    // add the combined layout to chat container
                    chatContainer.addView(messageLayout);


                    // scroll to bottom
                    chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));

                    // reset for next message
                    messageInput.setText("");
                    selectedSticker = 0;
                    sendButton.setVisibility(View.GONE);

                    if (!message.isEmpty()) {
                        getAIResponse(message);
                    }

                }
            }
        });

        sendButton.setOnClickListener(v -> sendUserMessage());
    }

    @Override
    protected void onResume() {
        super.onResume();

        ContactDao contactDao = db.contactDao();
        Contact contact = contactDao.getContactById(contactId);

        if (contact != null) {
            int style = contact.getStyleId();
            applyStickerPack(style);
        }
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

    private void applyStickerPack(int style) {

        ImageView sticker1 = findViewById(R.id.sticker1);
        ImageView sticker2 = findViewById(R.id.sticker2);
        ImageView sticker3 = findViewById(R.id.sticker3);
        ImageView sticker4 = findViewById(R.id.sticker4);
        ImageView sticker5 = findViewById(R.id.sticker5);
        ImageView sticker6 = findViewById(R.id.sticker6);

        if (style == 1) {
            // GRAY PACK
            sticker1.setImageResource(R.drawable.orange_happy);
            sticker2.setImageResource(R.drawable.orange_sad);
            sticker3.setImageResource(R.drawable.orange_angry);
            sticker4.setImageResource(R.drawable.orange_surprised);
            sticker5.setImageResource(R.drawable.orange_working);
            sticker6.setImageResource(R.drawable.orange_sleepy);

            setupStickerClick(sticker1, R.drawable.orange_happy);
            setupStickerClick(sticker2, R.drawable.orange_sad);
            setupStickerClick(sticker3, R.drawable.orange_angry);
            setupStickerClick(sticker4, R.drawable.orange_surprised);
            setupStickerClick(sticker5, R.drawable.orange_working);
            setupStickerClick(sticker6, R.drawable.orange_sleepy);



        } else if (style == 2) {
            // ORANGE PACK
            sticker1.setImageResource(R.drawable.gray_happy);
            sticker2.setImageResource(R.drawable.gray_sad);
            sticker3.setImageResource(R.drawable.gray_angry);
            sticker4.setImageResource(R.drawable.gray_surprised);
            sticker5.setImageResource(R.drawable.gray_working);
            sticker6.setImageResource(R.drawable.gray_sleepy);

            setupStickerClick(sticker1, R.drawable.gray_happy);
            setupStickerClick(sticker2, R.drawable.gray_sad);
            setupStickerClick(sticker3, R.drawable.gray_angry);
            setupStickerClick(sticker4, R.drawable.gray_surprised);
            setupStickerClick(sticker5, R.drawable.gray_working);
            setupStickerClick(sticker6, R.drawable.gray_sleepy);

        } else {
            // default pack
            sticker1.setImageResource(R.drawable.orange_happy);
            sticker2.setImageResource(R.drawable.orange_sad);
            sticker3.setImageResource(R.drawable.orange_angry);
            sticker4.setImageResource(R.drawable.orange_surprised);
            sticker5.setImageResource(R.drawable.orange_working);
            sticker6.setImageResource(R.drawable.orange_sleepy);

            setupStickerClick(sticker1, R.drawable.orange_happy);
            setupStickerClick(sticker2, R.drawable.orange_sad);
            setupStickerClick(sticker3, R.drawable.orange_angry);
            setupStickerClick(sticker4, R.drawable.orange_surprised);
            setupStickerClick(sticker5, R.drawable.orange_working);
            setupStickerClick(sticker6, R.drawable.orange_sleepy);
        }
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


