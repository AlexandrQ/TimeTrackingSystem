package Beans;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import dbCon.SingletonDBConnection;

@ManagedBean(name = "user")
@SessionScoped

public class User {
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







	public String loggedIn() {				
		if(UserAutenticate(login, password)) {			
			isLogged = true;					
			//this.myCosts = getAllCostsFromDB(name);			
			return "myActivity.xhtml?faces-redirect=true";			
		}
		else {
			isLogged = false;			 
		    FacesMessage message = null;
		    message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials");
		    FacesContext.getCurrentInstance().addMessage(null, message);		     			
			return "login.xhtml";			
		}
	}
	
	private boolean UserAutenticate(String login, String password) {
			
			//когда будет готова регистрация, вернуть хеширование пароля
			/*MessageDigest md = null;
			try {
				md = MessageDigest.getInstance( "SHA-256" );
			} catch (NoSuchAlgorithmException e1) {			
				e1.printStackTrace();
			}
		    md.update( password.getBytes( StandardCharsets.UTF_8 ) );
		    byte[] digest = md.digest();
		    String Password = String.format( "%064x", new BigInteger( 1, digest ) );	    
			*/
		
			String Password = password;
			
			String querryStr = "SELECT COUNT(user_login) AS Count FROM public.\"users\" WHERE user_login = '" + login + "' AND user_password = '" + Password + "'";
			Connection dbConnection = null;
		    Statement statement = null;
		    ResultSet rs;
			
			try {				
			    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
			    statement = dbConnection.createStatement();		 
			    
			    rs = statement.executeQuery(querryStr);		    
			    
			    if (rs.next()) {		    	
			    	if(rs.getString("Count").equals("1") ) {
				    	return true;
				    }
			    	else return false;   
			    }
			    else return false; 		    
				
			} catch (SQLException e) {
			    System.out.println(e.getMessage());
			    return false;
			} finally {
				if (dbConnection != null) {
		            try {
						dbConnection.close();
					} catch (SQLException e) {				
						e.printStackTrace();
					}
		        }			
			}	
		}
}
