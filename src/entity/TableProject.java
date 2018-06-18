package entity;

import java.util.ArrayList;
import java.util.List;

public class TableProject {

	private int males;
	private int females;
	private String projectName;
	private List<User> users = new ArrayList<>();
	
	
	public int getMales() {
		return males;
	}
	public void setMales(int males) {
		this.males = males;
	}
	public int getFemales() {
		return females;
	}
	public void setFemales(int females) {
		this.females = females;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String name) {
		this.projectName = name;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	} 
	
	
	
}
