package harootoonyan.michael.androidwearsensors;

/**
 * Created by michael on 4/12/17.
 */

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by michael on 2/11/17.
 */

public class SensorsHandler {

    private Context mContext;
    private SensorManager mSensorManager;
    private SensorEventListener mSensorEventListener;
    private float vibrateThreshold = 0;
    private Vibrator v;

    // accelerometer float variables
    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;
    private float deltaXMin = 0;
    private float deltaYMin = 0;
    private float deltaZMin = 0;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float lastX = 0, lastY = 0, lastZ = 0;

    // pressure variables
    private float deltaPressure = 0;
    private float deltaPressureMax = 0;
    private float deltaPressureMin = 10000;

    // light double variables
    private double deltaLightMax = 0;
    private double deltaLightMin = 100;
    private double deltaLight = 0;

    // display TextView variables
    private TextView availableSensors, currentLight, currentPressure, currentX, currentY, currentZ,
            maxLight, maxPressure, maxX, maxY, maxZ, minLight, minPressure, minX, minY, minZ;

    private DecimalFormat _DF = new DecimalFormat();
    private int DECIMAL_PLACES = 2;


    public SensorsHandler (Context context) {
        this.mContext = context;
        initView();
        _DF.setMaximumFractionDigits(DECIMAL_PLACES);
        mSensorManager = (SensorManager) this.mContext.getSystemService(Context.SENSOR_SERVICE);
        listenForChanges();
    }


    // reset the current values each time to grab new values.
    public void displayCleanValues() {
        currentX.setText("0.00");
        currentY.setText("0.00");
        currentZ.setText("0.00");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(_DF.format(deltaX));
        currentY.setText(_DF.format(deltaY));
        currentZ.setText(_DF.format(deltaZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(_DF.format(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(_DF.format(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(_DF.format(deltaZMax));
        }

    }

    // display the min x,y,z accelerometer values
    public void displayMinValues() {
        if (deltaX < deltaXMin) {
            deltaXMin = deltaX;
            minX.setText(_DF.format(deltaXMin));
        }
        if (deltaY < deltaYMin) {
            deltaYMin = deltaY;
            minY.setText(_DF.format(deltaYMin));
        }
        if (deltaZ < deltaZMin) {
            deltaZMin = deltaZ;
            minZ.setText(_DF.format(deltaZMin));
        }
    }

    private void listenForChanges() {
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged (Sensor sensor,int accuracy){
            }

            @Override
            public void onSensorChanged (SensorEvent event){
                Sensor sensor = event.sensor;
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    // clean current values
                    displayCleanValues();
                    // display the current x,y,z accelerometer values
                    displayCurrentValues();
                    // display the max x,y,z accelerometer values
                    displayMaxValues();
                    // display the min x,y,z accelerometer values
                    displayMinValues();
                    // get the change of the x,y,z values of the accelerometer
                    deltaX = (lastX - event.values[0]);
                    deltaY = (lastY - event.values[1]);
                    deltaZ = (lastZ - event.values[2]);

                    // if the change is below 2, it is just plain noise

                    if (-2 < deltaX && deltaX < 2)
                        deltaX = 0;
                    if (-2 < deltaY && deltaY < 2)
                        deltaY = 0;
                    if ((deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
                        v.vibrate(50);
                    }
                } else if(sensor.getType() == Sensor.TYPE_LIGHT) {

                    currentLight.setText(String.valueOf(event.values[0]));
                    deltaLight = event.values[0];
                    if (deltaLight > deltaLightMax) {
                        deltaLightMax = deltaLight;
                        maxLight.setText(String.valueOf(deltaLightMax));
                    }

                    if (deltaLight < deltaLightMin) {
                        deltaLightMin = deltaLight;
                        minLight.setText(String.valueOf(deltaLightMin));
                    }

                } else if(sensor.getType() == Sensor.TYPE_PRESSURE) {
                    //temporaily used proximty for pressure display

                    deltaPressure = event.values[0];
                    if (deltaPressure > deltaPressureMax) {
                        deltaPressureMax = deltaPressure;
                        maxPressure.setText(_DF.format(deltaPressureMax));
                    }

                    if (deltaPressure < deltaPressureMin) {
                        deltaPressureMin = deltaPressure;
                        minPressure.setText(_DF.format(deltaPressureMin));
                    }

                    currentPressure.setText(_DF.format(deltaPressure));

                }

            }
        };
    }


    public void getAvailableSensors() {
        String newline = System.getProperty("line.separator");
        String textView = ("You have the following sensors on this phone:"+newline+newline);


        Sensor heartRateMoto360= hasSensor(65538);

        if ( null !=  heartRateMoto360) {
            textView += ("Heart Rate Moto360 Sensor"+newline);
        } else {
            textView += ("No Heart Rate Moto360 Sensor found"+newline);
        }


        if ( null != hasSensor(Sensor.TYPE_ACCELEROMETER) ) {
            textView += ("Accelerometer sensor"+newline);
            // success! we have an accelerometer
            mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getMaximumRange() / 2;
        } else {
            textView += ("No accelerometer sensor found"+newline);
        }

        if ( null != hasSensor(Sensor.TYPE_LIGHT) ) {
            textView += ("Light sensor"+newline);
            mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            textView += ("No light sensor found"+newline);
        }

        if ( null != hasSensor(Sensor.TYPE_PRESSURE) ) {
            textView += ("Pressure sensor"+newline);
            mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            textView += ("No pressure sensor found"+newline);
        }

        if ( null != hasSensor(Sensor.TYPE_PROXIMITY) ) {
            textView += ("Proximity sensor"+newline);
        } else {
            textView += ("No proximity sensor found"+newline);
        }

        if ( null != hasSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) ) {
            textView += ("Ambient Temperature sensor"+newline);
        } else {
            textView += ("No Ambient Temperature sensor found"+newline);
        }

        if ( null != hasSensor(Sensor.TYPE_HEART_RATE) ) {
            textView += ("Heart rate sensor"+newline);
        } else {
            textView += ("No Heart rate sensor found"+newline);
        }

        if ( null != hasSensor(Sensor.TYPE_HEART_BEAT) ) {
            textView += ("Heart beat sensor"+newline);
        } else {
            textView += ("No Heart beat sensor found"+newline);
        }

        availableSensors.setText(textView);

    }

    private Sensor hasSensor(int type) {
        Sensor sensor = null;
        if (mSensorManager != null) {
            sensor = mSensorManager.getDefaultSensor(type);
        }
        return sensor;
    }

    private void initView() {

        availableSensors = (TextView) ((Activity)this.mContext).findViewById(R.id.availableSensors);

        // current acceleration
        currentX = (TextView) ((Activity)this.mContext).findViewById(R.id.currentX);
        currentY = (TextView) ((Activity)this.mContext).findViewById(R.id.currentY);
        currentZ = (TextView) ((Activity)this.mContext).findViewById(R.id.currentZ);

        // min acceleration
        minX = (TextView) ((Activity)this.mContext).findViewById(R.id.minX);
        minY = (TextView) ((Activity)this.mContext).findViewById(R.id.minY);
        minZ = (TextView) ((Activity)this.mContext).findViewById(R.id.minZ);

        // max acceleration
        maxX = (TextView) ((Activity)this.mContext).findViewById(R.id.maxX);
        maxY = (TextView) ((Activity)this.mContext).findViewById(R.id.maxY);
        maxZ = (TextView) ((Activity)this.mContext).findViewById(R.id.maxZ);

        // light
        currentLight = (TextView) ((Activity)this.mContext).findViewById(R.id.currentLight);
        minLight = (TextView) ((Activity)this.mContext).findViewById(R.id.minLight);
        maxLight = (TextView) ((Activity)this.mContext).findViewById(R.id.maxLight);

        // pressure
        currentPressure = (TextView) ((Activity)this.mContext).findViewById(R.id.currentPressure);
        minPressure = (TextView) ((Activity)this.mContext).findViewById(R.id.minPressure);
        maxPressure = (TextView) ((Activity)this.mContext).findViewById(R.id.maxPressure);


    }
}