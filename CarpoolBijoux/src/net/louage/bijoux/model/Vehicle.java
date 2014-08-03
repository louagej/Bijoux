package net.louage.bijoux.model;

import java.io.Serializable;
import java.util.Date;

public class Vehicle implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6304856622638567791L;
	private int vehicle_id;
	private String licenseplate; //License plate code known by governmental instances
	private Country country; //Country where this vehicle is registered
	private int numberOfPassengers; //default number of passengers that can get a lift
	private String brand; //Brand of the vehicle
	private VehicleType type; //Type of vehicle (motorcycle, car, plane,...)
	private int user_id; //links to the user table
	private Date update_at; //When working with interfaces, you can check when the last update was done on this object
	
	public static final String TAG_ID = "_id";
	public static final String TAG_LICENSEPLATE = "licenseplate";
	public static final String TAG_COUNTRY = "country";
	public static final String TAG_NUMBER_OF_PASS = "numberOfPassengers";
	public static final String TAG_BRAND = "brand";
	public static final String TAG_VEHICLE_TYPE = "vehicle_type";
	public static final String TAG_USER_ID = "user_id";
	public static final String TAG_UPDATED_AT = "update_at";
	
	public Vehicle(int vehicle_id, String licenseplate, Country country,
			int numberOfPassengers, String brand, VehicleType type, int user_id, Date update_at) {
		super();
		this.vehicle_id = vehicle_id;
		this.licenseplate = licenseplate;
		this.country = country;
		this.numberOfPassengers = numberOfPassengers;
		this.brand = brand;
		this.type = type;
		this.user_id = user_id;
		this.update_at = update_at;
	}

	public Vehicle() {
		super();
	}

	public int getVehicle_id() {
		return vehicle_id;
	}
	public void setVehicle_id(int vehicle_id) {
		this.vehicle_id = vehicle_id;
	}
	public String getLicenseplate() {
		return licenseplate;
	}
	public void setLicenseplate(String licenseplate) {
		this.licenseplate = licenseplate;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public int getNumberOfPassengers() {
		return numberOfPassengers;
	}
	public void setNumberOfPassengers(int numberOfPassengers) {
		this.numberOfPassengers = numberOfPassengers;
	}
	public VehicleType getType() {
		return type;
	}
	public void setType(VehicleType type) {
		this.type = type;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Date getUpdate_at() {
		return update_at;
	}

	public void setUpdate_at(Date update_at) {
		this.update_at = update_at;
	}

}
