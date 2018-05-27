package entity;

import java.io.Serializable;

//@ManagedBean(name = "user")
//@SessionScoped

public class User implements Serializable{
	private String login;
	private String password;
	private String name;
	private String surname;
	private String role;
	private boolean isLogged = false;
	
	
	
	public String getLogin() {
		return login;
	}



	public void setLogin(String login) {
		this.login = login;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getSurname() {
		return surname;
	}



	public void setSurname(String surname) {
		this.surname = surname;
	}



	public String getRole() {
		return role;
	}



	public void setRole(String role) {
		this.role = role;
	}



	public boolean isLogged() {
		return isLogged;
	}



	public void setLogged(boolean isLogged) {
		this.isLogged = isLogged;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}
	
}
