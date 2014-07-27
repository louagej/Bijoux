package net.louage.bijoux.model;

import java.util.ArrayList;
import java.util.Date;

public class User {
	private int user_id;
	public static final String TAG_ID = "_id";
	private String username;
	public static final String TAG_USERNAME = "username";
	private String password;
	private Date activation;
	public static final String TAG_ACTIVATION = "activation";
	private String lastname;
	public static final String TAG_LASTNAME = "lastname";
	private String firstname;
	public static final String TAG_FIRSTNAME = "firstname";
	private String email;
	public static final String TAG_EMAIL = "email";
	private String phone;
	public static final String TAG_PHONE = "phone";
	private String info; //Tells something about this user
	public static final String TAG_INFO = "info";
	private Date update_at; //When working with interfaces, you can check when the last update was done on this object
	public static final String TAG_UPDATED_AT = "update_at";
	private String driverlicense;
	public static final String TAG_DRIVERLICENSE = "driverlicense";
	private ArrayList<Role> roles; //User with Administrator permissions
	public static final String TAG_ROLES = "roles";
	private ArrayList<Team> memberOf; //Team were the user is a member of
	public static final String TAG_MEMBER_OF = "teams";
	private ArrayList<Team> managerOf; //Teams that are managed by the user
	public static final String TAG_MANAGER_OF = "teams";
	private ArrayList<Vehicle> vehicles;
	public static final String TAG_VEHICLES = "vehicles";
	
	public User() {
		super();
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getActivation() {
		return activation;
	}
	public void setActivation(Date activation) {
		this.activation = activation;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public Date getUpdate_at() {
		return update_at;
	}
	public void setUpdate_at(Date parsedDate) {
		this.update_at = parsedDate;
	}
	public String getDriverlicense() {
		return driverlicense;
	}
	public void setDriverlicense(String driverlicense) {
		this.driverlicense = driverlicense;
	}
	public ArrayList<Vehicle> getVehicles() {
		return vehicles;
	}
	public void setVehicles(ArrayList<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}
	public ArrayList<Role> getRoles() {
		return roles;
	}
	public void setRoles(ArrayList<Role> roles) {
		this.roles = roles;
	}
	public ArrayList<Team> getMemberOf() {
		return memberOf;
	}
	public void setMemberOf(ArrayList<Team> memberOf) {
		this.memberOf = memberOf;
	}
	public ArrayList<Team> getManagerOf() {
		return managerOf;
	}
	public void setManagerOf(ArrayList<Team> managerOf) {
		this.managerOf = managerOf;
	}
	

}
