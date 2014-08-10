package net.louage.bijoux.model;

import java.util.ArrayList;
import java.util.Date;

import android.location.Address;

public class Tour{
	private int tour_id;
	private Date date;
	private Date time;
	private ArrayList <Tracking> trackings;
	private User user;
	private Vehicle vehicle;
	private Double seat_price;
	private Address fromAddress;
	private Address toAddress;
	private ArrayList<Seat> seats;
	
	public static final String TAG_ID = "_id";
	public static final String TAG_DATE = "date";
	public static final String TAG_TIME = "time";
	public static final String TAG_USER_ID = "user_id";
	public static final String TAG_VEHICLE_ID = "vehicle_id";
	public static final String TAG_SEAT_PRICE = "seat_price";
	public static final String TAG_UPDATED_AT = "update_at";
	public static final String TAG_FROM_ADDRESS = "from_address";
	public static final String TAG_FROM_POST_CODE = "from_post_code";
	public static final String TAG_FROM_CITY = "from_city";
	public static final String TAG_FROM_COUNTRY = "from_country";
	
	public static final String TAG_TO_ADDRESS = "to_address";
	public static final String TAG_TO_POST_CODE = "to_post_code";
	public static final String TAG_TO_CITY = "to_city";
	public static final String TAG_TO_COUNTRY = "to_country";
	public static final String TAG_SEATS = "seats";

	public void setTour_id(int tour_id) {
		this.tour_id = tour_id;
	}

	public int getTour_id() {
		return tour_id;
	}
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public ArrayList<Tracking> getTrackings() {
		return trackings;
	}

	public void setTrackings(ArrayList<Tracking> trackings) {
		this.trackings = trackings;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Double getSeat_price() {
		return seat_price;
	}

	public void setSeat_price(Double seat_price) {
		this.seat_price = seat_price;
	}

	public Address getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(Address fromAddress) {
		this.fromAddress = fromAddress;
	}

	public Address getToAddress() {
		return toAddress;
	}

	public void setToAddress(Address toAddress) {
		this.toAddress = toAddress;
	}

	public ArrayList<Seat> getSeats() {
		return seats;
	}

	public void setSeats(ArrayList<Seat> seats) {
		this.seats = seats;
	}

}
