package net.louage.bijoux.constants;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;

public abstract class DateTime {

	public static String getStrDateTimeStamp(final Context context) {
		String dateTimeStamp;
		dateTimeStamp = "unknown";
		Date now = new Date();
		java.text.DateFormat dateFormat = android.text.format.DateFormat
				.getDateFormat(context);
		java.text.DateFormat timeFormat = android.text.format.DateFormat
				.getTimeFormat(context);
		if (dateFormat != null && timeFormat != null) {
			dateTimeStamp = dateFormat.format(now) + " "
					+ timeFormat.format(now);
		}
		return dateTimeStamp;
	}

	public static String getStrDateStamp(final Context context) {
		String dateStamp;
		dateStamp = "unknown";
		Date now = new Date();
		java.text.DateFormat dateFormat = android.text.format.DateFormat
				.getDateFormat(context);
		if (dateFormat != null) {
			dateStamp = dateFormat.format(now);
		}
		return dateStamp;
	}

	public static String getStrTimeStamp(final Context context) {
		String timeStamp;
		timeStamp = "unknown";
		Date now = new Date();
		java.text.DateFormat timeFormat = android.text.format.DateFormat
				.getTimeFormat(context);
		if (timeFormat != null) {
			timeStamp = timeFormat.format(now);
		}
		return timeStamp;
	}
	
	public static String getStrUTCDateTimeStamp(final Context context) {
		String dateTimeStamp;
		dateTimeStamp = "unknown";
		Date now = new Date();
		java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
		timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		if (dateFormat != null && timeFormat != null) {
			dateTimeStamp = dateFormat.format(now) + " "
					+ timeFormat.format(now);
		}
		return dateTimeStamp;
	}

	public static String getStrUTCDateStamp(final Context context) {
		String dateStamp;
		dateStamp = "unknown";
		Date now = new Date();
		java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		if (dateFormat != null) {
			dateStamp = dateFormat.format(now);
		}
		return dateStamp;
	}

	public static String getStrUTCTimeStamp(final Context context) {
		String timeStamp;
		timeStamp = "unknown";
		Date now = new Date();
		java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
		timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		if (timeFormat != null) {
			timeStamp = timeFormat.format(now);
		}
		return timeStamp;
	}
	
	public static DateFormat getDateStamp(final Context context) {
		Date now = new Date();
		java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
		dateFormat.format(now);
		return dateFormat;
	}
	
	public static DateFormat getTimeStamp(final Context context) {
		Date now = new Date();
		java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
		timeFormat.format(now);
		return timeFormat;
	}
	
	public static DateFormat getUTCDateStamp(final Context context) {
		Date now = new Date();
		java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		dateFormat.format(now);
		return dateFormat;
	}
	
	public static DateFormat getUTCTimeStamp(final Context context) {
		Date now = new Date();
		java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
		timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		timeFormat.format(now);
		return timeFormat;
	}
	
	public static DateFormat getDateTimeStamp(final Context context) {
		Date now = new Date();
		java.text.DateFormat dateTimeFormat = android.text.format.DateFormat.getDateFormat(context);
		dateTimeFormat = android.text.format.DateFormat.getTimeFormat(context);
		dateTimeFormat.format(now);
		return dateTimeFormat;
	}
	
	public static DateFormat getUTCDateTimeStamp(final Context context) {
		Date now = new Date();
		java.text.DateFormat dateTimeFormat = android.text.format.DateFormat.getDateFormat(context);
		dateTimeFormat = android.text.format.DateFormat.getTimeFormat(context);
		dateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		dateTimeFormat.format(now);
		return dateTimeFormat;
	}
	
	
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
	 * @param String
	 * @return Date
	 * This method to return a Date that can be saved as a date in SQLite database with the format yyyy-MM-dd.
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
	 * This method to return a Date that can be saved as a date in SQLite database with the format yyyy-MM-dd.
	 * The string represents the date in UTC offset, the returned Date is converted to the local date settings of the device
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
	
	
}