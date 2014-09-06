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
	 * This method to return a String with the format yyyy-MM-dd.
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
	 * @param date
	 * @return String
	 * This method to return a String with the format HH:mm:ss.
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getStrTimeStamp(Date date) {
		String stringDate;
		stringDate = "unknown";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		if (simpleDateFormat != null) {
			stringDate = simpleDateFormat.format(date);
		}
		return stringDate;
	}
	
	/**
	 * @param date
	 * @return String
	 * This method to return a String with the format HH:mm.
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getStrTimeStampShort(Date date) {
		String stringDate;
		stringDate = "unknown";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		if (simpleDateFormat != null) {
			stringDate = simpleDateFormat.format(date);
		}
		return stringDate;
	}
	
	/**
	 * @param date
	 * @return String
	 * This method to return a String that can be saved as a date in SQLite database with the format yyyy-MM-dd HH:mm:ss.
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getStrDateTimeStamp(Date date) {
		String sqliteDate;
		sqliteDate = "unknown";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (simpleDateFormat != null) {
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
	 * This method returns a Date with the format HH:mm.
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date getTimeFormStringShort(String stringTime) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
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
	 * This method to returns a Date timestamp that that was retrieved from a SQLite database in the format yyyy-MM-dd HH:mm:ss.
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date getDateTimeString(String dateTime) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = sdf.parse(dateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		};
		return date;
	}
	
}