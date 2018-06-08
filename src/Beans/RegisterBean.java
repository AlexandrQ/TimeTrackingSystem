package Beans;

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


@ManagedBean(name = "regBean")
@ViewScoped
public class RegisterBean {
	
	private User regUser = new User();
	private User selectedUser;
	private ArrayList<User> currentUsers = new ArrayList<>();
	
	private List<String> statuses;
	private List<String> degrees;
	private List<String> roles;
	private List<String> positions;
	private List<String> projects;
	
	@ManagedProperty(value="#{mainBean}")
	private MainBean mb;

	
	
	

	public List<String> getProjects() {
		return projects;
	}

	public void setProjects(List<String> projects) {
		this.projects = projects;
	}

	public List<String> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<String> statuses) {
		this.statuses = statuses;
	}

	public List<String> getDegrees() {
		return degrees;
	}

	public void setDegrees(List<String> degrees) {
		this.degrees = degrees;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public List<String> getPositions() {
		return positions;
	}

	public void setPositions(List<String> positions) {
		this.positions = positions;
	}

	public User getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}

	public ArrayList<User> getCurrentUsers() {
		return currentUsers;
	}

	public void setCurrentUsers(ArrayList<User> currentUsers) {
		this.currentUsers = currentUsers;
	}

	public User getRegUser() {
		return regUser;
	}

	public void setRegUser(User regUser) {
		this.regUser = regUser;
	}

	public MainBean getMb() {
		return mb;
	}

	public void setMb(MainBean mb) {
		this.mb = mb;
	}
	
	public void register() {
		
		FacesMessage msg = null;
		
		if(CheckUserDuplicate(regUser.getLogin())) {
			if(CreateUser()) {					
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration was successful", "");
				regUser = new User();
				SelectCurrentUsers();
			} else {					
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Register message", "Error!");
			}			
		} else {				
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Register message", "Error! User with such login already exists");			
			
		}
		
        FacesContext.getCurrentInstance().addMessage(null, msg);       
	}
	
	
	private boolean CreateUser() {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance( "SHA-256" );
		} catch (NoSuchAlgorithmException e1) {			
			e1.printStackTrace();
		}
		
		if (regUser.getPassword() != null) {
			md.update( regUser.getPassword().getBytes( StandardCharsets.UTF_8 ) );
		    byte[] digest = md.digest();
		    regUser.setPassword(String.format( "%064x", new BigInteger( 1, digest ) ));
		}
		else return false;
		
	    
	    
		if (regUser.getLogin() != null && regUser.getName() != null && regUser.getSurname() != null &&  regUser.getPassword() != null && regUser.getProject() != null && regUser.getPosition() != null && regUser.getRole() != null && regUser.getStatus() != null && regUser.getDegree() != null) {
			String querryStr = "INSERT INTO public.users(user_login, user_password, user_name, user_surname, user_project, user_position, user_degree, user_role, user_status, user_register_date)" + 
					"	VALUES ('" + regUser.getLogin() + "', '" + regUser.getPassword() +"', '" + regUser.getName() + "', '" + regUser.getSurname() + "', " + 
					"            (SELECT project_id FROM public.projects WHERE project_name = '" + regUser.getProject() + "'), " + 
					"            (SELECT user_position_id FROM public.user_positions WHERE user_position_name = '" + regUser.getPosition() + "'), " + 
					"            (SELECT user_degree_id FROM public.user_degrees WHERE user_degree_name = '" + regUser.getDegree() + "'), " + 
					"            (SELECT user_role_id FROM public.user_roles WHERE user_role_name = '" + regUser.getRole() + "'), " + 
					"            (SELECT user_status_id FROM public.user_statuses WHERE user_status_name = '" + regUser.getStatus() + "'), " + 
					"            '" + LocalDate.now().toString() +"');";
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
	
	private boolean CheckUserDuplicate(String login) {
		String querryStr = "SELECT COUNT(user_login) AS Count FROM public.\"users\" WHERE user_login = '" + login + "'";
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
	
	
	public void SelectCurrentUsers(){
		currentUsers.clear();
		
		User tempUser;
		String querryStrUser = "SELECT user_login, user_name, user_surname, project_name, user_position_name, user_role_name, user_register_date, user_degree_name, user_status_name" + 
				"	FROM public.users, public.projects, public.user_positions, public.user_roles, public.user_degrees, public.user_statuses" + 
				"    WHERE user_project = project_id" + 
				"    AND user_position = user_position_id" + 
				"    AND user_role = user_role_id" +
				"    AND user_degree = user_degree_id" +
				"    AND user_status = user_status_id";
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsUser;
		
		try {				
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
		    statement = dbConnection.createStatement();
    		rsUser = statement.executeQuery(querryStrUser);
    		
    		while (rsUser.next()) {	
    			tempUser = new User();
    			
    			tempUser.setLogin(rsUser.getString("user_login"));
    				tempUser.setName(rsUser.getString("user_name"));	 
		    		tempUser.setSurname(rsUser.getString("user_surname"));	
		    		tempUser.setProject(rsUser.getString("project_name"));	
		    		tempUser.setPosition(rsUser.getString("user_position_name"));
		    		tempUser.setRole(rsUser.getString("user_role_name"));
		    		tempUser.setRegDate(rsUser.getString("user_register_date"));
		    		tempUser.setStatus(rsUser.getString("user_status_name"));
		    		tempUser.setDegree(rsUser.getString("user_degree_name"));
		    		
		    		currentUsers.add(tempUser);
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
		
		fillListsForSelectMenus();
	}
	
	
	public void disableUser() {
		
		FacesMessage msg = null;
		String queryStrDelete = "UPDATE public.users SET user_status=2 WHERE user_login='" + selectedUser.getLogin() + "';";
		
		Connection dbConnection = null;
		Statement statement = null;
		    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();		
		    
		    if (!(statement.executeUpdate(queryStrDelete) == 1)) {
		    	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "Disable was unsuccessful");		    			    	
		    }
		    else {
		    	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Disable was successful", "");
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
		
	    SelectCurrentUsers();
        FacesContext.getCurrentInstance().addMessage(null, msg);    
	}
	
	public void enableUser() {
		
		FacesMessage msg = null;
		String queryStrDelete = "UPDATE public.users SET user_status=1 WHERE user_login='" + selectedUser.getLogin() + "';";
		
		Connection dbConnection = null;
		Statement statement = null;
		    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();		
		    
		    if (!(statement.executeUpdate(queryStrDelete) == 1)) {
		    	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error!", "Enable was unsuccessful");		    	 		    	
		    }
		    else {
		    	msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Enable was successful", "");
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
		
	    SelectCurrentUsers();
        FacesContext.getCurrentInstance().addMessage(null, msg);    
	}
	
	private void fillListsForSelectMenus() {
		
		statuses = new ArrayList<String>();
		degrees = new ArrayList<String>();
		positions = new ArrayList<String>();
		roles = new ArrayList<String>();
		projects = new ArrayList<String>();
		
		String queryStrStatuses = "SELECT user_status_name FROM public.user_statuses";
		String queryStrDegrees = "SELECT user_degree_name FROM public.user_degrees";
		String queryStrPositions = "SELECT user_position_name FROM public.user_positions";
		String queryStrRoles = "SELECT user_role_name FROM public.user_roles";
		String queryStrProjects = "SELECT project_name FROM public.projects";
		
		Connection dbConnection = null;
		Statement statement = null;
		ResultSet rsStatuses, rsDegrees, rsPos, rsRoles, rsProj;
		
		try {				
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
		    statement = dbConnection.createStatement();
		    
		    rsStatuses = statement.executeQuery(queryStrStatuses);    		
    		while (rsStatuses.next()) {	
    			statuses.add(rsStatuses.getString("user_status_name"));
		    }
    		
    		rsDegrees = statement.executeQuery(queryStrDegrees);    		
    		while (rsDegrees.next()) {	
    			degrees.add(rsDegrees.getString("user_degree_name"));
		    }
    		
    		rsPos = statement.executeQuery(queryStrPositions);    		
    		while (rsPos.next()) {	
    			positions.add(rsPos.getString("user_position_name"));
		    }
    		
    		rsRoles = statement.executeQuery(queryStrRoles);    		
    		while (rsRoles.next()) {	
    			roles.add(rsRoles.getString("user_role_name"));
		    }
    		
    		rsProj = statement.executeQuery(queryStrProjects);    		
    		while (rsProj.next()) {	
    			projects.add(rsProj.getString("project_name"));
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
