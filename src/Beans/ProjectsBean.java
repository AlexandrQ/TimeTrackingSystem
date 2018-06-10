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
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import dbCon.SingletonDBConnection;
import entity.User;


@ManagedBean(name = "projBean")
@ViewScoped
public class ProjectsBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String newProject;
	private String selectedProject;
	private List<String> currentProjects = new ArrayList<>();
	
	@ManagedProperty(value="#{mainBean}")
	private MainBean mb;
	
	

	public String getNewProject() {
		return newProject;
	}

	public void setNewProject(String newProject) {
		this.newProject = newProject;
	}

	public String getSelectedProject() {
		return selectedProject;
	}

	public void setSelectedProject(String selectedProject) {
		this.selectedProject = selectedProject;
	}

	public List<String> getCurrentProjects() {
		return currentProjects;
	}

	public void setCurrentProjects(List<String> currentProjects) {
		this.currentProjects = currentProjects;
	}

	public MainBean getMb() {
		return mb;
	}

	public void setMb(MainBean mb) {
		this.mb = mb;
	}
	
	public void CreateNewProject() {
		
		FacesMessage msg = null;
		
		if(CheckProjectDuplicate()) {
			if(CreateProject()) {					
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Project was created successful", "");
				newProject = "";
				SelectCurrentProjects();
			} else {					
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "");
			}			
		} else {				
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "Project already exists");			
		}
		
        FacesContext.getCurrentInstance().addMessage(null, msg);       
	}
	
	
	private boolean CreateProject() {
	    
		if (newProject.length() > 0) {
			String querryStr = "INSERT INTO public.projects(project_name) VALUES ('" + newProject + "');";
			Connection dbConnection = null;
		    Statement statement = null;
		    
		    try {
			    dbConnection = SingletonDBConnection.getInstance().getConnInst();
			    statement = dbConnection.createStatement();		 
			    
			    statement.executeUpdate(querryStr);			    
			    
			    if (dbConnection != null) {
			    	dbConnection.close();	
			    }		        		
			    return true;			    
			} catch (SQLException e) {
			    System.out.println(e.getMessage());	
			    
			    if (dbConnection != null) {
		            try {
						dbConnection.close();
					} catch (SQLException ee) {				
						ee.printStackTrace();
					}		            
		        }				    
			    return false;
			} 		    
		}
		else return false;
	}
	
	private boolean CheckProjectDuplicate() {
		String querryStr = "SELECT COUNT(project_name) AS Count FROM public.\"projects\" WHERE project_name = '" + newProject + "'";
		Connection dbConnection = null;
	    Statement statement = null;
		
		try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();		 
		    
		    ResultSet rs = statement.executeQuery(querryStr);		    
		    
		    if (rs.next()) {		    	
		    	if(rs.getString("Count").equals("0") ) {
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
	
	
	public void SelectCurrentProjects(){
		currentProjects.clear();		
		
		String querryStrProjects = "SELECT project_name FROM public.projects;";
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsProjects;
		
		try {				
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
		    statement = dbConnection.createStatement();
    		rsProjects = statement.executeQuery(querryStrProjects);
    		
    		while (rsProjects.next()) {
    			currentProjects.add(rsProjects.getString("project_name"));    			
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
	
	public void deleteProject() {
		
		FacesMessage msg = null;
		String queryStrDelete = "DELETE FROM public.projects WHERE project_name = '" + selectedProject + "';";
		
		Connection dbConnection = null;
		Statement statement = null;
		    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();		
		    
		    if (!(statement.executeUpdate(queryStrDelete) == 1)) {
		    	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "Deleting was unsuccessful");		    	 		    	
		    }
		    else {
		    	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Project was delete successful");
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
		
	    SelectCurrentProjects();
        FacesContext.getCurrentInstance().addMessage(null, msg); 
		 
	}
}
