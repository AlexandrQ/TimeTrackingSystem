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
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import dbCon.SingletonDBConnection;
import entity.Activity;
import entity.User;


@ManagedBean(name = "mainBean")
@SessionScoped

public class MainBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Activity testActivity = new Activity();
	
	private User user = new User();
	private ArrayList<Activity> activitiesList;
	private ArrayList<Activity> activitiesListWeek = new ArrayList<>();	
	private ArrayList<Activity> activitiesListMonth = new ArrayList<>();	
	private String lengthOfCurrentActivitiesList = "week";
	
	private ArrayList<String> workDaysForCalendar = new ArrayList<>();
	private ArrayList<String> vacationDaysForCalendar = new ArrayList<>();	
	private ArrayList<String> sickDaysForCalendar = new ArrayList<>();
	private ArrayList<String> daysOffForCalendar = new ArrayList<>();
	private ArrayList<String> weekends = new ArrayList<>();
	
	private List<String> typesL = new ArrayList<String>();
	private List<String> projectsL = new ArrayList<String>();
	private List<String> taskGroupsL = new ArrayList<String>();
	private List<String> tasksL = new ArrayList<String>();
	
	
	
	
	
	
	public ArrayList<String> getWeekends() {
		return weekends;
	}

	public void setWeekends(ArrayList<String> weekends) {
		this.weekends = weekends;
	}

	public List<String> getTypesL() {
		return typesL;
	}

	public void setTypesL(List<String> typesL) {
		this.typesL = typesL;
	}

	public List<String> getProjectsL() {
		return projectsL;
	}

	public void setProjectsL(List<String> projectsL) {
		this.projectsL = projectsL;
	}

	public List<String> getTaskGroupsL() {
		return taskGroupsL;
	}

	public void setTaskGroupsL(List<String> taskGroupsL) {
		this.taskGroupsL = taskGroupsL;
	}

	public List<String> getTasksL() {
		return tasksL;
	}

	public void setTasksL(List<String> tasksL) {
		this.tasksL = tasksL;
	}

	public Activity getTestActivity() {
		return testActivity;
	}

	public void setTestActivity(Activity testActivity) {
		this.testActivity = testActivity;
	}

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
		if(activitiesList==null){			
			getCurrentAtivities(LocalDate.now());
	    }	    
		return activitiesList;
	}

	public void setActivitiesList(ArrayList<Activity> activitiesList) {
		this.activitiesList = activitiesList;
	}

	public String loggedIn() {				
		if(UserAutenticate()) {			
			user.setLogged(true);
			getCurrentAtivities(LocalDate.now());
			fillWeekendsList();
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
	
	private boolean UserAutenticate() {			
			
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance( "SHA-256" );
			} catch (NoSuchAlgorithmException e1) {			
				e1.printStackTrace();
			}
		    md.update( user.getPassword().getBytes( StandardCharsets.UTF_8 ) );
		    byte[] digest = md.digest();
		    user.setPassword(String.format( "%064x", new BigInteger( 1, digest ) )); 				
			
			String querryStrCount = "SELECT COUNT(user_login) AS Count FROM public.\"users\" WHERE user_login = '" + user.getLogin() + "' AND user_password = '" + user.getPassword() + "' AND user_status = 1";
			String querryStrUser = "SELECT user_id, user_login, user_password, user_name, user_surname, project_name, user_role_name" + 
					"	FROM public.users, public.projects, public.user_roles" + 
					"    WHERE user_login = '" + user.getLogin() + "'" + 
					"    AND user_password = '" + user.getPassword() + "'" +
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
		if (activitiesList==null) activitiesList = new ArrayList<>();
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
	    fillListsForSelectMenus();
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
	}
	
	
	public void onDateSelect(SelectEvent event) {
		
		//получаем выбранную дату из event
		Date date = (Date) event.getObject();	
		
		//преобразуем Date в LocalDate
		LocalDate dd = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();	
		
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
	 
	
	public void onRowEdit(RowEditEvent event) {
	
		FacesMessage msg = null;
		Activity obj = (Activity) event.getObject();
		String queryStr = "";
		if(!obj.getProject().isEmpty() && !obj.getTaskGroup().isEmpty() && !obj.getTask().isEmpty()) {
			if (obj.getStatus().equals("Not filled")) {				
				queryStr = "INSERT INTO public.activities(" + 
						"	activity_user, activity_date, activity_project, activity_percentage, activity_type, activity_task_group, activity_task, activity_comment, activity_proportion, activity_status)" + 
						"	VALUES ((SELECT user_id FROM public.users WHERE user_login = '" + user.getLogin() + "' ), " + 
						"            '" + obj.getDate() + "', " + 
						"            (SELECT project_id FROM public.projects WHERE project_name = '" + obj.getProject() + "' ), " + 
						"            '" + obj.getPercentage() + "', " +
						"            (SELECT activity_type_id FROM public.activity_types WHERE activity_type_name = '" + obj.getType() + "' ), " + 
						"            (SELECT activity_task_group_id FROM public.activity_task_groups WHERE activity_task_group_name = '" + obj.getTaskGroup() + "' ), " + 
						"            (SELECT activity_task_id FROM public.activity_tasks WHERE activity_task_name = '" + obj.getTask() + "' ), " + 
						"            '" + obj.getComment() + "', " + 
						"            '" + obj.getProportion() + "', " +
						"            3);";
			} 
			else {			
				queryStr = "UPDATE public.activities" + 
						"	SET activity_percentage=" + obj.getPercentage() +" ," + 
						"        activity_type=(SELECT activity_type_id FROM public.activity_types WHERE activity_type_name = '" + obj.getType() + "' )," +
						"        activity_task_group=(SELECT activity_task_group_id FROM public.activity_task_groups WHERE activity_task_group_name = '" + obj.getTaskGroup() + "' )," +
						"        activity_task=(SELECT activity_task_id FROM public.activity_tasks WHERE activity_task_name = '" + obj.getTask() + "' )," +
						"        activity_project=(SELECT project_id FROM public.projects WHERE project_name = '" + obj.getProject() + "' )," + 
						"        activity_comment = '" + obj.getComment() + "', " + 
						"        activity_proportion = " + obj.getProportion() + ", " + 
						"        activity_status = 3" + 
						"	WHERE activity_user = (SELECT user_id FROM public.users WHERE user_login = '" + user.getLogin() +"' )" + 
						"    AND activity_date = '" + obj.getDate() +"'";
			}
		} else if(obj.getType().equals("Sick day") || obj.getType().equals("Day off")) {
			if (obj.getStatus().equals("Not filled")) {				
				queryStr = "INSERT INTO public.activities(" + 
						"	activity_user, activity_date, activity_percentage, activity_type, activity_comment, activity_proportion, activity_status)" + 
						"	VALUES ((SELECT user_id FROM public.users WHERE user_login = '" + user.getLogin() + "' ), " + 
						"            '" + obj.getDate() + "', " +						
						"            '" + obj.getPercentage() + "', " +
						"            (SELECT activity_type_id FROM public.activity_types WHERE activity_type_name = '" + obj.getType() + "' ), " + 						
						"            '" + obj.getComment() + "', " + 
						"            '" + obj.getProportion() + "', " +
						"            3);";
			} 
			else {			
				queryStr = "UPDATE public.activities" + 
						"	SET activity_percentage=" + obj.getPercentage() +" ," + 
						"        activity_type=(SELECT activity_type_id FROM public.activity_types WHERE activity_type_name = '" + obj.getType() + "' )," +
						"        activity_task_group=null ," +
						"        activity_task=null ," +
						"        activity_project=null ," + 
						"        activity_comment = '" + obj.getComment() + "', " + 
						"        activity_proportion = " + obj.getProportion() + ", " + 
						"        activity_status = 3" + 
						"	WHERE activity_user = (SELECT user_id FROM public.users WHERE user_login = '" + user.getLogin() +"' )" + 
						"    AND activity_date = '" + obj.getDate() +"'";
			}
		} else {
			msg = new FacesMessage("Error", "Fields can not be empty");
			return;
		}
		
		Connection dbConnection = null;
		Statement statement = null;
		    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();		
		    
		    if (!(statement.executeUpdate(queryStr) == 1)) {			    	
		    	msg = new FacesMessage("Error", "Update was unsuccessful");
		    } else msg = new FacesMessage("OK", "Successful!");
		    
		    if (dbConnection != null) {
		    	dbConnection.close();	
		    }				
		    
		} catch (SQLException e) {
		    System.out.println(e.getMessage());	
		    msg = new FacesMessage("Error", "Update was unsuccessful");
		    if (dbConnection != null) {
	            try {
					dbConnection.close();
				} catch (SQLException ee) {				
					ee.printStackTrace();
				}		            
	        }	
		}
	    getCurrentAtivities(LocalDate.now());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
     
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", "");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }	
    
    
    private void fillListsForSelectMenus() {
				
    	typesL.clear();
    	taskGroupsL.clear();
    	tasksL.clear();
    	projectsL.clear();
    	
		String queryStrTypes = "SELECT activity_type_name FROM public.activity_types";
		String queryStrProjects = "SELECT project_name FROM public.projects";
		String queryStrTaskGroups = "SELECT activity_task_group_name FROM public.activity_task_groups";
		String queryStrTasks = "SELECT activity_task_name FROM public.activity_tasks";
				
		Connection dbConnection = null;
		Statement statement = null;
		ResultSet rsTypes, rsTaskGroups, rsTasks, rsProj;
		
		try {				
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
		    statement = dbConnection.createStatement();
		    
		    rsTypes = statement.executeQuery(queryStrTypes);    		
    		while (rsTypes.next()) {	
    			typesL.add(rsTypes.getString("activity_type_name"));
		    }
    		
    		rsTaskGroups = statement.executeQuery(queryStrTaskGroups);    		
    		while (rsTaskGroups.next()) {	
    			taskGroupsL.add(rsTaskGroups.getString("activity_task_group_name"));
		    }
    		
    		rsTasks = statement.executeQuery(queryStrTasks);    		
    		while (rsTasks.next()) {	
    			tasksL.add(rsTasks.getString("activity_task_name"));
		    }
    		
    		rsProj = statement.executeQuery(queryStrProjects);    		
    		while (rsProj.next()) {	
    			projectsL.add(rsProj.getString("project_name"));
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
    
    
    private void fillWeekendsList() {
		String queryStrWeekens = "SELECT weekend_date FROM public.weekends";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsWeekend;
	    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();	 
		    
		    rsWeekend = statement.executeQuery(queryStrWeekens);
		    
		    while (rsWeekend.next()) {
		    	weekends.add(rsWeekend.getString("weekend_date"));		    	
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

}
