package michael.harootoonyan.project6;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    public TextView wifiSingalTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiSingalTextView = (TextView) findViewById(R.id.wifiSignalTextView);
        runSignalStrengthUpdaterThread();
    }

    private void runSignalStrengthUpdaterThread() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                while (1 == 1) {
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        public void run() {
                            wifiManager.startScan();
                            List<ScanResult> list = wifiManager.getScanResults();
                            wifiSingalTextView.append(list.get(0).level + "\n");
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }
}