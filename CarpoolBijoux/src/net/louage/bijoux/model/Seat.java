package net.louage.bijoux.model;

public class Seat {
	private int seat_id;
	private int created_by_user_id;
	private String device_id;
	private int tour_id;
	private int user_id;
	private String status;
	private boolean paid;
	
	public static final String TAG_ID = "_id";
	public static final String TAG_CREATED_BY_USER_ID = "created_by_user_id";
	public static final String TAG_DEVICE_ID = "device_id";
	public static final String TAG_TOUR_ID = "tour_id";
	public static final String TAG_USER_ID = "user_id";
	public static final String TAG_STATUS = "status";
	public static final String TAG_PAID = "paid";
	
	public int getSeat_id() {
		return seat_id;
	}
	public void setSeat_id(int seat_id) {
		this.seat_id = seat_id;
	}
	public int getCreated_by_user_id() {
		return created_by_user_id;
	}
	public void setCreated_by_user_id(int created_by_user_id) {
		this.created_by_user_id = created_by_user_id;
	}
	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	public int getTour_id() {
		return tour_id;
	}
	public void setTour_id(int tour_id) {
		this.tour_id = tour_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isPaid() {
		return paid;
	}
	public void setPaid(boolean paid) {
		this.paid = paid;
	}
	
}
