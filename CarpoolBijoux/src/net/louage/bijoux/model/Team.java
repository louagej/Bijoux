package net.louage.bijoux.model;

public class Team {
	private String team;
	public static final String TAG_USER_TEAMNAME = "teamname";
	public Team(String team) {
		super();
		this.team = team;
	}
	

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}
	
}
