# FallAlert: Intelligent Fall Detection System

A mobile application that leverages machine learning and sensor data to provide real-time fall detection and emergency assistance for elderly individuals and workers in hazardous environments.

> This work was completed as part of the course ENGG*6400 Mobile Devices App Development (Summer 2024) at the University of Guelph, receiving a grade of 92/100.

## Features

- Real-time fall detection using accelerometer and gyroscope data
- Machine learning-based fall detection using TensorFlow Lite
- Emergency contact system for immediate assistance
- Automated emergency calls in case of detected falls
- Circular buffer implementation for efficient sensor data processing

## Technology Stack

- Android (Java)
- TensorFlow Lite for machine learning inference
- Android Sensor Framework
- Gradle build system

## Project Structure

```
app/
├── src/main/
│   ├── assets/                 # ML model files
│   ├── java/
│   │   └── engg6400/project/fallalertnn/
│   │       ├── MainActivity.java       # Main application interface
│   │       ├── TFLiteHelper.java      # ML model integration
│   │       ├── CircularBuffer.java    # Sensor data management
│   │       └── PhoneCall.java         # Emergency call handling
│   └── res/                    # Android resources
```

## Prerequisites

- Android Studio Arctic Fox or newer
- Android SDK 21 or higher
- Android device with accelerometer and gyroscope sensors

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/ombhosale2510/FallAlert.git
   ```
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on your device

## Usage

1. Launch the application
2. Grant necessary permissions (phone calls, sensors)
3. Configure emergency contacts in the setup
4. The app will run in the background monitoring for falls
5. In case of a fall detection:
   - Visual and audio alerts will be triggered
   - Emergency contacts will be notified
   - Automatic emergency calls if no response

## Machine Learning Model

The app uses a TensorFlow Lite model trained on accelerometer and gyroscope data to detect falls. The model files are located in the `assets` folder:
- `fall_detection_model.tflite`: Primary model
- `simple_model.tflite`: Lightweight alternative model

## Contributing

While this was developed as a course project, contributions are welcome:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- University of Guelph, School of Engineering
- Course: ENGG*6400 Mobile Devices App Development
- Professor and TAs for their guidance
