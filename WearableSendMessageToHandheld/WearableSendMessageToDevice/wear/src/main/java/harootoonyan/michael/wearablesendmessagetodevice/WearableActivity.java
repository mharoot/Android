package harootoonyan.michael.wearablesendmessagetodevice;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.WatchViewStub;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;


public class WearableActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Button mButton;
    GoogleApiClient googleClient;
    StringData mStringData;
    private TextView mTextView;
    String dir;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();
        setContentView(R.layout.wearable);
        mStringData = new StringData();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        dir = this.getFilesDir().getPath()+"/";//"/data/user/0/harootoonyan.michael.wearablesendmessagetodevice/files/WearableSendMessageToDevice/";//Environment.getRootDirectory().getAbsolutePath()+"/";//getExternalStorageDirectory().getAbsolutePath()+"/";//Environment.getDataDirectory()+"/";
        System.out.println(dir);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mTextView.setText(mStringData.STRING_DATA);
                mButton = (Button) findViewById(R.id.compressTextButton);
                mButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        try {
                            sendData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        // Build a new GoogleApiClient that includes the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleClient.connect();



    }
    public void sendData() throws IOException {
        String message = ""; // about 66k chars limit

        //in order to run application B switch to false to use non compression version
        boolean isCompressionVersion = true;

        // Writing the uncompressed text file from scratch or over an existing one.
        PrintWriter writer = new PrintWriter(dir+"uncompressed.txt", "UTF-8");
        writer.println(mStringData.STRING_DATA);
        writer.close();

        // Setting up the input and output file for Huffman compression and using ISO-8859-1 because
        // we have written codepoint chars 128-256, as one BYTE, instead of 2 BYTES like in UTF-8.
        System.setIn(new FileInputStream(dir+"uncompressed.txt"));
        System.setOut(new PrintStream(dir+"compressed.txt", "ISO-8859-1"));

        // Performing Huffman compression.
        Huffman.compress();


        // taking the compressed file creating a new FIS and storing it in System.in
        System.setIn(new FileInputStream(dir+"compressed.txt"));

        // need the file length to read all bytes of our compressed file
        int fileLength = (int) new File(dir+"compressed.txt").length();

        // isr that reads System.in in ISO-8859-1 format
        InputStreamReader inputStreamReader = new InputStreamReader(System.in, "ISO-8859-1");

        // fill our bytes array with the compressed data
        char[] bytes = new char[fileLength];
        inputStreamReader.read(bytes, 0, fileLength);

        // convert each byte to its char representation and append to String message
        for (int i = 0; i < bytes.length; i++) {
            int k = (int) bytes[i];
            char p = (char) k;
            message += p;
        }

        if (!isCompressionVersion) {
            message = mStringData.STRING_DATA;
        }

        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread("/message_path", message).start();
    }
    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
        //googleClient.connect();
    }

    // Send a message when the data layer connection is successful.

    @Override
    public void onConnected(Bundle connectionHint) {
        Toast.makeText(this.getApplicationContext(),"Connect to Google Client... Button Ready!", Toast.LENGTH_SHORT);
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
                    wakeLock.release();
                } else {
                    // Log an error
                    Log.v("myTag", "ERROR: failed to send Message");
                }
            }
        }
    }
}