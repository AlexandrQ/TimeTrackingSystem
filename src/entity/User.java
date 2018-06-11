package entity;

import java.io.Serializable;


public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String login;
	private String password;
	private String name;
	private String surname;
	private String role;
	private String project;
	private String regDate;
	private String dismissalDate;
	private String position;
	private String degree;
	private String status;
	private String gender;
	private boolean isLogged = false;
	
	
	
	
	public String getDismissalDate() {
		return dismissalDate;
	}



	public void setDismissalDate(String dismissalDate) {
		this.dismissalDate = dismissalDate;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	public String getGender() {
		return gender;
	}



	public void setGender(String gender) {
		this.gender = gender;
	}



	public String getDegree() {
		return degree;
	}



	public void setDegree(String degree) {
		this.degree = degree;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public String getPosition() {
		return position;
	}



	public void setPosition(String position) {
		this.position = position;
	}



	public String getRegDate() {
		return regDate;
	}



	public void setRegDate(String date) {
		this.regDate = date;
	}



	public String getProject() {
		return project;
	}



	public void setProject(String project) {
		this.project = project;
	}



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
