package Beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import dbCon.SingletonDBConnection;
import entity.Activity;

@ManagedBean(name = "confirmBean")
@ViewScoped
public class ActivityConfirmationBean {
	
	private Activity selectedActivity;
	private List<Activity> unconfirmedActivies = new ArrayList<>();
	
	@ManagedProperty(value="#{mainBean}")
	private MainBean mb;
	
	
	
	public Activity getSelectedActivity() {
		return selectedActivity;
	}

	public void setSelectedActivity(Activity selectedActivity) {
		this.selectedActivity = selectedActivity;
	}

	public List<Activity> getUnconfirmedActivies() {
		return unconfirmedActivies;
	}

	public void setUnconfirmedActivies(List<Activity> unconfirmedActivies) {
		this.unconfirmedActivies = unconfirmedActivies;
	}

	public MainBean getMb() {
		return mb;
	}

	public void setMb(MainBean mb) {
		this.mb = mb;
	}

	public void selectUnconfirmedActivies(){
		unconfirmedActivies.clear();	
		
		String queryStrActivities = "SELECT user_login, activity_date, project_name,  activity_proportion,  activity_type_name, activity_task_group_name, activity_task_name, activity_comment, activity_percentage, activity_status_name" + 
				"	FROM public.users, public.activities, public.projects, public.activity_types, public.activity_task_groups, public.activity_tasks, public.activity_statuses" + 
				"   WHERE activity_project = (SELECT project_id FROM public.projects WHERE project_name = '" + mb.getUser().getProject() + "' )" + 
				"   AND activity_type = activity_type_id" + 
				"   AND activity_task_group = activity_task_group_id" + 
				"   AND activity_task = activity_task_id" + 
				"   AND activity_status = activity_status_id" +
				"   AND activity_user = user_id" +
				"   AND activity_project = project_id" +
				"   AND activity_status = 3";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsActivity;
		
		try {				
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
		    statement = dbConnection.createStatement();
		    rsActivity = statement.executeQuery(queryStrActivities);
    		
    		while (rsActivity.next()) {    			
    			Activity activity = new Activity(rsActivity.getString("user_login"),
    					rsActivity.getString("activity_date"),
    					rsActivity.getString("activity_type_name"),
    					rsActivity.getString("activity_proportion"),
    					rsActivity.getString("project_name"),
    					rsActivity.getString("activity_task_group_name"), 
    					rsActivity.getString("activity_task_name"),
    					rsActivity.getString("activity_comment"),
    					rsActivity.getString("activity_percentage"),
    					rsActivity.getString("activity_status_name"));	 
    			unconfirmedActivies.add(activity);		    	    			
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
	
	public void confirmActivity() {
		
		FacesMessage msg = null;
		String queryStrDelete = "UPDATE public.activities SET activity_status=1" + 
				"	WHERE activity_user = (SELECT user_id FROM public.users WHERE user_login = '" + selectedActivity.getUser() + "')" + 
				"    AND activity_date = '" + selectedActivity.getDate() + "'";
		
		Connection dbConnection = null;
		Statement statement = null;
		    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();		
		    
		    if (!(statement.executeUpdate(queryStrDelete) == 1)) {
		    	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "Confirmation was unsuccessful");		    	 		    	
		    }
		    else {
		    	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Confirmation was successful");
		    }
		    
		    if (dbConnection != null) {
		    	dbConnection.close();	
		    }				
		    
		} catch (SQLException e) {
		    System.out.println(e.getMessage());	
		    
		    if (dbConnection != null) {
	            try {
					dbConnection.close();
				} catch (SQLException ee) {				
					ee.printStackTrace();
				}		            
	        }	
		} 
		
	    selectUnconfirmedActivies();
        FacesContext.getCurrentInstance().addMessage(null, msg); 
		 
	}
}
