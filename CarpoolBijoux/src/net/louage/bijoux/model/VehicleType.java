package net.louage.bijoux.model;

import java.io.Serializable;

public class VehicleType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4657833837202730271L;
	private String type;

	public VehicleType(String type) {
		super();
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	

}
