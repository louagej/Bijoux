package net.louage.bijoux.model;

import java.io.Serializable;

public class Country implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6751058850459116436L;
	private int id;
	private String iso3166;
	private String description;
	
	public Country(String iso3166, String description) {
		super();
		this.iso3166 = iso3166;
		this.description = description;
	}
	public Country() {
	}
	public String getIso3166() {
		return iso3166;
	}
	public void setIso3166(String iso3166) {
		this.iso3166 = iso3166;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
