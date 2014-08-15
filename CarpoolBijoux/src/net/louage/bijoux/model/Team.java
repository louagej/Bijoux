package net.louage.bijoux.model;

public class Team {
	private int team_id;
	private String teamname;
	public static final String TAG_USER_TEAMNAME = "teamname";
	public static final String TAG_ID = "_id";
	public Team(String team, int _id) {
		super();
		this.teamname = team;
		this.team_id=_id;
	}
	

	public Team() {
		// TODO Auto-generated constructor stub
	}


	public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String team) {
		this.teamname = team;
	}


	public int getTeam_id() {
		return team_id;
	}


	public void setTeam_id(int team_id) {
		this.team_id = team_id;
	}
	
}
