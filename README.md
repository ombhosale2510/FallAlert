# FallAlert: Mobile Application for Real-Time Fall Detection and Assistance

FallAlert is a mobile application that provides real-time fall detection and emergency assistance for elderly individuals and workers in hazardous environments.

Getting Started:
----
Prerequisites
* Android Studio
* An Android device or emulator


Installation
----
Clone the repository:
* bash
Copy code
git clone [https://github.com/yourusername/fallalert.git](https://github.com/ombhosale2510/FallAlert.git)

Open the project in Android Studio:
----
* Start Android Studio and select "Open an existing Android Studio project".
* Navigate to the cloned repository and select the project folder.

Build the project:
----
* Click on "Build" in the menu and select "Make Project".

Run the app:
----
* Connect your Android device or start an emulator.
* Click on "Run" in the menu and select "Run 'app'".

Project Structure
----
* MainActivity.java: The main activity that displays sensor data (accelerometer and gyroscope) and includes buttons for calling a caregiver, calling emergency services, and accessing the setup screen.
* activity_main.xml: The layout file for the main activity, containing a sensor data display box and three buttons.
* SettingsActivity.java: The activity for customizing emergency contacts and other settings (to be implemented).

Current Status
----
* Setup Complete: The project structure and initial setup are complete.
* Main Screen: The main screen displays accelerometer and gyroscope data and includes buttons for emergency actions.
* Buttons: The buttons for calling a caregiver, calling emergency services, and accessing setup are in place but not yet functional.

Next Steps
----
* Implement Button Functionality: Add functionality to the buttons for calling emergency contacts and accessing setup.
* Fall Detection Algorithm: Develop and integrate the fall detection algorithm using accelerometer and gyroscope data.
* Customizable Settings: Implement the settings screen for customizing emergency contacts and other preferences.
* Machine Learning Integration: Incorporate a machine learning model to improve fall detection accuracy.

<!-- + 
[//]:  <>  Contributing


Contributions are welcome! Please feel free to submit a pull request or open an issue to discuss potential improvements and additions.


+ -->
License
----
This project is licensed under the MIT License - see the LICENSE file for details.
