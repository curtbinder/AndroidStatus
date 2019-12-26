/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Curt Binder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package info.curtbinder.reefangel.phone;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import info.curtbinder.reefangel.db.RADbHelper;
import info.curtbinder.reefangel.service.MessageCommands;
import info.curtbinder.reefangel.service.UpdateService;
import info.curtbinder.reefangel.service.XMLReadException;

public class RAApplication extends Application {

    private static final String TAG = RAApplication.class.getSimpleName();
    private static final String NUMBER_PATTERN = "\\d+";
    private static final String HOST_PATTERN =
            "^(?i:[[0-9][a-z]]+)(?i:[\\w\\.\\-]*)(?i:[[0-9][a-z]]+)$";
    private static final String USERID_PATTERN = "[\\w\\-\\.\\x20]+";
    @SuppressWarnings("unused")
    private static final String WIFI_LOOKUP =
            "http://forum.reefangel.com/getwifi.php?id=%1$s&pwd=%2$s";

    // Preferences
    public RAPreferences raprefs;

    // Error code stuff
    private String[] errorCodes;
    private String[] errorCodesStrings;
    private String errorCodeMessage;
    public int errorCode;
    public int errorCount;

    public void onCreate() {
        super.onCreate();
        errorCodes = getResources().getStringArray(R.array.errorCodes);
        errorCodesStrings = getResources().getStringArray(R.array.errorCodesStrings);
        errorCodeMessage = ""; // set to no error message
        errorCode = 0; // set to no error initially
        raprefs = new RAPreferences(this);

        // initialize the error count
        errorCount = 0;
    }

    public void onTerminate() {
        super.onTerminate();
    }

    public void restartAutoUpdateService() {
        cancelAutoUpdateService();
        startAutoUpdateService();
    }

    public void cancelAutoUpdateService() {
        PendingIntent pi = getUpdateIntent();
        AlarmManager am =
                (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // cancel the repeating service
        am.cancel(pi);
    }

    public void startAutoUpdateService() {
        // check to see if we need to start the repeating update service
        // grab the service interval, make sure it's greater than 0
        long interval = raprefs.getUpdateInterval();
        if (interval == 0) {
            Log.d(TAG, "disabled autoupdate");
            return;
        }

        int up = raprefs.getUpdateProfile();
        if (raprefs.isCommunicateController()) {
            int p = getSelectedProfile();
            Log.d(TAG, "UP: " + up + " P: " + p);
            if (isAwayProfileEnabled()) {
                if ((up == Globals.profileOnlyAway)
                        && (p != Globals.profileAway)) {
                    // only run on away profile and we are not on away profile
                    Log.d(TAG, "only run on away, not away");
                    return;
                } else if ((up == Globals.profileOnlyHome)
                        && (p != Globals.profileHome)) {
                    // only run on home profile and we are not on home profile
                    Log.d(TAG, "only run on home, not home");
                    return;
                }
            }
        }

        // create a status query message
        PendingIntent pi = getUpdateIntent();
        // setup alarm service to wake up and start the service periodically
        AlarmManager am =
                (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                interval, pi);
    }

    private PendingIntent getUpdateIntent() {
        Intent i = new Intent(this, AlarmReceiver.class);
        i.setAction(MessageCommands.QUERY_STATUS_INTENT);
        i.putExtra(AlarmReceiver.ALARM_TYPE, AlarmReceiver.UPDATE);
        i.putExtra(MessageCommands.AUTO_UPDATE_PROFILE_INT,
                raprefs.getUpdateProfile());
        return PendingIntent.getBroadcast(this, -1, i,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    // Error Logging
    public void clearErrorCode() {
        errorCode = 0;
        errorCodeMessage = "";
    }

    private String getSimpleErrorMessage ( String msg ) {
        String s = "";
        if ( msg.contains( "EHOSTUNREACH" ) ) {
            s = "Host unreachable: " + raprefs.getHost() + ":" + raprefs.getPort();
        } else if ( msg.contains( "ECONNREFUSED" ) ) {
            s = "Connection Refused: " + raprefs.getHost() + ":" + raprefs.getPort();
        } else if ( msg.contains( "ECONNRESET" ) ) {
            s = "Connection Reset by Peer";
        } else {
            s = msg;
        }
        return s;
    }

    public void error(int errorCodeIndex, Throwable t, String msg) {
        errorCode = Integer.parseInt(errorCodes[errorCodeIndex]);
        if ( t.getMessage() != null )
            errorCodeMessage = getSimpleErrorMessage(t.getMessage());
        if ( errorCode == 15 )
            // timeout error
            errorCodeMessage = String.format( Locale.getDefault(),
                    getString(R.string.messageErrorTimeout),
                    raprefs.getHost(), raprefs.getPort());
        Log.d(TAG, "Error: " + errorCode + ", " + errorCodeMessage);

        // if logging enabled, save the log
        if (raprefs.isLoggingEnabled()) {
            if (!hasExternalStorage()) {
                // doesn't have external storage
                Toast.makeText(this,
                        getString(R.string.messageNoExternalStorage),
                        Toast.LENGTH_LONG).show();
                return;
            }
            boolean keepFile = raprefs.isLoggingAppendFile();
            try {
                String sFile = getLoggingFile();
                FileWriter fw = new FileWriter(sFile, keepFile);
                PrintWriter pw = new PrintWriter(fw);
                DateFormat dft = Utils.getOldDefaultDateFormat();
                pw.println(dft.format(Calendar.getInstance().getTime()));
                String s =
                        String.format("Profile: %s\nHost: %s:%s\nUser ID: %s",
                                (getSelectedProfile() == 1) ? "Away"
                                        : "Home",
                                raprefs.getHost(), raprefs.getPort(),
                                raprefs.getUserId()
                        );
                pw.println(s);
                pw.println(msg);
                if ( t instanceof XMLReadException) {
                    // we have an XML read exception, get the xml data if any
                    pw.println( ((XMLReadException) t).getXmlData() );
                }
                pw.println(t.toString());
                pw.println("Stack Trace:");
                pw.flush();
                t.printStackTrace(pw);
                pw.println("----");
                pw.flush();
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getErrorMessage() {
        String s = getString(R.string.messageUnknownError);
        // loop through array of error codes and match with the current code
        for (int i = 0; i < errorCodes.length; i++) {
            if (Integer.parseInt(errorCodes[i]) == errorCode) {
                // found code
                s = String.format(Locale.US, "%s %d: %s", getString(R.string.messageError),
                        errorCode,
                        (errorCodeMessage == "" ) ? errorCodesStrings[i]
                                                : errorCodeMessage);
                break;
            }
        }
        return s;
    }

    public boolean canErrorRetry() {
        boolean f = false;
        if (errorCount <= raprefs.getNotificationErrorRetryMax()) {
            f = true;
        }
        return f;
    }

    public void clearErrorRetryCount() {
        errorCount = Globals.errorRetryNone;
    }

    public void increaseErrorCount() {
        errorCount++;
    }

    public String getLoggingDirectory() {
        String s =
                "" + Environment.getExternalStorageDirectory()
                        + Environment.getDataDirectory() + "/"
                        + Globals.PACKAGE + "/";
        return s;
    }

    public String getLoggingFile() {
        return getLoggingDirectory() + Globals.loggingFile;
    }

    public boolean isLoggingFilePresent() {
        return isFilePresent(getLoggingFile());
    }

    public void deleteLoggingFile() {
        File l = new File(getLoggingFile());
        if (l != null && l.exists())
            l.delete();
    }

    public boolean hasExternalStorage() {
        boolean f = false;
        File path = new File(getLoggingDirectory());
        path.mkdirs();
        File file = new File(path, "test.txt");
        file.mkdirs();
        if (file != null) {
            if (file.exists()) {
                f = true;
                file.delete();
            }
        }
        return f;
    }

    public boolean isFilePresent(String fileName) {
        boolean f = false;
        File l = new File(fileName);
        if ((l != null) && (l.exists()))
            f = true;
        return f;
    }

    public String getDateConversionFile() {
        return getLoggingDirectory() + Globals.dateConversionFile;
    }

    private boolean isDateConversionFilePresent() {
        return isFilePresent(getDateConversionFile());
    }

    public PrintWriter openDateConversionFile() {
        if (!hasExternalStorage()) {
            Log.d(TAG, "No external storage available");
            return null;
        }
        PrintWriter pw = null;
        if (!isDateConversionFilePresent()) {
            // file not present
            Log.d(TAG, "Date Conversion file not present, creating it");
        }
        try {
            String sFile = getDateConversionFile();
            FileWriter fw = new FileWriter(sFile, true);
            pw = new PrintWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pw;
    }

    public void closeDateConversionFile(PrintWriter pw) {
        if ( pw != null ) {
            pw.flush();
            pw.close();
        }
    }

    public void logRepeatMsgDateConversion(PrintWriter pw, String msg) {
        if ( pw != null ) {
            DateFormat dft = Utils.getOldDefaultDateFormat();
            pw.println(dft.format(Calendar.getInstance().getTime()));
            pw.println(msg);
        }
    }

    public void exportDatabase() {
        File path = getDatabasePath(RADbHelper.DB_NAME);
        File exportedDb = new File(getLoggingDirectory(), RADbHelper.DB_NAME + "3");
        try {
            copy(path, exportedDb);
            Toast.makeText(this, R.string.messageExportSuccessful, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, R.string.messageError, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "Exported DB: " + exportedDb.getAbsolutePath());
    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    private boolean isNumber(Object value) {
        if ((!value.toString().equals(""))
                && (value.toString().matches(NUMBER_PATTERN))) {
            return true;
        }
        return false;
    }

    public boolean validateHost(Object host) {
        // host validation here
        String h = host.toString();

        // Hosts must:
        // - not start with 'http://'
        // - only contain: alpha, number, _, -, .
        // - end with: alpha or number

        if (!h.matches(HOST_PATTERN)) {
            // invalid host
            Toast.makeText(this,
                    this.getString(R.string.prefHostInvalidHost)
                            + ": " + host.toString(),
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }
        return true;
    }

    public boolean validatePort(Object port) {
        if (!isNumber(port)) {
            // not a number
            Toast.makeText(this,
                    getString(R.string.messageNotNumber) + ": "
                            + port.toString(), Toast.LENGTH_SHORT
            )
                    .show();
            return false;
        } else {
            // it's a number, verify it's within range
            // TODO: convert min & max ports to int value defines
            int min = Integer.parseInt(getString(R.string.prefPortMin));
            int max = Integer.parseInt(getString(R.string.prefPortMax));
            int v = Integer.parseInt((String) port.toString());

            // check if it's less than the min value or if it's greater than
            // the max value
            if ((v < min) || (v > max)) {
                Toast.makeText(this,
                        getString(R.string.prefPortInvalidPort)
                                + ": " + port.toString(),
                        Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        }
        return true;
    }

    public boolean validateUser(Object user) {
        String u = user.toString();
        if (!u.matches(USERID_PATTERN)) {
            // invalid userid
            Toast.makeText(this,
                    getString(R.string.prefUserIdInvalid) + ": "
                            + user.toString(), Toast.LENGTH_SHORT
            )
                    .show();
            return false;
        }
        return true;
    }

    public String getPWMOverrideChannelName(int channel) {
        String name = "";
        switch ( channel ) {
            default:
                name = getString(R.string.labelChannel);
                break;
            case Globals.OVERRIDE_DAYLIGHT:
                name = raprefs.getControllerLabel(Globals.DP_INDEX);
                break;
            case Globals.OVERRIDE_ACTINIC:
                name = raprefs.getControllerLabel(Globals.AP_INDEX);
                break;
            case Globals.OVERRIDE_CHANNEL0:
            case Globals.OVERRIDE_CHANNEL1:
            case Globals.OVERRIDE_CHANNEL2:
            case Globals.OVERRIDE_CHANNEL3:
            case Globals.OVERRIDE_CHANNEL4:
            case Globals.OVERRIDE_CHANNEL5:
                name = raprefs.getDimmingModuleChannelLabel(channel - Globals.OVERRIDE_CHANNEL0);
                break;
            case Globals.OVERRIDE_AI_WHITE:
                name = getString( R.string.labelAI ) + " " + getString( R.string.labelWhite );
                break;
            case Globals.OVERRIDE_AI_ROYALBLUE:
                name = getString( R.string.labelAI ) + " " + getString( R.string.labelRoyalBlue );
                break;
            case Globals.OVERRIDE_AI_BLUE:
                name = getString( R.string.labelAI ) + " " + getString( R.string.labelBlue );
                break;
            case Globals.OVERRIDE_RF_WHITE:
                name = getString( R.string.labelRadion ) + " " + getString( R.string.labelWhite );
                break;
            case Globals.OVERRIDE_RF_ROYALBLUE:
                name = getString( R.string.labelRadion ) + " " + getString( R.string.labelRoyalBlue );
                break;
            case Globals.OVERRIDE_RF_RED:
                name = getString( R.string.labelRadion ) + " " + getString( R.string.labelRed );
                break;
            case Globals.OVERRIDE_RF_GREEN:
                name = getString( R.string.labelRadion ) + " " + getString( R.string.labelGreen );
                break;
            case Globals.OVERRIDE_RF_BLUE:
                name = getString( R.string.labelRadion ) + " " + getString( R.string.labelBlue );
                break;
            case Globals.OVERRIDE_RF_INTENSITY:
                name = getString( R.string.labelRadion ) + " " + getString( R.string.labelIntensity );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL0:
            case Globals.OVERRIDE_16CH_CHANNEL1:
            case Globals.OVERRIDE_16CH_CHANNEL2:
            case Globals.OVERRIDE_16CH_CHANNEL3:
            case Globals.OVERRIDE_16CH_CHANNEL4:
            case Globals.OVERRIDE_16CH_CHANNEL5:
            case Globals.OVERRIDE_16CH_CHANNEL6:
            case Globals.OVERRIDE_16CH_CHANNEL7:
            case Globals.OVERRIDE_16CH_CHANNEL8:
            case Globals.OVERRIDE_16CH_CHANNEL9:
            case Globals.OVERRIDE_16CH_CHANNEL10:
            case Globals.OVERRIDE_16CH_CHANNEL11:
            case Globals.OVERRIDE_16CH_CHANNEL12:
            case Globals.OVERRIDE_16CH_CHANNEL13:
            case Globals.OVERRIDE_16CH_CHANNEL14:
            case Globals.OVERRIDE_16CH_CHANNEL15:
                name = raprefs.getSCDimmingModuleChannelLabel( channel - Globals.OVERRIDE_16CH_CHANNEL0 );
                break;
        }
        return name;
    }

    // TODO cleanup this function, remove repeated format calls, simplify
    public String getPWMOverrideMessageDisplay ( int channel ) {
        String msg = "";
        String name = getPWMOverrideChannelName(channel);
        switch ( channel ) {
            case Globals.OVERRIDE_DAYLIGHT:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefDPTitle) );
                break;
            case Globals.OVERRIDE_ACTINIC:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefAPTitle) );
                break;
            case Globals.OVERRIDE_DAYLIGHT2:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefDP2Title) );
                break;
            case Globals.OVERRIDE_ACTINIC2:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefAP2Title) );
                break;
            case Globals.OVERRIDE_CHANNEL0:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpDimmingCh0LabelTitle));
                break;
            case Globals.OVERRIDE_CHANNEL1:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpDimmingCh1LabelTitle) );
                break;
            case Globals.OVERRIDE_CHANNEL2:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpDimmingCh2LabelTitle) );
                break;
            case Globals.OVERRIDE_CHANNEL3:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpDimmingCh3LabelTitle) );
                break;
            case Globals.OVERRIDE_CHANNEL4:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpDimmingCh4LabelTitle) );
                break;
            case Globals.OVERRIDE_CHANNEL5:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpDimmingCh5LabelTitle) );
                break;
            case Globals.OVERRIDE_AI_WHITE:
            case Globals.OVERRIDE_AI_ROYALBLUE:
            case Globals.OVERRIDE_AI_BLUE:
            case Globals.OVERRIDE_RF_WHITE:
            case Globals.OVERRIDE_RF_ROYALBLUE:
            case Globals.OVERRIDE_RF_RED:
            case Globals.OVERRIDE_RF_GREEN:
            case Globals.OVERRIDE_RF_BLUE:
            case Globals.OVERRIDE_RF_INTENSITY:
                msg = name + " " + getString( R.string.labelChannel );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL0:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh0LabelTitle));
                break;
            case Globals.OVERRIDE_16CH_CHANNEL1:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh1LabelTitle) );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL2:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh2LabelTitle) );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL3:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh3LabelTitle) );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL4:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh4LabelTitle) );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL5:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh5LabelTitle) );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL6:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh6LabelTitle));
                break;
            case Globals.OVERRIDE_16CH_CHANNEL7:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh7LabelTitle) );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL8:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh8LabelTitle) );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL9:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh9LabelTitle) );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL10:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh10LabelTitle) );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL11:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh11LabelTitle) );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL12:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh12LabelTitle));
                break;
            case Globals.OVERRIDE_16CH_CHANNEL13:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh13LabelTitle) );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL14:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh14LabelTitle) );
                break;
            case Globals.OVERRIDE_16CH_CHANNEL15:
                msg = String.format( Locale.getDefault(),
                        getString( R.string.messagePWMPopupCustom),
                        name, getString(R.string.prefExpSCDimmingCh15LabelTitle) );
                break;
        }
        return msg;
    }

    // Preferences
    public boolean isFirstRun() {
        // First run will be determined by:
        // if the first run key is NOT set AND
        // if the host key is NOT set OR if it's the same as the default
        boolean fFirst = raprefs.isFirstRun();

        // if it's already set, no need to compare the hosts
        if (!fFirst) {
            Log.w( TAG, "First run already set" );
            return false;
        }

        // if it's not set (as in existing installations), check the host
        // the host should be set and it should not be the same as the default
        if (!raprefs.isMainHostSet())
            return true;

        // if we have made it here, then it's an existing install where the user
        // has the host set to something other than the default
        // so we will go ahead and clear the first run prompt for them
        raprefs.disableFirstRun();
        return false;
    }

    public void displayChangeLog(AppCompatActivity a) {
        // check version code stored in preferences vs the version stored in
        // running code
        // display the changelog if the values are different
        int previous = raprefs.getPreviousCodeVersion();

        int current = 0;
        try {
            current = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            // safely ignore the error
        }
        if (current > previous) {
            // save code version in preferences
            raprefs.setPreviousCodeVersion(current);
            // newer version, display changelog
            DialogSupportChangelog dlg = new DialogSupportChangelog();
            dlg.show(a.getSupportFragmentManager(), "dlg");
        }
        // deletePref( R.string.prefPreviousCodeVersion );
    }

    // Profiles
    public int getSelectedProfile() {
        return raprefs.getSelectedProfile();
    }

    public void setSelectedProfile(int profile) {
        if (profile > Globals.profileAway)
            return;
        raprefs.setSelectedProfile(profile);
        restartAutoUpdateService();
    }

    public boolean isAwayProfileEnabled() {
        // String host = raprefs.getAwayHost();
        // Log.d( TAG, "isAwayProfileEnabled: " + host );
        // get away host, compare to empty host
        // if host is set, then the profile is enabled
        // if port is not set, that implies default port
        return raprefs.isAwayHostSet();
    }
}
