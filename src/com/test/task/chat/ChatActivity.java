package com.test.task.chat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.test.task.chat.sqlite.DatabaseHelper;
import com.test.task.chat.sqlite.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Olenka on 18.11.2014.
 */
public class ChatActivity extends Activity{
    public static final String PARAM_PENDING_INTENT = "pending_intent_for_notifications";
    private static final int CODE = 1;
    private static String IMPORTANT_TAG = "#awesome_app";

    private LinearLayout mContainer;
    private Button mSendButton;
    private EditText mOutputMessage;
    private ScrollView mScrollView;
    private DatabaseHelper mHelper;
    private List<Message> mCurrentMessagesCache = new ArrayList<Message>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mHelper = new DatabaseHelper(this);
        initUI();
        updateChat();
        startNotificationService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        insertAllMessagesToDb();
    }

    @Override
    protected void onDestroy() {
        hideKeyboard();
        mHelper.closeDB();
        stopNotificationService();
        super.onDestroy();
    }

    private void initUI() {
        mContainer = (LinearLayout) findViewById(R.id.container);

        mOutputMessage = (EditText) findViewById(R.id.output_message);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mScrollView = (ScrollView) findViewById(R.id.message_scroll_view);

        mSendButton = (Button) findViewById(R.id.send_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                String message = mOutputMessage.getText().toString();
                if (!message.equals("")) {
                    Message m = new Message(message, Message.Type.OUTPUT, new Date());
                    addOutputBox(m);
                    mCurrentMessagesCache.add(m);
                    mOutputMessage.setText("");
                }
            }
        });
    }

    private void updateChat() {
        LoadMessageTask task = new LoadMessageTask();
        task.execute();
    }

    private void startNotificationService(){
        PendingIntent pendingIntent = createPendingResult(CODE, new Intent(), 0);
        Intent intent = new Intent(this, ClientService.class);
        intent.putExtra(PARAM_PENDING_INTENT, pendingIntent);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String message = data.getStringExtra(ClientService.MESSAGE_TEXT);
        message = markTagWithColor(message, IMPORTANT_TAG, "FF0000");
        Date date = new Date(data.getLongExtra(ClientService.DATE, 0L));
        Message m = new Message(message, Message.Type.INPUT, date);
        mCurrentMessagesCache.add(m);
        addInputBox(m);
    }

    public void stopNotificationService(){
        stopService(new Intent(this, ClientService.class));
    }

    private void addOutputBox(Message m) {
        View view = getLayoutInflater().inflate(R.layout.output_box, null);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.output_box_layout);
        TextView inputBox = (TextView) view.findViewById(R.id.output_box);
        String eol = System.getProperty("line.separator");
        inputBox.setText(m.getMessage() + eol + eol + "Send: " + m.getStringDate());
        mContainer.addView(layout);
    }

    private void addInputBox(Message m) {
        View view = getLayoutInflater().inflate(R.layout.input_box, null);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.input_box_layout);
        TextView inputBox = (TextView) view.findViewById(R.id.input_box);
        inputBox.setText(Html.fromHtml(m.getMessage() + "<br><br>" + "Send: " + m.getStringDate()));

        if(mContainer.getMeasuredHeight() <= mScrollView.getScrollY() + mScrollView.getHeight()) {
            mContainer.addView(layout);
            updateScrollView();
        } else {
            mContainer.addView(layout);
        }
    }

    private void updateScrollView(){
        mScrollView.post(new Runnable() {
            public void run() {
                mScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mOutputMessage.getWindowToken(), 0);
    }

    private String markTagWithColor(String text, String tag, String color){
        return text.replaceAll(tag, "<font color='#" + color + "'>" + tag + "</font>");
    }

    private void insertAllMessagesToDb(){
        if (mCurrentMessagesCache.isEmpty()){
            return;
        }
        mHelper.insertMessages(mCurrentMessagesCache);
        mCurrentMessagesCache.clear();
    }


    private class LoadMessageTask extends AsyncTask<Void, Void, List<Message>> {

        @Override
        protected List<Message> doInBackground(Void... params) {
            List<Message> messages = mHelper.getAllMessages();
            return messages;
        }

        @Override
        protected void onPostExecute(List<Message> messages) {
            Collections.sort(messages);
            for (Message m : messages){
                if (m.getType() == Message.Type.OUTPUT){
                    addOutputBox(m);
                } else {
                    addInputBox(m);
                }
            }
            updateScrollView();
        }
    }
}
