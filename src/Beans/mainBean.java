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
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;

import dbCon.SingletonDBConnection;
import entity.Activity;
import entity.User;


@ManagedBean(name = "mainBean")
@SessionScoped

public class MainBean implements Serializable{

	private User user = new User();
	private ArrayList<Activity> activitiesList = new ArrayList<>();
	private ArrayList<Activity> activitiesListWeek = new ArrayList<>();	
	private ArrayList<Activity> activitiesListMonth = new ArrayList<>();	
	private String lengthOfCurrentActivitiesList = "week";
	
	private ArrayList<String> workDaysForCalendar = new ArrayList<>();
	private ArrayList<String> vacationDaysForCalendar = new ArrayList<>();	
	private ArrayList<String> sickDaysForCalendar = new ArrayList<>();
	private ArrayList<String> daysOffForCalendar = new ArrayList<>();
	
	
	
	
	public ArrayList<String> getVacationDaysForCalendar() {
		return vacationDaysForCalendar;
	}

	public void setVacationDaysForCalendar(ArrayList<String> vacationDaysForCalendar) {
		this.vacationDaysForCalendar = vacationDaysForCalendar;
	}

	public ArrayList<String> getWorkDaysForCalendar() {
		return workDaysForCalendar;
	}

	public void setWorkDaysForCalendar(ArrayList<String> workDaysForCalendar) {
		this.workDaysForCalendar = workDaysForCalendar;
	}

	public ArrayList<String> getSickDaysForCalendar() {
		return sickDaysForCalendar;
	}

	public void setSickDaysForCalendar(ArrayList<String> sickDaysForCalendar) {
		this.sickDaysForCalendar = sickDaysForCalendar;
	}

	public ArrayList<String> getDaysOffForCalendar() {
		return daysOffForCalendar;
	}

	public void setDaysOffForCalendar(ArrayList<String> daysOffForCalendar) {
		this.daysOffForCalendar = daysOffForCalendar;
	}

	public ArrayList<Activity> getActivitiesListWeek() {
		return activitiesListWeek;
	}

	public void setActivitiesListWeek(ArrayList<Activity> activitiesListWeek) {
		this.activitiesListWeek = activitiesListWeek;
	}

	public ArrayList<Activity> getActivitiesListMonth() {
		return activitiesListMonth;
	}

	public void setActivitiesListMonth(ArrayList<Activity> activitiesListMonth) {
		this.activitiesListMonth = activitiesListMonth;
	}

	public String getLengthOfCurrentActivitiesList() {
		return lengthOfCurrentActivitiesList;
	}

	public void setLengthOfCurrentActivitiesList(String lengthOfCurrentActivitiesList) {
		this.lengthOfCurrentActivitiesList = lengthOfCurrentActivitiesList;
	}

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
			getCurrentAtivities(LocalDate.now());				
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
			
			String querryStrCount = "SELECT COUNT(user_login) AS Count FROM public.\"users\" WHERE user_login = '" + login + "' AND user_password = '" + Password + "'";
			String querryStrUser = "SELECT user_id, user_login, user_password, user_name, user_surname, project_name, user_role_name" + 
					"	FROM public.users, public.projects, public.user_roles" + 
					"    WHERE user_login = '" + login + "'" + 
					"    AND user_project = project_id" + 					 
					"    AND user_role = user_role_id";
			Connection dbConnection = null;
		    Statement statement = null;
		    ResultSet rsCount, rsUser;
			
			try {				
			    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
			    statement = dbConnection.createStatement();		 
			    
			    rsCount = statement.executeQuery(querryStrCount);		    
			    
			    if (rsCount.next()) {		    	
			    	if(rsCount.getString("Count").equals("1") ) {
			    		rsUser = statement.executeQuery(querryStrUser);
			    		while (rsUser.next()) {	
					    		user.setName(rsUser.getString("user_name"));	 
					    		user.setSurname(rsUser.getString("user_surname"));	
					    		user.setProject(rsUser.getString("project_name"));	
					    		user.setRole(rsUser.getString("user_role_name"));
					    }
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
	
	
	private void getCurrentAtivities(LocalDate date) {		
		activitiesList.clear();
		activitiesListWeek.clear();
		activitiesListMonth.clear();
		
		ArrayList<LocalDate> daysInSelectedWeek = fillDaysInSelectedWeek(date); 
		ArrayList<LocalDate> daysInSelectedMonth = fillDaysInSelectedMonth(date);
		
		String queryStrWeek = "SELECT activity_date, project_name,  activity_proportion,  activity_type_name, activity_task_group_name, activity_task_name, activity_comment, activity_percentage, activity_status_name" + 
				"	FROM public.activities, public.projects, public.activity_types, public.activity_task_groups, public.activity_tasks, public.activity_statuses" + 
				"   WHERE activity_user = (SELECT user_id FROM public.users WHERE user_login = '" + user.getLogin() + "' )" + 
				"   AND activity_date between '" + daysInSelectedWeek.get(0).toString() + "' and '" + daysInSelectedWeek.get(daysInSelectedWeek.size()-1).toString() + "'" + 
				"   AND activity_project = project_id" + 
				"   AND activity_type = activity_type_id" + 
				"   AND activity_task_group = activity_task_group_id" + 
				"   AND activity_task = activity_task_id" + 
				"   AND activity_status = activity_status_id";
		
		String queryStrWeekForNotWorkDays = "SELECT DISTINCT activity_date, activity_proportion,  activity_type_name, activity_comment, activity_percentage, activity_status_name" + 
				"	FROM public.activities, public.projects, public.activity_types, public.activity_task_groups, public.activity_tasks, public.activity_statuses " + 
				"   WHERE activity_user = (SELECT user_id FROM public.users WHERE user_login = '" + user.getLogin() + "' )" + 
				"   AND activity_date between '" + daysInSelectedWeek.get(0).toString() + "' and '" + daysInSelectedWeek.get(daysInSelectedWeek.size()-1).toString() + "'" + 
				"   AND activity_type = activity_type_id" +
				"   AND activity_status = activity_status_id" +
				"	AND NOT activity_type = 1";
		
		String queryStrMonth = "SELECT activity_date, project_name,  activity_proportion,  activity_type_name, activity_task_group_name, activity_task_name, activity_comment, activity_percentage, activity_status_name" + 
				"	FROM public.activities, public.projects, public.activity_types, public.activity_task_groups, public.activity_tasks, public.activity_statuses" + 
				"   WHERE activity_user = (SELECT user_id FROM public.users WHERE user_login = '" + user.getLogin() + "' )" + 
				"   AND activity_date between '" + daysInSelectedMonth.get(0).toString() + "' and '" + daysInSelectedMonth.get(daysInSelectedMonth.size()-1).toString() + "'" + 
				"   AND activity_project = project_id" + 
				"   AND activity_type = activity_type_id" + 
				"   AND activity_task_group = activity_task_group_id" + 
				"   AND activity_task = activity_task_id" + 
				"   AND activity_status = activity_status_id";
		
		String queryStrMonthForNotWorkDays = "SELECT DISTINCT activity_date, activity_proportion,  activity_type_name, activity_comment, activity_percentage, activity_status_name" + 
				"	FROM public.activities, public.projects, public.activity_types, public.activity_task_groups, public.activity_tasks, public.activity_statuses" + 
				"	WHERE activity_user = (SELECT user_id FROM public.users WHERE user_login = '" + user.getLogin() + "' )" + 
				"	AND activity_date between '" + daysInSelectedMonth.get(0).toString() + "' and '" + daysInSelectedMonth.get(daysInSelectedMonth.size()-1).toString() + "'" + 
				"	AND activity_type = activity_type_id" + 
				"	AND activity_status = activity_status_id" +
				"	AND NOT activity_type = 1";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsWeek, rsMonth, rsWeekNWD, rsMonthNWD;
	    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();	 
		    
		    rsWeek = statement.executeQuery(queryStrWeek);		    
		    
		    while (rsWeek.next()) {	
		    	Activity activity = new Activity(rsWeek.getString("activity_date"), 
		    			rsWeek.getString("activity_type_name"),
		    			rsWeek.getString("activity_proportion"),
		    			rsWeek.getString("project_name"),
		    			rsWeek.getString("activity_task_group_name"), 
		    			rsWeek.getString("activity_task_name"),
		    			rsWeek.getString("activity_comment"),
		    			rsWeek.getString("activity_percentage"),
		    			rsWeek.getString("activity_status_name"));		 
		    	activitiesListWeek.add(activity);
		    }
		    
		    rsWeekNWD = statement.executeQuery(queryStrWeekForNotWorkDays);		    
		    
		    while (rsWeekNWD.next()) {	
		    	Activity activity = new Activity(rsWeekNWD.getString("activity_date"), 
		    			rsWeekNWD.getString("activity_type_name"),
		    			rsWeekNWD.getString("activity_proportion"),
		    			rsWeekNWD.getString("activity_comment"),
		    			rsWeekNWD.getString("activity_percentage"),
		    			rsWeekNWD.getString("activity_status_name"));		 
		    	activitiesListWeek.add(activity);
		    }
		    
		    rsMonth = statement.executeQuery(queryStrMonth);
		    
		    while (rsMonth.next()) {	
		    	Activity activity = new Activity(rsMonth.getString("activity_date"),
		    			rsMonth.getString("activity_type_name"),
		    			rsMonth.getString("activity_proportion"),
		    			rsMonth.getString("project_name"),
		    			rsMonth.getString("activity_task_group_name"), 
		    			rsMonth.getString("activity_task_name"),
		    			rsMonth.getString("activity_comment"),
		    			rsMonth.getString("activity_percentage"),
		    			rsMonth.getString("activity_status_name"));	 
		    	activitiesListMonth.add(activity);		    	
		    }
		    
		    rsMonthNWD = statement.executeQuery(queryStrMonthForNotWorkDays);
		    
		    while (rsMonthNWD.next()) {	
		    	Activity activity = new Activity(rsMonthNWD.getString("activity_date"),
		    			rsMonthNWD.getString("activity_type_name"),
		    			rsMonthNWD.getString("activity_proportion"),		    			
		    			rsMonthNWD.getString("activity_comment"),
		    			rsMonthNWD.getString("activity_percentage"),
		    			rsMonthNWD.getString("activity_status_name"));	 
		    	activitiesListMonth.add(activity);		    	
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
	    
	    fillActivitiesListForEmptyDays(daysInSelectedWeek);
	    fillActivitiesListForEmptyDays(daysInSelectedMonth);
		
	    if (lengthOfCurrentActivitiesList.equals("week")) {activitiesList = activitiesListWeek;}
	    else if((lengthOfCurrentActivitiesList.equals("month"))) {activitiesList = activitiesListMonth;}
	    
	    fillDaysForCalendarHighlighter();
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
			System.out.println("(My comment) ArrayWeek: " + daysInSelectedWeek.get(i) );
		}
		
		return daysInSelectedWeek;
	}
	
	
	private ArrayList<LocalDate> fillDaysInSelectedMonth(LocalDate date) {
		//+-31 days
		int selectedMonth;
		ArrayList<LocalDate> daysInSelectedMonth = new ArrayList<>();
		daysInSelectedMonth.add(date);
		
		//получаем из LocalDate номер недели
		//WeekFields weekFields = WeekFields.of(Locale.getDefault()); 		
		//selectedWeek = date.get(weekFields.weekOfWeekBasedYear());
		selectedMonth = date.getMonthValue();
		
		 		
		int numberOfMonth = -1;
		LocalDate myDate1 = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
		LocalDate myDate2;
		
		for (int i = 1; i<31; i++) {
			myDate2 = myDate1.plusDays(i);
			numberOfMonth = myDate2.getMonthValue() ;
			if (numberOfMonth == selectedMonth) daysInSelectedMonth.add(myDate2);
			
			myDate2 = myDate1.minusDays(i);
			numberOfMonth = myDate2.getMonthValue();
			if (numberOfMonth == selectedMonth) daysInSelectedMonth.add(myDate2);
		}
		
		Collections.sort(daysInSelectedMonth);
		
		for (int i = 0; i < daysInSelectedMonth.size(); i++) {
			System.out.println("(My comment) ArrayMonth: " + daysInSelectedMonth.get(i) );
		}
		
		return daysInSelectedMonth;
	}
	
	
	private void fillActivitiesListForEmptyDays(ArrayList<LocalDate> daysInSelectedPeriod) {
		if(daysInSelectedPeriod.size() < 8) {
			for(int i = 0; i < activitiesListWeek.size(); i++ ) {
				for(int j=0; j < daysInSelectedPeriod.size(); j++) {
					if(activitiesListWeek.get(i).getDate().equals(daysInSelectedPeriod.get(j).toString())) {
						daysInSelectedPeriod.remove(j);
						break;
					}
				}
			}
			
			for (int i = 0; i < daysInSelectedPeriod.size(); i++) {
				activitiesListWeek.add(new Activity(daysInSelectedPeriod.get(i).toString()));
			}
			
			bubbleSortActivitiesListByDate("week");
		}
		else {
			for(int i = 0; i < activitiesListMonth.size(); i++ ) {
				for(int j=0; j < daysInSelectedPeriod.size(); j++) {
					if(activitiesListMonth.get(i).getDate().equals(daysInSelectedPeriod.get(j).toString())) {
						daysInSelectedPeriod.remove(j);
						break;
					}
				}
			}
			
			for (int i = 0; i < daysInSelectedPeriod.size(); i++) {
				activitiesListMonth.add(new Activity(daysInSelectedPeriod.get(i).toString()));
			}
			
			bubbleSortActivitiesListByDate("month");
		}
				
		
	}
	
	//сортирует только определенный лист activitiesListWeek и activitiesListMonth
	private void bubbleSortActivitiesListByDate(String str) {
		int i = 0;
        int goodPairsCounter = 0;
        LocalDate date1 = null, date2 = null;
        
        if(str.equals("week")) {
        	while (true) {        	
                if (date2.parse(activitiesListWeek.get(i+1).getDate()).isBefore(date1.parse(activitiesListWeek.get(i).getDate()))) {
                    Activity q = activitiesListWeek.get(i);
                    activitiesListWeek.set(i, activitiesListWeek.get(i+1));
                    activitiesListWeek.set((i + 1), q);
                    goodPairsCounter = 0;
                } else {
                    goodPairsCounter++;
                }
                i++;
                if (i == activitiesListWeek.size() - 1) {
                    i = 0;
                }
                if (goodPairsCounter == activitiesListWeek.size() - 1) break;
            }        
        }
        else if(str.equals("month")){
        	while (true) {        	
                if (date2.parse(activitiesListMonth.get(i+1).getDate()).isBefore(date1.parse(activitiesListMonth.get(i).getDate()))) {
                    Activity q = activitiesListMonth.get(i);
                    activitiesListMonth.set(i, activitiesListMonth.get(i+1));
                    activitiesListMonth.set((i + 1), q);
                    goodPairsCounter = 0;
                } else {
                    goodPairsCounter++;
                }
                i++;
                if (i == activitiesListMonth.size() - 1) {
                    i = 0;
                }
                if (goodPairsCounter == activitiesListMonth.size() - 1) break;
            }        
        }
        
        
	}
	
	
	private void fillDaysForCalendarHighlighter() {		
		workDaysForCalendar.clear();
		vacationDaysForCalendar.clear();
		sickDaysForCalendar.clear();
		daysOffForCalendar.clear();
		for (int i = 0; i < activitiesListMonth.size(); i++) {					
			if (activitiesListMonth.get(i).getType() != null) {
				if (activitiesListMonth.get(i).getType().equals("Work day")) {					
					workDaysForCalendar.add("'" + activitiesListMonth.get(i).getDate() + "'");				
				}
				else if (activitiesListMonth.get(i).getType().equals("Vacation")) {
					vacationDaysForCalendar.add("'" + activitiesListMonth.get(i).getDate() + "'");
				}
				else if (activitiesListMonth.get(i).getType().equals("Sick day")) {
					sickDaysForCalendar.add("'" + activitiesListMonth.get(i).getDate() + "'");
				}
				else if (activitiesListMonth.get(i).getType().equals("Day off")) {
					daysOffForCalendar.add("'" + activitiesListMonth.get(i).getDate() + "'");
				}
			}
			
		}
		System.out.println("(My comment) fillDaysForCalendarHighlighter->workDaysForCalendar : " + workDaysForCalendar.toString() );
		System.out.println("(My comment) fillDaysForCalendarHighlighter->vacationDaysForCalendar : " + vacationDaysForCalendar.toString() );
		System.out.println("(My comment) fillDaysForCalendarHighlighter->sickDaysForCalendar : " + sickDaysForCalendar.toString() );
		System.out.println("(My comment) fillDaysForCalendarHighlighter->daysOffForCalendar : " + daysOffForCalendar.toString() );
		/*for (int i = 0; i < workDaysForCalendar.size(); i++) {
			System.out.println("(My comment) fillDaysForCalendarHighlighter->workDaysForCalendar[i] : " + workDaysForCalendar.get(i));
		}*/
		
	}
	
	
	public void onDateSelect(SelectEvent event) {
		System.out.println("(My comment) onDateSelect->SelectEvent");
		
		//получаем выбранную дату из event
		Date date = (Date) event.getObject();	
		
		//преобразуем Date в LocalDate
		LocalDate dd = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();		
		System.out.println("(My comment) onDateSelect->LocalDate: " + dd);
		
		getCurrentAtivities(dd);
	}
	
	
	public void showWeekActions(){
		lengthOfCurrentActivitiesList = "week";
		getCurrentAtivities(LocalDate.now());
	}
	
	public void showMonthActions(){
		lengthOfCurrentActivitiesList = "month";
		getCurrentAtivities(LocalDate.now());
	}
	
	
}
