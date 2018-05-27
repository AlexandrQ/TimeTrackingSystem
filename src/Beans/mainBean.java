package Beans;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import dbCon.SingletonDBConnection;
import entity.Activity;
import entity.User;


@ManagedBean(name = "mainBean")
@SessionScoped

public class MainBean implements Serializable{

	private User user = new User();
	private ArrayList<Activity> activitiesList = new ArrayList<>();	
	
	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ArrayList<Activity> getActivitiesList() {
		return activitiesList;
	}

	public void setActivitiesList(ArrayList<Activity> activitiesList) {
		this.activitiesList = activitiesList;
	}

	public String loggedIn() {				
		if(UserAutenticate(user.getLogin(), user.getPassword())) {			
			user.setLogged(true);
			getCurrentAtivities();				
			return "myActivity.xhtml?faces-redirect=true";			
		}
		else {
			user.setLogged(false);			 
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
	
	
	private void getCurrentAtivities() {
		ArrayList<LocalDate> daysInSelectedWeek = fillDaysInSelectedWeek(LocalDate.now());
		String queryStr = "SELECT activity_date, project_name, activity_percentage, activity_type_name, activity_task_group_name, activity_task_name, activity_comment, activity_proportion, activity_status_name" + 
				"	FROM public.activities, public.projects, public.activity_types, public.activity_task_groups, public.activity_tasks, public.activity_statuses" + 
				"    WHERE activity_user = (SELECT user_id FROM public.users WHERE user_login = '" + user.getLogin() + "' )" + 
				"    AND activity_date between '" + daysInSelectedWeek.get(0).toString() + "' and '" + daysInSelectedWeek.get(daysInSelectedWeek.size()-1).toString() + "'" + 
				"    AND activity_project = project_id" + 
				"    AND activity_type = activity_type_id" + 
				"    AND activity_task_group = activity_task_group_id" + 
				"    AND activity_task = activity_task_id" + 
				"    AND activity_status = activity_status_id";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rs;
	    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();	 
		    
		    rs = statement.executeQuery(queryStr);		    
		    
		    while (rs.next()) {	
		    	Activity activity = new Activity(rs.getString("activity_date"), 
		    			rs.getString("project_name"),
		    			rs.getString("activity_percentage"),
		    			rs.getString("activity_type_name"), 
		    			rs.getString("activity_task_group_name"), 
		    			rs.getString("activity_task_name"),
		    			rs.getString("activity_comment"),
		    			rs.getString("activity_proportion"),
		    			rs.getString("activity_status_name"));	 
		    	activitiesList.add(activity);
		    }    
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());	 
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
	
	
	private ArrayList<LocalDate> fillDaysInSelectedWeek(LocalDate date) {
		//+-6 days
		int selectedWeek;
		ArrayList<LocalDate> daysInSelectedWeek = new ArrayList<>();
		daysInSelectedWeek.add(date);
		
		//получаем из LocalDate номер недели
		WeekFields weekFields = WeekFields.of(Locale.getDefault()); 		
		selectedWeek = date.get(weekFields.weekOfWeekBasedYear());
		
		 		
		int numberOfWeek = -1;
		LocalDate myDate1 = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
		LocalDate myDate2;
		
		for (int i = 1; i<7; i++) {
			myDate2 = myDate1.plusDays(i);
			numberOfWeek = myDate2.get(weekFields.weekOfWeekBasedYear());
			if (numberOfWeek == selectedWeek) daysInSelectedWeek.add(myDate2);
			
			myDate2 = myDate1.minusDays(i);
			numberOfWeek = myDate2.get(weekFields.weekOfWeekBasedYear());
			if (numberOfWeek == selectedWeek) daysInSelectedWeek.add(myDate2);
		}
		
		Collections.sort(daysInSelectedWeek);
		
		for (int i = 0; i < daysInSelectedWeek.size(); i++) {
			System.out.println("(My comment) Array: " + daysInSelectedWeek.get(i) );
		}
		
		return daysInSelectedWeek;
	}
	
}
