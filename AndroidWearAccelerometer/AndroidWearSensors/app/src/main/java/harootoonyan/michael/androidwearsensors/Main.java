package harootoonyan.michael.androidwearsensors;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;


public class Main extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                Context mContext = stub.getContext();
                SensorsHandler handler = new SensorsHandler(mContext);
                String txt = handler.getAvailableSensors();


            }
        });
    }
}
