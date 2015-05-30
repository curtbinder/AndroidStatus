# Android Status
This application is designed to communicate with the Reef Angel Controller. It gives you complete control over your controller. You are able to get the status of your controller from the Controller itself or from the Reef Angel Portal. *Note: The WIFI module must be installed on the controller in order to use this app.*
Communicating with the Portal limits you to only being able to monitor the status. This is due to the limitations of the Portal. The Portal only allows you to get data from it. It does not allow you to send commands to it and have it relay the commands to the Controller. If you desire to control your controller from this app, you must communicate with it directly.
If you are communicating with your controller directly, in addition to being able to get the status, you will be able to:
- Update Internal Memory values
- Update the date and time
- Enter/Exit Feeding and Water Change modes
- Toggle ports ON/OFF
- Change values and modes for: Dimming (Standard and 16 channel), Vortech, Aqua Illuminations, DC Pump, Radion, I/O, Custom Variables
- Clear the ATO and/or Overheat Flags
- Launch the calibration modes for:  PH, PH Expansion, Salinity, ORP, Water Level


## Requirements
A WIFI module installed on the controller is required. Without the module, there is no way to communicate with the controller.
This application will run on Android 4.0 or later. If you have an older device, you cannot run the most recent version of this application. You will only be able to install version 1.0 or earlier. The older versions of the software are available at:  http://curtbinder.info/apps/

## Main Screen
### Status
#### Flags
### Memory
### Notifications
### History
### Errors
### Date and Time

## Preferences
### Profiles
- **Device** - Selection between Controller or Portal
- **Device Authentication Username** - The username used for connecting to your controller. This is enabled in the code installed on your controller by using the following code (placed in your setup section and automatically added by the Wizard if enabled): `ReefAngel.Wifi.WifiAuthentication("username:password");`
- **Device Authentication Password** - The password used for connecting to your controller. See above for how to enable it.
- **Selected Profile** - Selection between Home and Away profiles.
- **Home Host** - IP or URL of your controller. This is typically either your local IP address or a dynamic dns name.
*Note: Enter the URL or IP only, do not enter http://*
If you are using the Reef Angel Dynamic DNS, you will have something like this this in the setup section of your code (it will be automatically added by the Wizard if enabled and the text will be different based on what you chose): `ReefAngel.DDNS("home");`
The Host that will be entered eitehr in the Home or Away host will be this: `username-home.myreefangel.com`  The username is your forum username and home is what you have listed in the DDNS line in your code. This example uses home (like displayed above).
- **Home Port** - The port the controller is set to listen on.
*Default: 2000*
- **Away Host** - IP or URL of your controller. This is typically either your public IP or dynamic dns name.
*Note: Enter the URL or IP only, do not enter http://*
- **Away Port** - The port the controller is set to listen on.
*Default: 2000*
- **User ID** - Your portal username. This is used to download your labels from the Portal. It is also used if you have Portal chosen as your device.

### Controller
- **Download** - Downloads all labels you have configured in the Portal using the User ID that you specified. The username/User ID that will be used to download the labels is specified in the summary text on the option.
- **Controller**
	- **Labels** - Allows you to set/update the labels for all the parameters that are associated with the base controller. The labels are: 
> Temp Sensor 1, Temp Sensor 2, Temp Sensor 3, PH, Daylight Channel, Actinic Channel, ATO Low, ATO High, Salinity, ORP, PH Expansion, Water Level, Water Level 1, Water Level 2, Water Level 3, Water Level 4, Humidity
	- **Visibility** - Allows you to toggle which parameters are visible on the Controller page of the app. The options are: 
> Temp Sensor 2, Temp Sensor 3, PH, Daylight Channel, Actinic Channel, ATO Low, ATO High, Salinity, ORP, PH Expansion, Water Level, Water Level 1, Water Level 2, Water Level 3, Water Level 4, Humidity
	- *Note: Temp Sensor 1 is not allowed to be hidden. It is always visible.*
- **Main Relay**
	- **Labels** - This allows you to set/update the labels for the Relay Ports on the Main/Primary relay.
	- **Enabled Ports** - This allows you to Enable/Disable individual Relay Ports for the Main/Primary relay. If you Disable (un-check) a Relay Port, you will not be able to toggle the status of the port from the app. You will will be able to monitor the status but not able toggle the ports status. This is useful if you have a port that you never want turned on or off and you do not want to worry about accidentally turning it on or off. 
An example would be if you had your return pump plugged into a port and wanted to ensure that you would never turn it off accidentally, you could disable the port that it is connected to and it could never be turned off from the app.
- **Dimming**
 	- **Enable** - This option enables the Dimming expansion module. *Note: This option is overridden if you have the Auto Update Modules option selected under the Advanced Settings.*
 	- **Label Settings** - This allows you to set/update the labels for the dimming channels.
- **16 Ch Dimming**
	- **Enable** - This option enables the 16 channel dimming expansion module. *Note: This option is overridden if you have the Auto Update Modules option selected under the Advanced Settings.*
	- **Label Settings** - This allows you to set/update the labels for the dimming channels.
- **Radion** - This option enables the Radion expansion module. *Note: This option is overridden if you have the Auto Update Modules option selected under the Advanced Settings.*
- **Vortech** - This option enables the Vortech expansion module. *Note: This option is overridden if you have the Auto Update Modules option selected under the Advanced Settings.*
- **DC Pump** - This option enables the DC Pump control. *Note: This option is overridden if you have the Auto Update Modules option selected under the Advanced Settings.*
- **Aqua Illuminations** - This option enables the Aqua Illumincations expansion module. *Note: This option is overridden if you have the Auto Update Modules option selected under the Advanced Settings.*
- **I/O**
	- **Enable** - This option enables the I/O expansion module. *Note: This option is overridden if you have the Auto Update Modules option selected under the Advanced Settings.*
	- **Label Settings** - This allows you to set/update the labels for the I/O channels.
- **Custom Variables**
	- **Enable** - This option enables the Custom Variables.
	- **Label Settings** - This allows you to set/update the labels for the Custom Variables.
- **Relay**
	- **Quantity** - Allows you to select how many expansion relay boxes are installed. *Note: This option is overridden if you have the Auto Update Modules option selected under the Advanced Settings.*
	*Default: None*
	- **Expansion Labels** - This allows you to select the Expansion Relay and then subsequently update the Labels for each of the Ports for the Relays. All 8 Expansion Relays are listed and each Relay has all 8 Labels listed under it. *Note: These Labels are updated and replaced with the Labels from the Portal if the Download Labels option is chosen and executed.*
	- **Expansion Enabled Ports** - This allows you to Enable/Disable individual Relay Ports for any of the 8 Expansion Relay Ports. All 8 Expansion Relays are listed and each Relay has all 8 Ports listed under it. This functions exactly like the Enabled Ports for the Main Relay.
	- **Old Expansion** - This option enables the way the expansion relays were handled in the v0.8.5.X libraries. This is normally not needed but still remains for those who are using very old libraries.
	*Default: Un checked*
- **Relay Settings**
	- **Reset Labels** - Resets ALL labels back to their default values.
	- **Reset Enabled Ports** - Resets ALL Enabled/Disabled ports back to their default values. 
	*Default: Enabled for all ports*

### Automatic Updating
- **Interval** - The frequency the app will query the device. *Default: 15 minutes*
- **Profile** - The profile that you want the automatic updating to occur on. If you have it chosen to only update on either the Home or Away profile, it will ONLY update when the chosen profile is selected in the app. The currently selected profile is displayed in the app toolbar. If you have Always selected, it will always automatically update update no matter what profile you have selected.
*Default: Only on Away*

### Advanced
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
### Notifications
- **Enable** - This option enables notifications in the app. The notifications are specifically parameter notifications that are defined in the Notifications section of the app. Also note that errors and parameter notifications use the same alert ringtone.
*Default: Checked*
- **Choose Ringtone** - This option allows you to chose a custom ringtone for errors and parameter notifications.
*Default: Default notification sound*
- **Error Retries** - This option allows you to specify how many times the app will try to communicate with the controller before notifying you of an error. Sometimes there can be delays or problems communicating with the controller and an error occurs. If you immediately try to communicate with the controller, it will succeed without an error. Instead of always being notified of these errors, you can specify that you want the app to retry up to the specified number of times before you get alerted to an error. 
*Default: Never*
- **Error Retry Interval** - This option allows you to specify how much time must elapse before the app tries to communicate with the controller after a failed attempt. This option is directly related to the Error Retries option mentioned above.
*Default: 5 seconds*

### Logging
- **Enable** - This option enables application logging. If you are encountering errors while communicating with the controller, you should enable this option to help determine the exact problems.
*Default: Un checked*
- **File Updating** - This option lets you specify how the log file is updated. If Replace file is selected, only the last error received will be logged. If Append file is selected, each error will be added to the end of the log file. This option can allow for a large log file if lots of errors occur.
*Default: Replace file*
- **Display log file** - This option displays the application log file inside the app.
- **Send log file** - This option allows you to email the application log file to the developer. You must have an email account configured on your device in order to email the log file.
- **Delete log file** - This option allows you to delete the application log file.

### Application Info
>Displays the application information: Version, Author, Contributors, Copyright, and License.

- **Changelog** - Displays the application changelog.
- **Reef Angel Controller** - Launches the default web browser and displays the main page:  http://www.reefangel.com/
- **Reef Angel Forums** - Launches the default web browser and displays the forum: http://forum.reefangel.com/

###### Copyright (c) 2015 Curt Binder