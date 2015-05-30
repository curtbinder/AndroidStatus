Android Status Preferences
---

#### Profiles
- **Device** - Selection between Controller or Portal
- **Device Authentication Username** - The username used for connecting to your controller. This is enabled in the code installed on your controller by using the `ReefAngel.Wifi.WifiAuthentication(username:password);`
- **Device Authentication Password** - The password used for connecting to your controller. See above for how to enable it.
- **Selected Profile** - Selection between Home and Away profiles.
- **Home Host** - IP or URL of your controller. This is typically either your local IP address or a dynamic dns name
*Note: Enter the URL or IP only, do not enter http://*
- **Home Port** - The port the controller is set to listen on.
*Default: 2000*
- **Away Host** - IP or URL of your controller. This is typically either your public IP or dynamic dns name.
*Note: Enter the URL or IP only, do not enter http://*
- **Away Port** - The port the controller is set to listen on.
*Default: 2000*
- **User ID** - Your portal username. This is used to download your labels from the Portal. It is also used if you have Portal chosen as your device.

#### Controller

#### Automatic Updating
- **Interval** - The frequency the app will query the device. *Default: 15 minutes*
- **Profile** - The profile that you want the automatic updating to occur on. If you have it chosen to only update on either the Home or Away profile, it will ONLY update when the chosen profile is selected in the app. The currently selected profile is displayed in the app toolbar. If you have Always selected, it will always automatically update update no matter what profile you have selected.
*Default: Only on Away*

#### Advanced
- **Pre v1.0 Locations** - This option tells the application to use the memory locations for the ReefAngel Libraries before v1.0.
*Default: Un checked*
- **Auto Update Modules** - This option automatically updates the configuration of the modules of the app based on what the controller reports to it. If you add or remove modules on your controller and have updated the code on it, all you need to do is to refresh the status of the controller and the changes will be reflected in the app.
*Default: Checked*
- **Auto refresh after commands** - This option will automatically refresh the controller status after you issue any command. This includes all the commands from the Commands tab and any of the override commands (ie, PWM, Custom Var, Vortech Mode, etc).
*Default: Checked*
- **Keep Screen On** - This option keeps the screen on and prevents the display from dimming or turning off while you are in the Status section (main section) of the app. This is useful if you have a tablet next to your tank that you use to monitor your controller OR if you have a short timeout on your device before it locks and you are working on your tank.
*Default: Un checked*
- **Connection Timeout** - The timeout value in seconds that must elapse without a response from the controller when trying to connect to it before an error is thrown. This value does not need to be changed under normal or most circumstances. Only change this if you are getting connection errors inside the app.
*Default: 15 seconds*
- **Read Timeout** - The timeout value in seconds that must elapse while trying to read a response from the controller before an error is thrown. The value does not need to be changed under normal or most cirmstances. Only change this if you are getting read errors inside the app.
*Default: 10 seconds*
#### Notifications
- **Enable** - This option enables notifications in the app. The notifications are specifically parameter notifications that are defined in the Notifications section of the app. Also note that errors and parameter notifications use the same alert ringtone.
*Default: Checked*
- **Choose Ringtone** - This option allows you to chose a custom ringtone for errors and parameter notifications.
*Default: Default notification sound*
- **Error Retries** - This option allows you to specify how many times the app will try to communicate with the controller before notifying you of an error. Sometimes there can be delays or problems communicating with the controller and an error occurs. If you immediately try to communicate with the controller, it will succeed without an error. Instead of always being notified of these errors, you can specify that you want the app to retry up to the specified number of times before you get alerted to an error. 
*Default: Never*
- **Error Retry Interval** - This option allows you to specify how much time must elapse before the app tries to communicate with the controller after a failed attempt. This option is directly related to the Error Retries option mentioned above.
*Default: 5 seconds*

#### Logging
- **Enable** - This option enables application logging. If you are encountering errors while communicating with the controller, you should enable this option to help determine the exact problems.
*Default: Un checked*
- **File Updating** - This option lets you specify how the log file is updated. If Replace file is selected, only the last error received will be logged. If Append file is selected, each error will be added to the end of the log file. This option can allow for a large log file if lots of errors occur.
*Default: Replace file*
- **Display log file** - This option displays the application log file inside the app.
- **Send log file** - This option allows you to email the application log file to the developer. You must have an email account configured on your device in order to email the log file.
- **Delete log file** - This option allows you to delete the application log file.

#### Application Info
- Displays the application information: Version, Author, Contributors, Copyright, and License.
- **Changelog** - Displays the application changelog.
- **Reef Angel Controller** - Launches the default web browser and displays the main page:  http://www.reefangel.com/
- **Reef Angel Forums** - Launches the default web browser and displays the forum: http://forum.reefangel.com/

###### Copyright (c) 2015 Curt Binder