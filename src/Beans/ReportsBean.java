package Beans;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import dbCon.SingletonDBConnection;
import entity.TableDegree;
import entity.TableProject;
import entity.User;

@ManagedBean(name = "reportsBean")
@ViewScoped

public class ReportsBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<TableProject> tbProjects = new ArrayList<>();
	private List<TableDegree> tbDegrees = new ArrayList<>();
	
	private List<User> hiredStaff = new ArrayList<>();
	private List<User> dismissedStaff = new ArrayList<>();
	private String fromDate;
	private String toDate;
	
	
	
	
	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public List<User> getHiredStaff() {
		return hiredStaff;
	}

	public void setHiredStaff(List<User> hiredStaff) {
		this.hiredStaff = hiredStaff;
	}

	public List<User> getDismissedStaff() {
		return dismissedStaff;
	}

	public void setDismissedStaff(List<User> dismissedStaff) {
		this.dismissedStaff = dismissedStaff;
	}

	public List<TableDegree> getTbDegrees() {
		return tbDegrees;
	}

	public void setTbDegrees(List<TableDegree> tbDegrees) {
		this.tbDegrees = tbDegrees;
	}

	public List<TableProject> getTbProjects() {
		return tbProjects;
	}

	public void setTbProjects(List<TableProject> tbProjects) {
		this.tbProjects = tbProjects;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public void getUsersInProjectsReport() {
		
		tbProjects.clear();
		
		String projQuery = "SELECT project_name FROM public.projects";
		
		String mainQuery = "";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsProjects, rsMain;
	    
	    try {				
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
		    statement = dbConnection.createStatement();
    		
		    rsProjects = statement.executeQuery(projQuery);    		
    		while (rsProjects.next()) {
    			TableProject pObj = new TableProject();
    			pObj.setProjectName(rsProjects.getString("project_name"));
    			tbProjects.add(pObj);    			
		    }
    		
    		for(int i = 0; i < tbProjects.size(); i++) {    			
    			int m = 0, f = 0;
    			mainQuery = "SELECT user_login, user_name, user_surname, user_position_name, user_register_date, user_degree_name, user_gender_name" + 
    					"	FROM public.users, public.user_positions, public.user_genders, public.user_degrees" + 
    					"   WHERE user_project = (SELECT project_id FROM public.projects WHERE project_name = '" + tbProjects.get(i).getProjectName() + "') " + 
    					"   AND user_position = user_position_id" + 
    					"   AND user_degree = user_degree_id" + 
    					"   AND user_gender = user_gender_id";
    			
    			rsMain = statement.executeQuery(mainQuery);
    			while (rsMain.next()) {
        			User uObj = new User();
        			uObj.setLogin(rsMain.getString("user_login"));
        			uObj.setName(rsMain.getString("user_name"));
        			uObj.setSurname(rsMain.getString("user_surname"));
        			uObj.setPosition(rsMain.getString("user_position_name"));
        			uObj.setRegDate(rsMain.getString("user_register_date"));
        			uObj.setDegree(rsMain.getString("user_degree_name"));
        			uObj.setGender(rsMain.getString("user_gender_name"));
        			//тут возможно потребуется setUsers()
        			tbProjects.get(i).getUsers().add(uObj); 
        			
        			if(uObj.getGender().equals("Male")) {
        				m++;
        			} else if (uObj.getGender().equals("Female")) {
        				f++;
        			}
    		    }
    			tbProjects.get(i).setFemales(f);
    			tbProjects.get(i).setMales(m);
    			
    		}
    		
    		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "");
    		FacesContext.getCurrentInstance().addMessage(null, msg); 
    		
    		
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "Something went wrong");
    		FacesContext.getCurrentInstance().addMessage(null, msg);
		    return;
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
	
	public void getUsersInDegreesReport() {
		
		tbDegrees.clear();
		
		String degQuery = "SELECT user_degree_name FROM public.user_degrees";
		
		String mainQuery = "";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsDegrees, rsMain;
	    
	    try {				
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
		    statement = dbConnection.createStatement();
    		
		    rsDegrees = statement.executeQuery(degQuery);    		
    		while (rsDegrees.next()) {
    			TableDegree pObj = new TableDegree();
    			pObj.setDegreeName(rsDegrees.getString("user_degree_name"));
    			tbDegrees.add(pObj);    			
		    }
    		
    		for(int i = 0; i < tbDegrees.size(); i++) {    			
    			int m = 0, f = 0;
    			mainQuery = "SELECT user_login, user_name, user_surname, user_position_name, user_register_date, project_name, user_gender_name" + 
    					"	FROM public.users, public.user_positions, public.user_genders, public.projects" + 
    					"   WHERE user_degree = (SELECT user_degree_id FROM public.user_degrees WHERE user_degree_name = '" + tbDegrees.get(i).getDegreeName() + "') " + 
    					"   AND user_position = user_position_id" + 
    					"   AND user_project = project_id" + 
    					"   AND user_gender = user_gender_id";
    			
    			rsMain = statement.executeQuery(mainQuery);
    			while (rsMain.next()) {
        			User uObj = new User();
        			uObj.setLogin(rsMain.getString("user_login"));
        			uObj.setName(rsMain.getString("user_name"));
        			uObj.setSurname(rsMain.getString("user_surname"));
        			uObj.setPosition(rsMain.getString("user_position_name"));
        			uObj.setRegDate(rsMain.getString("user_register_date"));
        			uObj.setProject(rsMain.getString("project_name"));
        			uObj.setGender(rsMain.getString("user_gender_name"));
        			//тут возможно потребуется setUsers()
        			tbDegrees.get(i).getUsers().add(uObj); 
        			
        			if(uObj.getGender().equals("Male")) {
        				m++;
        			} else if (uObj.getGender().equals("Female")) {
        				f++;
        			}
    		    }
    			tbDegrees.get(i).setFemales(f);
    			tbDegrees.get(i).setMales(m);
    			
    		}
    		
    		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "");
    		FacesContext.getCurrentInstance().addMessage(null, msg); 
    		
    		
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "Something went wrong");
    		FacesContext.getCurrentInstance().addMessage(null, msg);
		    return;
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
	
	public void getHuredAndDismissalStaff() {
		if(fromDate.length() == 0) {
			fromDate = "2018-01-01";
		}
		if(toDate.length() == 0) {
			toDate = "2018-12-31";
		}
		
		fillHiredStaff();
		fillDismissedStaffStaff();
	}
	
	
	private void fillHiredStaff() {
		hiredStaff.clear();	
		
		String mainQuery = "SELECT user_login, user_name, user_surname, project_name, user_position_name, user_register_date, user_degree_name, user_gender_name, user_dismissal_date" + 
				"	FROM public.users, public.projects, public.user_positions, public.user_degrees, public.user_genders" + 
				"    WHERE user_register_date BETWEEN '" + fromDate + "' AND '" + toDate + "'" + 
				"    AND user_project = project_id" + 
				"    AND user_position = user_position_id" + 
				"    AND user_degree = user_degree_id" + 
				"    AND user_gender = user_gender_id";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsMain;
	    
	    try {				
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
		    statement = dbConnection.createStatement();
    			
			rsMain = statement.executeQuery(mainQuery);
			while (rsMain.next()) {
    			User uObj = new User();
    			uObj.setLogin(rsMain.getString("user_login"));
    			uObj.setName(rsMain.getString("user_name"));
    			uObj.setSurname(rsMain.getString("user_surname"));
    			uObj.setPosition(rsMain.getString("user_position_name"));
    			uObj.setRegDate(rsMain.getString("user_register_date"));
    			uObj.setProject(rsMain.getString("project_name"));
    			uObj.setGender(rsMain.getString("user_gender_name"));
    			uObj.setDegree(rsMain.getString("user_degree_name"));
    			uObj.setDismissalDate(rsMain.getString("user_dismissal_date"));
    			hiredStaff.add(uObj);    			
		    }
    		
    		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "");
    		FacesContext.getCurrentInstance().addMessage(null, msg); 
    		
    		
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "Something went wrong");
    		FacesContext.getCurrentInstance().addMessage(null, msg);
		    return;
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
	
	private void fillDismissedStaffStaff() {
		dismissedStaff.clear();	
		
		String mainQuery = "SELECT user_login, user_name, user_surname, project_name, user_position_name, user_register_date, user_degree_name, user_gender_name, user_dismissal_date" + 
				"	FROM public.users, public.projects, public.user_positions, public.user_degrees, public.user_genders" + 
				"    WHERE user_dismissal_date BETWEEN '" + fromDate + "' AND '" + toDate + "'" + 
				"    AND user_project = project_id" + 
				"    AND user_position = user_position_id" + 
				"    AND user_degree = user_degree_id" + 
				"    AND user_gender = user_gender_id";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsMain;
	    
	    try {				
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
		    statement = dbConnection.createStatement();
    			
			rsMain = statement.executeQuery(mainQuery);
			while (rsMain.next()) {
    			User uObj = new User();
    			uObj.setLogin(rsMain.getString("user_login"));
    			uObj.setName(rsMain.getString("user_name"));
    			uObj.setSurname(rsMain.getString("user_surname"));
    			uObj.setPosition(rsMain.getString("user_position_name"));
    			uObj.setRegDate(rsMain.getString("user_register_date"));
    			uObj.setProject(rsMain.getString("project_name"));
    			uObj.setGender(rsMain.getString("user_gender_name"));
    			uObj.setDegree(rsMain.getString("user_degree_name"));
    			uObj.setDismissalDate(rsMain.getString("user_dismissal_date"));
    			dismissedStaff.add(uObj);    			
		    }
    		
    		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "");
    		FacesContext.getCurrentInstance().addMessage(null, msg); 
    		
    		
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "Something went wrong");
    		FacesContext.getCurrentInstance().addMessage(null, msg);
		    return;
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
