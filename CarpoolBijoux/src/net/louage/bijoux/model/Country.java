package net.louage.bijoux.model;

public class Country {
	private String iso3166;
	private String description;
	
	public Country(String iso3166, String description) {
		super();
		this.iso3166 = iso3166;
		this.description = description;
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
	
}
