package engg6400.project.fallalertnn;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.content.DialogInterface;
import android.app.AlertDialog;

import android.Manifest;


import java.io.IOException;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private float[] accelData = new float[3];
    private float[] gyroData = new float[3];
    //private CircularBuffer accelBuffer;
    private CircularArray accelBuffer;
    private CircularArray gyroBuffer;

    TextView acceleration;
    TextView gyroscope;
    TextView buffer;
    TextView sensorInfoTextView;

    int accelerationTriggerTime = 0;
    int gyroscopeTriggerTime = 0;

    AlertDialog alertDialog;
    private TFLiteHelper tfLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize TensorFlow Lite helper
        try {
            tfLiteHelper = new TFLiteHelper(getAssets(), "fall_detection_model.tflite");
        } catch (IOException e) {
            e.printStackTrace();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Get Accelerometer and Gyroscope sensors
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        accelBuffer = new CircularArray(1000); // Buffer size can be adjusted
        gyroBuffer = new CircularArray(1000);

        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        acceleration = findViewById(R.id.acceleration);
        gyroscope = findViewById(R.id.gyroscope);
        buffer = findViewById(R.id.Buffer);
        sensorInfoTextView = findViewById(R.id.SensorInfo);

        displaySensorInfo();

    }


    private void displaySensorInfo() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        StringBuilder sensorInfo = new StringBuilder();

        if (accelerometer != null) {
            //int resolutionBits = (int) Math.round(Math.log(2 * maxRange / resolution) / Math.log(2));

            sensorInfo.append("Accelerometer:\n")
                    .append("Name: ").append(accelerometer.getName()).append("\n")
                    .append("Vendor: ").append(accelerometer.getVendor()).append("\n")
                    .append("Version: ").append(accelerometer.getVersion()).append("\n")
                    .append("Maximum Range: ").append(accelerometer.getMaximumRange()).append("\n")
                    .append("Resolution: ").append((int) Math.round(Math.log(2 * accelerometer.getMaximumRange() / accelerometer.getResolution()) / Math.log(2))).append("\n")
                    .append("Power: ").append(accelerometer.getPower()).append(" mA\n\n");
        } else {
            sensorInfo.append("No Accelerometer found on this device.\n\n");
        }

        if (gyroscope != null) {
            sensorInfo.append("Gyroscope:\n")
                    .append("Name: ").append(gyroscope.getName()).append("\n")
                    .append("Vendor: ").append(gyroscope.getVendor()).append("\n")
                    .append("Version: ").append(gyroscope.getVersion()).append("\n")
                    .append("Maximum Range: ").append(gyroscope.getMaximumRange()).append("\n")
                    .append("Resolution: ").append((int) Math.round(Math.log(2 * gyroscope.getMaximumRange() / gyroscope.getResolution()) / Math.log(2))).append("\n")
                    .append("Power: ").append(gyroscope.getPower()).append(" mA\n\n");
        } else {
            sensorInfo.append("No Gyroscope found on this device.\n\n");
        }

        sensorInfoTextView.setText(sensorInfo.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listeners
        sensorManager.unregisterListener(this);
        //sensorManager.unregisterListener(gyroscopeListener);
    }
    // Accelerometer sensor listener

    public void displayAcceleroData(float[] data1) {
        acceleration.setText("accelerationTriggerTime:" + accelerationTriggerTime + "\nX:" + data1[0] + "\nY:" + data1[1] + "\nZ:" + data1[2]);
    }

    public void displayGyroData(float[] data2) {
        gyroscope.setText(
                "gyroscopeTriggerTime:" + gyroscopeTriggerTime + "\nX:" + data2[0] + "\nY:" + data2[1] + "\nZ:" + data2[2]);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelData = event.values;
            accelBuffer.addLast(accelData);
            accelerationTriggerTime = accelerationTriggerTime + 1;
            displayAcceleroData(accelData);

        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroData = event.values;
            gyroBuffer.addLast(gyroData);
            gyroscopeTriggerTime = gyroscopeTriggerTime + 1;
            displayGyroData((gyroData));
        }

        if (accelBuffer.size() > 3 && gyroBuffer.size() > 3) {

            accelBuffer.removeFromStart(3);
            gyroBuffer.removeFromStart(3);

            // Combine accelerometer and gyroscope data
            float[] inputData = new float[6];
            System.arraycopy(accelBuffer.getFirst(), 0, inputData, 0, 3);
            System.arraycopy(gyroBuffer.getFirst(), 0, inputData, 3, 3);

            // Prepare input for the model

            int fallDetect=tfLiteHelper.predict(inputData);
            String result = fallDetect == 1 ? "Fall detected!" : "";
            buffer.setText(result);
            if(fallDetect==1){
                sensorManager.unregisterListener(this);
                this.onPause();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Fall Detected!!");
                builder.setMessage("Are you all right? (10s)");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        buffer.setText("");
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User is not all right, implement call emergency functionality
                        //callEmergency();
                        makePhoneCall("911");
                    }
                });

                builder.setCancelable(false);

                alertDialog = builder.create();
                alertDialog.show();
                new CountDownTimer(10000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        alertDialog.setMessage("Are you all right? (" + millisUntilFinished / 1000 + "s)");
                    }

                    public void onFinish() {
                        alertDialog.dismiss();
                        // Implement what to do when the timer finishes (e.g., callEmergency())
                        makePhoneCall("911");
                    }
                }.start();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void CallCaregiver(View view) {
        makePhoneCall("4161234567"); //Current version Just for finish the process, later I will search the related phone number from the database then make the call

    }
    private void callNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
    public void makePhoneCall(String PhoneNo){
        String phoneNumber = PhoneNo;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {


            // Request the CALL_PHONE permission if not granted
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {

            callNumber(phoneNumber);
        }
    }

    public void Reset(View view) {

    }
}



