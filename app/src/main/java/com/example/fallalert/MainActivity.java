package com.example.fallalert;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button btnCallCaregiver, btnCallEmergency, btnSetup;
    private TextView sensorDataBox;
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;

    private float[] accelerometerData = new float[3];
    private float[] gyroscopeData = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCallCaregiver = findViewById(R.id.btnCallCaregiver);
        btnCallEmergency = findViewById(R.id.btnCallEmergency);
        btnSetup = findViewById(R.id.btnSetup);
        sensorDataBox = findViewById(R.id.sensorDataBox);

        btnCallCaregiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the call caregiver functionality here
            }
        });

        btnCallEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the call emergency functionality here
            }
        });

        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//                startActivity(intent);
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerData, 0, event.values.length);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            System.arraycopy(event.values, 0, gyroscopeData, 0, event.values.length);
        }
        updateSensorDataDisplay();
    }

    private void updateSensorDataDisplay() {
        String accelerometerDisplay = "Accelerometer\n" +
                "X: " + accelerometerData[0] + "\n" +
                "Y: " + accelerometerData[1] + "\n" +
                "Z: " + accelerometerData[2];

        String gyroscopeDisplay = "Gyroscope\n" +
                "X: " + gyroscopeData[0] + "\n" +
                "Y: " + gyroscopeData[1] + "\n" +
                "Z: " + gyroscopeData[2];

        sensorDataBox.setText(accelerometerDisplay + "\n\n" + gyroscopeDisplay);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing for now
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
