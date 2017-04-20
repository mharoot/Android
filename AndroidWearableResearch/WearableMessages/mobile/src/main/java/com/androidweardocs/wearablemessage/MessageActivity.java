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
import java.io.OutputStreamWriter;


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
        String message = null;//.STRING_DATA;
        try {
            message = mStringData.compress();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String mFilename = "WearableMessageProject.txt";//"bytes.txt";
        String mFilenameCopy = "WearableMessageProject1.txt";
        String bytestxt = "bytes.txt";
        boolean writingToSdCard = true;
        if (writingToSdCard) {
            try {
                File myFile = new File("/sdcard/"+bytestxt);
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(fOut);

                //myOutWriter.append("oooooooooooollllllll very very");
                //message = getTextFileMessageFromSD(mFilenameCopy);
                //myOutWriter.append(message);
                //BinaryStdOut.write("isllllll very very");
                //BinaryStdOut.close(); // and done
                Huffman.compress();



                myOutWriter.close();
                fOut.close();
                Toast.makeText(getBaseContext(),
                        "Done writing SD '"+bytestxt+"'",
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        } //end

    /*
    after the text file is saved to the sd card on mobile device, we read it and create a string to
    send a message to the wearable using getMessageFromFile call.

     */
        message = getTextFileMessageFromSD("bytes.txt");
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