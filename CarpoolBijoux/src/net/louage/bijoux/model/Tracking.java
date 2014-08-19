package net.louage.bijoux.model;

import java.io.Serializable;
import java.util.Date;

public class Tracking implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TRACKING = "tracking";
	private int tracking_id;
	private Date track_date_time;
	private int tour_id;
	private double latitude;
	private double longitude;
	private double accuracy;
	private double altitude;
	private double speed;
	private int cloud_id;
	
	public Tracking() {
		super();
	}
	public int getTracking_id() {
		return tracking_id;
	}
	public void setTracking_id(int tracking_id) {
		this.tracking_id = tracking_id;
	}
	public Date getTrack_date_time() {
		return track_date_time;
	}
	public void setTrack_date_time(Date track_date_time) {
		this.track_date_time = track_date_time;
	}
	public int getTour_id() {
		return tour_id;
	}
	public void setTour_id(int tour_id) {
		this.tour_id = tour_id;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public int getCloud_id() {
		return cloud_id;
	}
	public void setCloud_id(int cloud_id) {
		this.cloud_id = cloud_id;
	}
}
