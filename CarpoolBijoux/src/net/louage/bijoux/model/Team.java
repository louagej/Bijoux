package net.louage.bijoux.model;

public class Team {
	private int team_id;
	private String team;
	public static final String TAG_USER_TEAMNAME = "teamname";
	public static final String TAG_ID = "_id";
	public Team(String team, int _id) {
		super();
		this.team = team;
		this.team_id=_id;
	}
	

	public Team() {
		// TODO Auto-generated constructor stub
	}


	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}


	public int getTeam_id() {
		return team_id;
	}


	public void setTeam_id(int team_id) {
		this.team_id = team_id;
	}
	
}
