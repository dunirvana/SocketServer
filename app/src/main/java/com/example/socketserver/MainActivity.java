package com.example.socketserver;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IMessage {

    private ServerThread _st;

    Thread serverThread = null;
    public static final int SERVER_PORT = 3003;
    private LinearLayout msgList;
    private Handler handler;
    private EditText edMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Server");
        handler = new Handler();
        msgList = findViewById(R.id.msgList);
        edMessage = findViewById(R.id.edMessage);
    }

    public TextView textView(String message, int color) {
        if (null == message || message.trim().isEmpty()) {
            message = "<Empty Message>";
        }
        TextView tv = new TextView(this);
        tv.setTextColor(color);
        tv.setText(String.format("%s [%s]", message, getTime()));
        tv.setTextSize(20);
        tv.setPadding(0, 5, 0, 0);
        return tv;
    }

    public void showMessage(final String message, final int color) {
        handler.post(() -> msgList.addView(textView(message, color)));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.start_server) {
            msgList.removeAllViews();
            showMessage("Server Started.", Color.BLACK);

            _st = new ServerThread(MainActivity.this, SERVER_PORT);
            this.serverThread = new Thread(_st);

            this.serverThread.start();
            return;
        }
        if (view.getId() == R.id.send_data) {
            String msg = edMessage.getText().toString().trim();
            showMessage("Server : " + msg, Color.BLUE);
            sendMessage(msg, false);
        }
    }

    private int _globalId = 0;

    public void sendMessage(final String message, boolean isConfirmationMessage) {
        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("id", _globalId++);
            jsonData.put("message", message);
            jsonData.put("isConfirmationMessage", isConfirmationMessage);

            _st.sendMessage(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLastMessageSentConfirmed(char value) {

        _st.setLastMessageSentConfirmed(value);
    }

    public void setVisibility(int id, int visibility) {
        findViewById(id).setVisibility(visibility);
    }

    String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != serverThread) {
            sendMessage("Disconnect", false);
            serverThread.interrupt();
            serverThread = null;
        }
    }
}