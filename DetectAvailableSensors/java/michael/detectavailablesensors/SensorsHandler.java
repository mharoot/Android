package michael.detectavailablesensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

/**
 * Created by michael on 2/11/17.
 */

public class SensorsHandler {

    private Context context;
    public SensorsHandler (Context context) {
        this.context = context;
    }

    public String getAvailaleSensors() {
        String newline = System.getProperty("line.separator");
        String textView = ("You have the following sensors on this phone:"+newline+newline);
        if ( hasLightSensor() ) {
            textView += ("Light sensor"+newline);
        } else {
            textView += ("No light sensor found");
        }

        if ( hasProximitySensor() ) {
            textView += ("Proximity sensor"+newline);
        } else {
            textView += ("No proximity sensor found");
        }

        if ( hasPressureSensor() ) {
            textView += ("Pressure sensor"+newline);
        } else {
            textView += ("No pressure sensor found"+newline);
        }

        if ( hasTemperatureSensor() ) {
            textView += ("Temperature sensor"+newline);
        } else {
            textView += ("No temperature sensor found"+newline);
        }

        return textView;

    }

    private boolean hasLightSensor() {
        boolean supported = false;
        SensorManager sensorService = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorService != null) {
            List<Sensor> lightSensors = sensorService.getSensorList(Sensor.TYPE_LIGHT);
            supported = lightSensors != null && lightSensors.size() > 0;
        }
        return supported;
    }

    private boolean hasProximitySensor() {
        boolean supported = false;
        SensorManager sensorService = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorService != null) {
            List<Sensor> lightSensors = sensorService.getSensorList(Sensor.TYPE_PROXIMITY);
            supported = lightSensors != null && lightSensors.size() > 0;
        }
        return supported;
    }

    private boolean hasPressureSensor() {
        boolean supported = false;
        SensorManager sensorService = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorService != null) {
            List<Sensor> lightSensors = sensorService.getSensorList(Sensor.TYPE_PRESSURE);
            supported = lightSensors != null && lightSensors.size() > 0;
        }
        return supported;
    }

    private boolean hasTemperatureSensor() {
        boolean supported = false;
        SensorManager sensorService = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorService != null) {
            List<Sensor> lightSensors = sensorService.getSensorList(Sensor.TYPE_TEMPERATURE);
            supported = lightSensors != null && lightSensors.size() > 0;
        }
        return supported;
    }
}
