package com.androidweardocs.wearablemessage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;

public class MessageActivity extends WearableActivity {

    private TextView mTextView;
    String message;
    Button mButton;
    Context mThis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        mThis = this;
        setAmbientEnabled();

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            message = intent.getStringExtra("message");
            Log.v("myTag", "Main activity received message: " + message);
            // Display message in UI
            mTextView.setText(message);

            mButton = new Button(mThis);
            mButton.setText("Click to expand the compressed text");
            mButton.setId(R.id.mButton);
            mButton.setLayoutParams(new BoxInsetLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String txt = "/data/user/0/com.androidweardocs.wearablemessage/files/WearableMessage/";
                    try {

                        System.setOut(new PrintStream(new FileOutputStream(new File(txt+"test2.txt"))));
                        BinaryStdOut.write(message);
                        BinaryStdOut.close();

                        System.setIn(new FileInputStream(new File(txt+"test2.txt")));
                        System.setOut(new PrintStream(new FileOutputStream(new File(txt+"test3.txt"))));
                        BinaryStdOut.setOut();

                        Huffman.expand(41);
                        InputStream is = null;
                        String fileAsString = "";
                        try {
                            is = new FileInputStream(txt+"test3.txt");
                            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                            String line = null;

                            line = buf.readLine();

                            StringBuilder sb = new StringBuilder();
                            while (line != null) {
                                sb.append(line).append("\n");
                                line = buf.readLine();
                            }
                            fileAsString = sb.toString();
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mTextView.setText(fileAsString);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });

            LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.mInnerLinearLayout);
            mLinearLayout.addView(mButton);



        }
    }
}