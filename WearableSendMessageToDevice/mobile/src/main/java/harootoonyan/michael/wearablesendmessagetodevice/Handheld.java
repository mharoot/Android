package harootoonyan.michael.wearablesendmessagetodevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Handheld extends AppCompatActivity {

    private TextView mTextView;
    String message;
    Button mButton;
    Context mThis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.handheld);
        mTextView = (TextView) findViewById(R.id.text);


        mThis = this;

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }

    public void expandCompressedMessage() throws IOException {
        String dir = "storage/sdcard0/";

        System.setOut(new PrintStream(new FileOutputStream(new File(dir + "test2.txt"))));
        BinaryStdOut.setOut();
        BinaryStdOut.write(message);
        BinaryStdOut.close();



        System.setIn(new FileInputStream(new File(dir + "test2.txt")));
        System.setOut(new PrintStream(new FileOutputStream(new File(dir + "test3.txt"))));
        BinaryStdOut.setOut();


        Huffman.expand();

        InputStream is = new FileInputStream(dir + "test3.txt");
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = null;

        line = buf.readLine();

        StringBuilder sb = new StringBuilder();
        while (line != null) {
            sb.append(line).append("\n");
            line = buf.readLine();
        }
        String fileAsString = sb.toString();
        is.close();

        mTextView.setText(fileAsString);
    }
    public float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            message = intent.getStringExtra("message");
            //Log.v("myTag","Battery Level:" +getBatteryLevel());
            // Display message in UI
            mTextView.setText(message);

            if (mButton == null) {
                mButton = new Button(mThis);
                mButton.setText("Click to expand the compressed text");
                mButton.setId(R.id.mButton);
                mButton.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
                mButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            expandCompressedMessage();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.mInnerLinearLayout);
                mLinearLayout.addView(mButton, 0);
            }



        }
    }
}