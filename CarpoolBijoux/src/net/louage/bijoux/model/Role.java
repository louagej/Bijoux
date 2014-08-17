package net.louage.bijoux.model;

public class Role {
	private String rolename;
	public static final String TAG_USER_ROLENAME = "role_name";

	public Role(String role) {
		super();
		this.rolename = role;
	}
	
	public Role() {
		// TODO Auto-generated constructor stub
	}

	public String getRoleName() {
		return rolename;
	}

	public void setRoleName(String role) {
		this.rolename = role;
	}


}
