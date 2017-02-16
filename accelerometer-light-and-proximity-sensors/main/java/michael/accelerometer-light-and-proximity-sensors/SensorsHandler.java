package michael.minmaxcurrentaccelerationsensor;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

/**
 * Created by michael on 2/11/17.
 */

public class SensorsHandler {

    private Context context;
    private SensorManager sensorService;
    public SensorsHandler (Context context) {
        this.context = context;
        sensorService = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);
    }

    public String getAvailableSensors() {
        String newline = System.getProperty("line.separator");
        String textView = ("You have the following sensors on this phone:"+newline+newline);

        if ( hasSensor(Sensor.TYPE_ACCELEROMETER) ) {
            textView += ("Accelerometer sensor"+newline);
        } else {
            textView += ("No accelerometer sensor found"+newline);
        }

        if ( hasSensor(Sensor.TYPE_GRAVITY) ) {
            textView += ("Gravity sensor"+newline);
        } else {
            textView += ("No Gravity sensor found"+newline);
        }


        if ( hasSensor(Sensor.TYPE_GYROSCOPE) ) {
            textView += ("Gyroscope sensor"+newline);
        } else {
            textView += ("No Gyroscope sensor found"+newline);
        }

        if ( hasSensor(Sensor.TYPE_LIGHT) ) {
            textView += ("Light sensor"+newline);
        } else {
            textView += ("No light sensor found");
        }

        if ( hasSensor(Sensor.TYPE_PRESSURE) ) {
            textView += ("Pressure sensor"+newline);
        } else {
            textView += ("No pressure sensor found"+newline);
        }

        if ( hasSensor(Sensor.TYPE_PROXIMITY) ) {
            textView += ("Proximity sensor"+newline);
        } else {
            textView += ("No proximity sensor found");
        }

        if ( hasSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) ) {
            textView += ("Ambient Temperature sensor"+newline);
        } else {
            textView += ("No Ambient Temperature sensor found"+newline);
        }

        return textView;

    }

    private boolean hasSensor(int type) {
        boolean supported = false;
        if (sensorService != null) {
            List<Sensor> sensors = sensorService.getSensorList(type);
            supported = sensors != null && sensors.size() > 0;
        }
        return supported;
    }
}

