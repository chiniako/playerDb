package gw.db.chi.nl.playerdbapp.utils;

import android.util.Log;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrmUtils {

    public static Long dateStringToLong(String baseValue) {
        Long internalDateValue = new Long(0);
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateObj = simpledateformat.parse(baseValue, pos);
        Log.i("sma", "Date obj generated: " +dateObj.toString());
        internalDateValue = dateObj.getTime();
        Log.i("sma", "Date ts generated: " +internalDateValue);
        return internalDateValue;
    }

    public static String dateLongToString(Long baseValue) {
        Date dateObj = new Date(baseValue);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(dateObj);
    }

}
