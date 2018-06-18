package entity;

import java.util.ArrayList;
import java.util.List;

public class TableDegree {
	
	private int males;
	private int females;
	private String degreeName;
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
	public String getDegreeName() {
		return degreeName;
	}
	public void setDegreeName(String degreeName) {
		this.degreeName = degreeName;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}

	
	
}
