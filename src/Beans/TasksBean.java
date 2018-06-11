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


@ManagedBean(name = "tasksBean")
@ViewScoped
public class TasksBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String newTask;
	private String newTaskGroup;
	private String selectedTask;
	private String selectedTaskGroup;
	private List<String> currentTasks = new ArrayList<>();
	private List<String> currentTaskGroups = new ArrayList<>();
	
	@ManagedProperty(value="#{mainBean}")
	private MainBean mb;

	
	
	
	public String getNewTask() {
		return newTask;
	}

	public void setNewTask(String newTask) {
		this.newTask = newTask;
	}

	public String getNewTaskGroup() {
		return newTaskGroup;
	}

	public void setNewTaskGroup(String newTaskGroup) {
		this.newTaskGroup = newTaskGroup;
	}

	public String getSelectedTask() {
		return selectedTask;
	}

	public void setSelectedTask(String selectedTask) {
		this.selectedTask = selectedTask;
	}

	public String getSelectedTaskGroup() {
		return selectedTaskGroup;
	}

	public void setSelectedTaskGroup(String selectedTaskGroup) {
		this.selectedTaskGroup = selectedTaskGroup;
	}

	public List<String> getCurrentTasks() {
		return currentTasks;
	}

	public void setCurrentTasks(List<String> currentTasks) {
		this.currentTasks = currentTasks;
	}

	public List<String> getCurrentTaskGroups() {
		return currentTaskGroups;
	}

	public void setCurrentTaskGroups(List<String> currentTasksGroup) {
		this.currentTaskGroups = currentTasksGroup;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public MainBean getMb() {
		return mb;
	}

	public void setMb(MainBean mb) {
		this.mb = mb;
	}
	
	public void createNewTask() {
		
		FacesMessage msg = null;
		
		if(checkTaskDuplicate()) {
			if(createTask()) {					
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Task was created successful", "");
				newTask = "";
				selectCurrentTasks();
			} else {					
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "");
			}			
		} else {				
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "Task already exists");			
		}
		
        FacesContext.getCurrentInstance().addMessage(null, msg);       
	}
	
	
	private boolean createTask() {
	    
		if (newTask.length() > 0) {
			String querryStr = "INSERT INTO public.activity_tasks(activity_task_name, activity_task_project) VALUES ('" + newTask + "', (SELECT project_id FROM public.projects WHERE project_name = '" + mb.getUser().getProject() + "'));";
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
	
	private boolean checkTaskDuplicate() {
		String querryStr = "SELECT COUNT(activity_task_name) AS Count FROM public.activity_tasks WHERE activity_task_name = '" + newTask + "'";
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
	
	public void createNewTaskGroup() {
		
		FacesMessage msg = null;
		
		if(checkTaskGroupDuplicate()) {
			if(createTaskGroup()) {					
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Task was created successful", "");
				newTaskGroup = "";
				selectCurrentTaskGroups();
			} else {					
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "");
			}			
		} else {				
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "Task already exists");			
		}
		
        FacesContext.getCurrentInstance().addMessage(null, msg);       
	}
	
	
	private boolean createTaskGroup() {
	    
		if (newTaskGroup.length() > 0) {
			String querryStr = "INSERT INTO public.activity_task_groups (activity_task_group_name, activity_task_group_project) VALUES ('" + newTaskGroup + "',(SELECT project_id FROM public.projects WHERE project_name = '" + mb.getUser().getProject() + "'))";
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
	
	private boolean checkTaskGroupDuplicate() {
		String querryStr = "SELECT COUNT(activity_task_group_name) AS Count FROM public.activity_task_groups WHERE activity_task_group_name = '" + newTaskGroup + "'";
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
	
	public void selectTasksAndGroups() {
		selectCurrentTasks();
		selectCurrentTaskGroups();
	}
	
	
	public void selectCurrentTasks(){
		currentTasks.clear();		
		
		String querryStrTasks = "SELECT activity_task_name FROM public.activity_tasks WHERE activity_task_project = (SELECT project_id FROM public.projects WHERE project_name = '" + mb.getUser().getProject() +"')";                            
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsTasks;
		
		try {				
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
		    statement = dbConnection.createStatement();
    		rsTasks = statement.executeQuery(querryStrTasks);
    		
    		while (rsTasks.next()) {
    			currentTasks.add(rsTasks.getString("activity_task_name"));    			
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
	
	public void selectCurrentTaskGroups(){
		currentTaskGroups.clear();		
		
		String querryStrTasks = "SELECT activity_task_group_name FROM public.activity_task_groups WHERE activity_task_group_project = (SELECT project_id FROM public.projects WHERE project_name = '" + mb.getUser().getProject() +"')";                            
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsTasks;
		
		try {				
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
		    statement = dbConnection.createStatement();
    		rsTasks = statement.executeQuery(querryStrTasks);
    		
    		while (rsTasks.next()) {
    			currentTaskGroups.add(rsTasks.getString("activity_task_group_name"));    			
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
	
	
	
	 public void deleteTask() {		
		 if(selectedTask.length()>0) {
			 FacesMessage msg = null;
				String queryStrDelete = "DELETE FROM public.activity_tasks WHERE activity_task_name = '" + selectedTask + "';";
				
				Connection dbConnection = null;
				Statement statement = null;
				    
			    try {
				    dbConnection = SingletonDBConnection.getInstance().getConnInst();
				    statement = dbConnection.createStatement();		
				    
				    if (!(statement.executeUpdate(queryStrDelete) == 1)) {
				    	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "Deleting was unsuccessful");		    	 		    	
				    }
				    else {
				    	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Task was delete successful");
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
				
			    selectCurrentTasks();
		        FacesContext.getCurrentInstance().addMessage(null, msg);
		 }
	}
	 
	 
	 public void deleteTaskGroup() {		
		 if(selectedTaskGroup.length()>0) {
			 FacesMessage msg = null;
				String queryStrDelete = "DELETE FROM public.activity_task_groups WHERE activity_task_group_name = '" + selectedTaskGroup + "';";
				
				Connection dbConnection = null;
				Statement statement = null;
				    
			    try {
				    dbConnection = SingletonDBConnection.getInstance().getConnInst();
				    statement = dbConnection.createStatement();		
				    
				    if (!(statement.executeUpdate(queryStrDelete) == 1)) {
				    	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "Deleting was unsuccessful");		    	 		    	
				    }
				    else {
				    	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Task group was delete successful");
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
			     
			    selectCurrentTaskGroups();
		        FacesContext.getCurrentInstance().addMessage(null, msg);
		 }
	}
	 
	 
}
