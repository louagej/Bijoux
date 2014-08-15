package net.louage.bijoux.constants;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.text.format.Time;
import android.util.Log;

public abstract class DateTime {
	
	/**
	 * @param date
	 * @return String
	 * This method to return a String that can be saved as a date in SQLite database with the format yyyy-MM-dd.
	 * The Date represents the date in in local time settings, the returned String is converted to the UTC date
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getStrSQLiteDateStamp(Date date) {
		String sqliteDate;
		sqliteDate = "unknown";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (simpleDateFormat != null) {
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			sqliteDate = simpleDateFormat.format(date);
		}
		return sqliteDate;
	}
	
	/**
	 * @param date
	 * @return String
	 * This method to return a String that can be saved as a date in SQLite database with the format yyyy-MM-dd.
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getStrDateStamp(Date date) {
		String stringDate;
		stringDate = "unknown";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (simpleDateFormat != null) {
			stringDate = simpleDateFormat.format(date);
		}
		return stringDate;
	}
	
	/**
	 * @param Date
	 * @return String
	 * This method to return a String that can be saved as a date in SQLite database with the format HH:mm:SS.
	 * The time represents the date in UTC offset.
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getStrSQLiteTimeStamp(Date date) {
		String sqliteDate;
		sqliteDate = "unknown";
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
		if (simpleTimeFormat != null) {
			simpleTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			sqliteDate = simpleTimeFormat.format(date);
		}
		return sqliteDate;
	}
	
	/**
	 * @param date
	 * @return String
	 * This method to return a String that can be saved as a date in SQLite database with the format yyyy-MM-dd HH:mm:ss.
	 * The Date represents the date in in local time settings, the returned String is converted to the UTC date
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getStrSQLiteDateTimeStamp(Date date) {
		String sqliteDate;
		sqliteDate = "unknown";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (simpleDateFormat != null) {
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			sqliteDate = simpleDateFormat.format(date);
		}
		return sqliteDate;
	}

	/**
	 * @param String
	 * @return Date
	 * This method returns a Date with the format yyyy-MM-dd.
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date getDateFormString(String stringDate) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//sdf.setTimeZone(TimeZone.getDefault());
			date = sdf.parse(stringDate);
		} catch (ParseException e) {
			e.printStackTrace();
		};
		return date;
	}
	
	/**
	 * @param String
	 * @return Date
	 * This method returns a Date with the format HH:mm:ss.
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date getTimeFormString(String stringTime) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			//sdf.setTimeZone(TimeZone.getDefault());
			date = sdf.parse(stringTime);
		} catch (ParseException e) {
			e.printStackTrace();
		};
		return date;
	}
	
	/**
	 * @param String
	 * @return Date
	 * This method returns a Date that can be saved as a date in SQLite database with the format yyyy-MM-dd.
	 * The string represents the date in UTC offset, the returned Date is converted to the local date settings of the device
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date getDateSQLiteString(String sqliteDate) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//sdf.setTimeZone(TimeZone.getDefault());
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			date = sdf.parse(sqliteDate);
		} catch (ParseException e) {
			e.printStackTrace();
		};
		return date;
	}
	
	/**
	 * @param String
	 * @return Date
	 * This method returns a Date that can be saved as a date in SQLite database with the format HH:mm:ss.
	 * The string represents the time in UTC offset, the returned Date is converted to the local date settings of the device
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date getTimeSQLiteString(String sqliteTime) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			//sdf.setTimeZone(TimeZone.getDefault());
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			date = sdf.parse(sqliteTime);
		} catch (ParseException e) {
			e.printStackTrace();
		};
		return date;
	}

	/**
	 * @param String
	 * @return Date
	 * This method to returns a Date timestamp that that was retrieved from a SQLite database in the format yyyy-MM-dd HH:mm:ss.
	 * The string represents the date in UTC offset, the returned Date is converted to the local date settings of the device
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date getDateTimeSQLiteString(String sqliteDate) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//sdf.setTimeZone(TimeZone.getDefault());
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			date = sdf.parse(sqliteDate);
		} catch (ParseException e) {
			e.printStackTrace();
		};
		return date;
	}
	
	/**
	 * @param date
	 * @return String
	 * This method to return a medium String of a date formatted in local date settings.
	 */
	public static String dateToStringMediumFormat(Date date) {
		DateFormat mediumdate = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
		return mediumdate.format(date);
	}
	
	/**
	 * @param dateStringMediumFormat
	 * @return String
	 * This method to return a medium String of a date formatted in local date settings.
	 * @throws ParseException 
	 */
	public static Date stringToDateMediumFormat(String dateStringMediumFormat) throws ParseException {
		DateFormat mediumFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
		Date dateMedium = mediumFormat.parse(dateStringMediumFormat);
		System.out.println(dateMedium);
		return dateMedium;
	}
	
	/**
	 * @param date
	 * @return String
	 * This method to return a short String of a date formatted in local date settings.
	 */
	public static String getDateMediumFormat(Date date) {
		DateFormat shortdate = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
		return shortdate.format(date);
	}
	
	/**
	 * @param date
	 * @return String
	 * This method to return a short String of a date formatted in local date settings.
	 */
	public static String getDateShortFormat(Date date) {
		DateFormat shortdate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
		return shortdate.format(date);
	}
	
	/**
	 * @param date
	 * @return String
	 * This method to return a long String of a date formatted in local date settings.
	 */
	public static String getDateLongFormat(Date date) {
		DateFormat longdate = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
		return longdate.format(date);
	}
	
	
	/**
	 * @param date
	 * @return String
	 * This method Starts with a Date object that is set in UTC time.
	 * This is very common when you're querying a database to receive a date in UTC time.
	 * To convert this object, we first compare the locale time zone to the UTC.
	 * With the difference between the two time zones, we build a new Calendar object.
	 * This Calendar object then is converted in to a String that is formatted according the default locale settings
	 */
	public static String getLocaleTimeDefaultFormat(Date utcDateTime) {
		//Android documentation
		//Construct a Time object in the time zone named by the string argument "time zone".
		Time utc = new Time("UTC");
		//Construct a Time object in the default time zone.
		Time time = new Time();
		//Compare two Time objects and return a negative number if a is less than b,
		// a positive number if a is greater than b, or 0 if they are equal.
		int diff = Time.compare(utc, time);
		//Calculate difference between
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(utcDateTime);
		//Add the differences between two time zones to the hours of the calendar
		calendar.add(Calendar.HOUR_OF_DAY, diff);
		//Get a default representation of the time
		DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.getDefault());
		//Create a string from time in the default locale settings
		String formattedLocaleTime = timeFormatter.format(calendar.getTime());
		return formattedLocaleTime;
	}
	
	/**
	 * @param String
	 * @return Date
	 * This method Starts with a String formatted as HH:mm:ss representing the local time.
	 * This is very common when you're writing to a database, you need to set time in UTC time.
	 * To convert the String, we first compare the locale time zone to the UTC.
	 * With the difference between the two time zones, we build a new Calendar object.
	 * This Calendar object then is converted in to a String that is formatted according the default locale settings
	 * @throws ParseException 
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date getUTCTimeDefaultFormat(String localDateTime) throws ParseException {
		//Android documentation
		//Construct a Time object in the time zone named by the string argument "time zone".
		Time utc = new Time("UTC");
		//Construct a Time object in the default time zone.
		Time time = new Time();
		//Compare two Time objects and return a negative number if a is less than b,
		// a positive number if a is greater than b, or 0 if they are equal.
		int diff = Time.compare(time, utc);
		//Parse the locale time to a Date Object
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date date = sdf.parse(localDateTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		//Add the differences between two time zones to the hours of the calendar
		calendar.add(Calendar.HOUR_OF_DAY, diff);
		//Return the date that was calculated in the calendar
		return calendar.getTime();
	}
	
	/**
	 * @param date
	 * @return String
	 * This method Starts with a Date object that is set in local time and returns a string in UTC time
	 * This is very common when you're writing to a database, you need to set time in UTC time.
	 * To convert this object, we first compare the locale time zone to the UTC.
	 * With the difference between the two time zones (daylight saving time taken into account), we build a new Calendar object.
	 * This Calendar object then is converted in to a String that is formatted according the default locale settings
	 */
	public static String getUTCTimeDefaultFormat(Date localeDateTime) {
		//Android documentation
		Time utc = new Time("UTC");
		//Construct a Time object in the default time zone.
		Time time = new Time();
		//Compare two Time objects and return a negative number if a is less than b,
		// a positive number if a is greater than b, or 0 if they are equal.
		int diff = Time.compare(time, utc)+time.isDst;
		//Calculate difference between
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(localeDateTime);
		//Add the differences between two time zones to the hours of the calendar
		calendar.add(Calendar.HOUR_OF_DAY, diff);
		//Get a default representation of the time
		DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.getDefault());
		//Create a string from time in the default locale settings
		String formattedUTCTime = timeFormatter.format(calendar.getTime());
		Log.d("getUTCTimeDefaultFormat formattedUTCTime: ", formattedUTCTime);
		return formattedUTCTime;
	}
	
}