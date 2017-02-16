package michael.minmaxcurrentaccelerationsensor;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;

public class Main extends AppCompatActivity {

    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private SensorEventListener sensorListener;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;
    private float deltaXMin = 0;
    private float deltaYMin = 0;
    private float deltaZMin = 0;
    private double deltaProximityMax = 0;
    private double deltaProximityMin = 0;
    private double deltaLightMax = 0;
    private double deltaLightMin = 100;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private double deltaProximity = 0;
    private double deltaLight = 0;

    private float vibrateThreshold = 0;

    private TextView  currentLight, currentProximity, currentX, currentY, currentZ,
            maxX, maxY, maxZ, maxLight, maxProximity,
            minLight, minProximity, minX, minY, minZ,
            phoneSensors;

    public Vibrator v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initializeViews();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged (Sensor sensor,int accuracy){
            }

            @Override
            public void onSensorChanged (SensorEvent event){
                Sensor sensor = event.sensor;
                if (sensor.getType() == Sensor.TYPE_PROXIMITY) {

                    String txt = String.valueOf(event.values[0]);
                    if (event.values[0] > 3) {
                        currentProximity.setText(txt);
                        maxProximity.setText(txt);
                    }
                    else {
                        currentProximity.setText(txt);
                        minProximity.setText(txt);

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

                } else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
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
                }

            }
        };


        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getMaximumRange() / 2;
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);


    }

    public void initializeViews() {
        currentLight = (TextView) findViewById(R.id.currentLight);
        currentProximity = (TextView) findViewById(R.id.currentProximity);
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxLight = (TextView) findViewById(R.id.maxLight);
        maxProximity = (TextView) findViewById(R.id.maxProximity);
        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);

        minLight = (TextView) findViewById(R.id.minLight);
        minProximity = (TextView) findViewById(R.id.minProximity);
        minX = (TextView) findViewById(R.id.minX);
        minY = (TextView) findViewById(R.id.minY);
        minZ = (TextView) findViewById(R.id.minZ);

        phoneSensors = (TextView) findViewById(R.id.phoneSensors);
        phoneSensors.setText(new SensorsHandler(this).getAvailableSensors());


    }


    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }


    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
    }
    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }

    }
    // display the min x,y,z accelerometer values
    public void displayMinValues() {
        if (deltaX < deltaXMin) {
            deltaXMin = deltaX;
            minX.setText(Float.toString(deltaXMin));
        }
        if (deltaY < deltaYMin) {
            deltaYMin = deltaY;
            minY.setText(Float.toString(deltaYMin));
        }
        if (deltaZ < deltaZMin) {
            deltaZMin = deltaZ;
            minZ.setText(Float.toString(deltaZMin));
        }
    }
}

