package com.example.fallalert;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;



import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button btnCallCaregiver, btnCallEmergency, btnSetup;
    private TextView sensorDataBox, resultTextView;
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;

    private float[] accelerometerData = new float[3];
    private float[] gyroscopeData = new float[3];

    private boolean isAlertShowing = false;
    private TFLiteHelper tfLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TensorFlow Lite helper
        try {
            tfLiteHelper = new TFLiteHelper(getAssets(), "fall_detection_model.tflite");
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnCallCaregiver = findViewById(R.id.btnCallCaregiver);
        btnCallEmergency = findViewById(R.id.btnCallEmergency);
        btnSetup = findViewById(R.id.btnSetup);
        sensorDataBox = findViewById(R.id.sensorDataBox);
        resultTextView = findViewById(R.id.resultTextView);

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
                // Implement the setup functionality here
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

        // Prepare input data for the model
        float[] inputData = new float[6];
        System.arraycopy(accelerometerData, 0, inputData, 0, 3);
        System.arraycopy(gyroscopeData, 0, inputData, 3, 3);

        // Run the model
        int prediction = tfLiteHelper.predict(inputData);


        if (prediction == 1) {
            showAlertDialog();
        }


        // Update the UI with the prediction result
        String resultText = prediction == 1 ? "Fall detected!" : "No fall detected.";
        resultTextView.setText(resultText);

        updateSensorDataDisplay();
    }
    private void showAlertDialog() {
        isAlertShowing = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fall Detected");
        builder.setMessage("Are you all right?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User is all right, dismiss dialog
                isAlertShowing = false;
                dialog.dismiss();


                String phoneNumber = "1234567890"; // Change this to the desired phone number
                // Check if the CALL_PHONE permission is granted
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // Request the CALL_PHONE permission if not granted
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else {
                    // Call the specified number
                    callNumber(phoneNumber);
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User is not all right, implement call emergency functionality
                isAlertShowing = false;
                callEmergency();
            }
        });
        builder.setCancelable(false); // Make the dialog non-cancelable
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Add a 10-second countdown timer
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                alertDialog.setMessage("Are you all right? (" + millisUntilFinished / 1000 + "s)");
            }

            public void onFinish() {
                alertDialog.dismiss();
                // Implement what to do when the timer finishes (e.g., callEmergency())
                callEmergency();
            }
        }.start();
    }
    private void callNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
    private void callEmergency() {
        // Implement the call emergency functionality here
        // Example: Show a message or actually make a call
        resultTextView.setText("Calling Emergency Services...");
        Toast.makeText(MainActivity.this, "Emergency services called!", Toast.LENGTH_SHORT).show();
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
