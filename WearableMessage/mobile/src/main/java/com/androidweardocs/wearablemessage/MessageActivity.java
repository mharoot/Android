package com.androidweardocs.wearablemessage;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class MessageActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Button mButton;
    TextView mTextView;
    GoogleApiClient googleClient;
    StringData mStringData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mStringData = new StringData();
        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setText(mStringData.STRING_DATA);
        // Build a new GoogleApiClient that includes the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mButton = (Button) findViewById(R.id.compressTextButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                googleClient.connect();
            }
        });


    }

    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
        //googleClient.connect();
    }

    // Send a message when the data layer connection is successful.
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onConnected(Bundle connectionHint) {
        String message = mStringData.STRING_DATA;
        try {
            PrintWriter writer = new PrintWriter("storage/sdcard0/test.txt", "UTF-8");
            writer.println(message);
            writer.close();
        } catch (IOException e) {
            // do something
        }

        try {
            System.setIn(new FileInputStream("storage/sdcard0/test.txt"));

            try {
                System.setOut(new PrintStream("storage/sdcard0/test2.txt", "ISO-8859-1"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        Huffman.compress();
        try {
            System.setIn(new FileInputStream("storage/sdcard0/test2.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            File file = new File("storage/sdcard0/test2.txt");

            InputStreamReader inputStreamReader = new InputStreamReader(System.in,"ISO-8859-1");
            char [] bytes = new char[(int)file.length()];
            inputStreamReader.read(bytes,0,(int)file.length());

            for(int i = 0; i < bytes.length; i++)
            {
                int k = (int)bytes[i];
                char p = (char)k;
                message+=p;
            }
            System.out.print(message);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        try {

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BinaryStdIn.setIn();

        message = BinaryStdIn.readString();
        */



        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread("/message_path", message).start();
    }

    private String getTextFileMessageFromSD(String mFilename) {
        //*Don't* hardcode "/sdcard"
        File sdcard = Environment.getExternalStorageDirectory(); // Find the directory for the SD Card using the API

        //Get the text file
        File file = new File(sdcard, mFilename);

        //Read text from file
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        return text.toString();
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    // Unused project wizard code
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class SendToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    // Log an error
                    Log.v("myTag", "ERROR: failed to send Message");
                }
            }
        }
    }
}
/*
package com.androidweardocs.wearablemessage;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;


public class MessageActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Button mButton;
    TextView mTextView;
    GoogleApiClient googleClient;
    StringData mStringData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mStringData = new StringData();
        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setText(mStringData.STRING_DATA);
        // Build a new GoogleApiClient that includes the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mButton = (Button) findViewById(R.id.compressTextButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                googleClient.connect();
            }
        });


    }

    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
        //googleClient.connect();
    }

    static boolean count = false;
    static String message;
    // Send a message when the data layer connection is successful.
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onConnected(Bundle connectionHint) {
        if(count == false){
        try {
            PrintWriter writer = new PrintWriter("storage/sdcard0/test.txt", "UTF-8");
            writer.println("Yo Homie, This that west side!");
            writer.close();
        } catch (IOException e) {
            // do something
        }

        try {
            System.setIn(new FileInputStream("storage/sdcard0/test.txt"));
            System.setOut(new PrintStream(new FileOutputStream("storage/sdcard0/test2.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Huffman.compress();


        InputStream is = null;
        String fileAsString = "";
        try {
            is = new FileInputStream("storage/sdcard0/test2.txt");
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
        count = true;
        message = fileAsString;
    }

        new SendToDataLayerThread("/message_path", "fuck").start();
    }

    private String getTextFileMessageFromSD(String mFilename) {
        //*Don't* hardcode "/sdcard"
        File sdcard = Environment.getExternalStorageDirectory(); // Find the directory for the SD Card using the API

        //Get the text file
        File file = new File(sdcard, mFilename);

        //Read text from file
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        return text.toString();
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    // Unused project wizard code
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class SendToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    // Log an error
                    Log.v("myTag", "ERROR: failed to send Message");
                }
            }
        }
    }
}
*/
