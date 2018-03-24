package info.curtbinder.reefangel.phone;

import android.app.Activity;
import android.content.Intent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by binder on 3/6/15.
 */
public class Utils {

    public final static int THEME_DARK = 0;
    public final static int THEME_LIGHT = 1;
    private static int iTheme = THEME_LIGHT;

    public static int getCurrentTheme() { return iTheme; }

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, int theme)
    {
        iTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (iTheme)
        {
            default:
            case THEME_LIGHT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_DARK:
//                activity.setTheme(R.style.AppThemeDark);
                break;
        }
    }
    public static void onActivityCreateSetTheme(Activity activity, int theme) {
        iTheme = theme;
        onActivityCreateSetTheme(activity);
    }

    public static SimpleDateFormat getDefaultDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }

    public static DateFormat getOldDefaultDateFormat() {
        return DateFormat.getDateTimeInstance( DateFormat.DEFAULT,
                DateFormat.DEFAULT,
                Locale.getDefault() );
    }

    public static String getDisplayDate(String dbDateFormat) {
        // Assign the return value to be the date given
        // If there is a problem converting the date, just return the given date
        String displayDate = dbDateFormat;
        SimpleDateFormat dftProper = Utils.getDefaultDateFormat();
        Date d;
        try {
            // Parse the universal standard date format that is stored in the DB
            d = dftProper.parse(dbDateFormat);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            DateFormat dftDisplay = Utils.getOldDefaultDateFormat();
            // Convert to the display date format
            displayDate = dftDisplay.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return displayDate;
    }
}
