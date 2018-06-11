package Beans;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;

import dbCon.SingletonDBConnection;
import entity.Activity;
import entity.DaysWeekCls;
import entity.MonthLength;
import entity.NDaysCls;
import entity.Vacation;

@ManagedBean(name = "vacationBean")
@SessionScoped

public class VacationsView implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value="#{mainBean}")
	private MainBean mb;
	
	private ArrayList<Activity> activitiesListYear = new ArrayList<>();	
	private ArrayList<String> classesListYear = new ArrayList<>();
	private ArrayList<String> weekends = new ArrayList<>();
	
	private ArrayList<DaysWeekCls> daysOfWeekInYear = new ArrayList<>();
	private ArrayList<NDaysCls> numbersOfDaysOfWeekYear = new ArrayList<>();
	private ArrayList<MonthLength> monthLengthList = new ArrayList<>(); 
	
	private ArrayList<Vacation> currentVacations  = new ArrayList<>();
	private ArrayList<Vacation> previousVacations  = new ArrayList<>();
	private ArrayList<String> typesL  = new ArrayList<>();
	

	
	
	
	
	

	public ArrayList<Vacation> getPreviousVacations() {
		return previousVacations;
	}

	public void setPreviousVacations(ArrayList<Vacation> previosVacations) {
		this.previousVacations = previosVacations;
	}

	public ArrayList<String> getTypesL() {
		return typesL;
	}

	public void setTypesL(ArrayList<String> typesL) {
		this.typesL = typesL;
	}

	public ArrayList<Vacation> getCurrentVacations() {
		return currentVacations;
	}

	public void setCurrentVacations(ArrayList<Vacation> currentVacations) {
		this.currentVacations = currentVacations;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ArrayList<DaysWeekCls> getDaysOfWeekInYear() {
		return daysOfWeekInYear;
	}

	public void setDaysOfWeekInYear(ArrayList<DaysWeekCls> daysOfWeekInYear) {
		this.daysOfWeekInYear = daysOfWeekInYear;
	}

	public ArrayList<String> getWeekends() {
		return weekends;
	}

	public void setWeekends(ArrayList<String> weekends) {
		this.weekends = weekends;
	}

	public ArrayList<NDaysCls> getNumbersOfDaysOfWeekYear() {
		return numbersOfDaysOfWeekYear;
	}

	public void setNumbersOfDaysOfWeekYear(ArrayList<NDaysCls> numbersOfDaysOfWeekYear) {
		this.numbersOfDaysOfWeekYear = numbersOfDaysOfWeekYear;
	}

	public ArrayList<String> getWeekens() {
		return weekends;
	}

	public void setWeekens(ArrayList<String> weekens) {
		this.weekends = weekens;
	}

	public ArrayList<String> getClassesListYear() {
		return classesListYear;
	}

	public void setClassesListYear(ArrayList<String> classesListYear) {
		this.classesListYear = classesListYear;
	}

	public ArrayList<MonthLength> getMonthLengthList() {
		return monthLengthList;
	}

	public void setMonthLengthList(ArrayList<MonthLength> monthLengthList) {
		this.monthLengthList = monthLengthList;
	}

	public ArrayList<Activity> getActivitiesListYear() {
		return activitiesListYear;
	}

	public void setActivitiesListYear(ArrayList<Activity> activitiesListYear) {
		this.activitiesListYear = activitiesListYear;
	}

	public MainBean getMb() {
		return mb;
	}

	public void setMb(MainBean mb) {
		this.mb = mb;
	}
	
	public void getCurrentAtivities() {		
		
		LocalDate date = LocalDate.now();
		activitiesListYear.clear();	
		
		String str = date.getDayOfWeek().toString();
		ArrayList<LocalDate> daysInSelectedYear = fillDaysInSelectedYear(date);			
		
		String queryStrMonth = "SELECT activity_date, project_name,  activity_proportion,  activity_type_name, activity_task_group_name, activity_task_name, activity_comment, activity_percentage, activity_status_name" + 
				"	FROM public.activities, public.projects, public.activity_types, public.activity_task_groups, public.activity_tasks, public.activity_statuses" + 
				"   WHERE activity_user = (SELECT user_id FROM public.users WHERE user_login = '" + mb.getUser().getLogin() + "' )" + 
				"   AND activity_date between '" + daysInSelectedYear.get(0).toString() + "' and '" + daysInSelectedYear.get(daysInSelectedYear.size()-1).toString() + "'" + 
				"   AND activity_project = project_id" + 
				"   AND activity_type = activity_type_id" + 
				"   AND activity_task_group = activity_task_group_id" + 
				"   AND activity_task = activity_task_id" + 
				"   AND activity_status = activity_status_id";
		
		String queryStrMonthForNotWorkDays = "SELECT DISTINCT activity_date, activity_proportion,  activity_type_name, activity_comment, activity_percentage, activity_status_name" + 
				"	FROM public.activities, public.projects, public.activity_types, public.activity_task_groups, public.activity_tasks, public.activity_statuses" + 
				"	WHERE activity_user = (SELECT user_id FROM public.users WHERE user_login = '" + mb.getUser().getLogin() + "' )" + 
				"	AND activity_date between '" + daysInSelectedYear.get(0).toString() + "' and '" + daysInSelectedYear.get(daysInSelectedYear.size()-1).toString() + "'" + 
				"	AND activity_type = activity_type_id" + 
				"	AND activity_status = activity_status_id" +
				"	AND NOT activity_type = 1";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsMonth, rsMonthNWD;
	    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();	 
		    
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
		    	activitiesListYear.add(activity);		    	
		    }
		    
		    rsMonthNWD = statement.executeQuery(queryStrMonthForNotWorkDays);
		    
		    while (rsMonthNWD.next()) {	
		    	Activity activity = new Activity(rsMonthNWD.getString("activity_date"),
		    			rsMonthNWD.getString("activity_type_name"),
		    			rsMonthNWD.getString("activity_proportion"),		    			
		    			rsMonthNWD.getString("activity_comment"),
		    			rsMonthNWD.getString("activity_percentage"),
		    			rsMonthNWD.getString("activity_status_name"));	 
		    	activitiesListYear.add(activity);		    	
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
	    
	    
	    fillActivitiesListForEmptyDays(daysInSelectedYear);		
	    fillNumberOfDaysAndDaysOfWeek();
	    fillClasesList();
	    fillWeekendsList();
	    fillTypesList();
	    fillCurrentVacations();
	    fillPreviosVacations();
	    //fillDaysForCalendarHighlighter();
	}
	
	
	private ArrayList<LocalDate> fillDaysInSelectedYear(LocalDate date) {
		//+-31 days
		int selectedYear;
		ArrayList<LocalDate> daysInSelectedYear = new ArrayList<>();
		daysInSelectedYear.add(date);
		
		//получаем из LocalDate номер недели
		//WeekFields weekFields = WeekFields.of(Locale.getDefault()); 		
		//selectedWeek = date.get(weekFields.weekOfWeekBasedYear());
		selectedYear = date.getYear();		
		 		
		int numberOfMonth = -1;
		LocalDate myDate1 = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
		LocalDate myDate2;
		
		for (int i = 1; i<365; i++) {
			myDate2 = myDate1.plusDays(i);
			numberOfMonth = myDate2.getYear();
			if (numberOfMonth == selectedYear) daysInSelectedYear.add(myDate2);
			
			myDate2 = myDate1.minusDays(i);
			numberOfMonth = myDate2.getYear();
			if (numberOfMonth == selectedYear) daysInSelectedYear.add(myDate2);
		}
		
		Collections.sort(daysInSelectedYear);
		
		for (int i = 0; i < daysInSelectedYear.size(); i++) {
			System.out.println("(My comment) ArrayYear: " + daysInSelectedYear.get(i) );
		}
		
		return daysInSelectedYear;
	}
	
	private void fillActivitiesListForEmptyDays(ArrayList<LocalDate> daysInSelectedPeriod) {
		
			for(int i = 0; i < activitiesListYear.size(); i++ ) {
				for(int j=0; j < daysInSelectedPeriod.size(); j++) {
					if(activitiesListYear.get(i).getDate().equals(daysInSelectedPeriod.get(j).toString())) {
						daysInSelectedPeriod.remove(j);
						break;
					}
				}
			}
			
			for (int i = 0; i < daysInSelectedPeriod.size(); i++) {
				activitiesListYear.add(new Activity(daysInSelectedPeriod.get(i).toString()));
			}
			
			bubbleSortActivitiesListByDate();
	}
	
	//сортирует только определенный лист activitiesListWeek и activitiesListMonth
	private void bubbleSortActivitiesListByDate() {
		int i = 0;
        int goodPairsCounter = 0;
        LocalDate date1 = null, date2 = null;        
        
        
        	while (true) {        	
                if (date2.parse(activitiesListYear.get(i+1).getDate()).isBefore(date1.parse(activitiesListYear.get(i).getDate()))) {
                    Activity q = activitiesListYear.get(i);
                    activitiesListYear.set(i, activitiesListYear.get(i+1));
                    activitiesListYear.set((i + 1), q);
                    goodPairsCounter = 0;
                } else {
                    goodPairsCounter++;
                }
                i++;
                if (i == activitiesListYear.size() - 1) {
                    i = 0;
                }
                if (goodPairsCounter == activitiesListYear.size() - 1) break;
            }        
	}
	
	private void fillNumberOfDaysAndDaysOfWeek() {
		
		daysOfWeekInYear.clear();
		numbersOfDaysOfWeekYear.clear();
		boolean sw = true, weekendB = true;
		for (Activity obj:activitiesListYear) {
			sw = true;
			weekendB = true;
			
			//daysOfWeekInYear.add(LocalDate.parse(obj.getDate()).getDayOfWeek().toString().substring(0, 2) );
			
			/////////////////////////////////
			
			for(String weekend : weekends) {
				if(obj.getDate().equals(weekend) ) {
					numbersOfDaysOfWeekYear.add(new NDaysCls( LocalDate.parse(obj.getDate()).getDayOfMonth()+"" , "weekendClass" ));
					daysOfWeekInYear.add(new DaysWeekCls( LocalDate.parse(obj.getDate()).getDayOfWeek().toString().substring(0, 2) , "weekendClass" ));
					
					weekendB = false;
					break;
				}
			}
			if (weekendB) {
				numbersOfDaysOfWeekYear.add(new NDaysCls( LocalDate.parse(obj.getDate()).getDayOfMonth()+"" , "notWeekendClass" ));
				daysOfWeekInYear.add(new DaysWeekCls( LocalDate.parse(obj.getDate()).getDayOfWeek().toString().substring(0, 2) , "notWeekendClass" ));
			}
			
			////////////////////////////////
			
			for (MonthLength m : monthLengthList) {
				if (m.getMonth().equals(LocalDate.parse(obj.getDate()).getMonth().toString())) {
					sw = false;
				}
			}
			if (sw) monthLengthList.add(new MonthLength( LocalDate.parse(obj.getDate()).getMonth().toString(), LocalDate.parse(obj.getDate()).getMonth().length(false)+"" )); 
		}
	}
	
	private void fillClasesList() {		
		classesListYear.clear();
		
		for (Activity obj : activitiesListYear) {			
			if (obj.getType().equals("Work day")) {				
				classesListYear.add("wd_class");
			}
			else if (obj.getType().equals("Vacation")) {
				classesListYear.add("vac_class");
			}
			else if (obj.getType().equals("Sick day")) {
				classesListYear.add("sck_class");
			}
			else if (obj.getType().equals("Day off")) {
				classesListYear.add("do_class");
			}
			else if (obj.getType().equals("Not filled")) {
				boolean b = true;
				for(String weekend : weekends) {
					if(obj.getDate().equals(weekend) ) {
						classesListYear.add("weekendClass");
						b = false;
						break;
					}
				}
				if (b) {
					classesListYear.add("nf_class");
				}	
			}
			else {
				classesListYear.add("nf_class");
			}
		}
	}
	
	private void fillWeekendsList() {
		weekends.clear();
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
	
	private void fillTypesList() {
		typesL.clear();
		String queryStrWeekens = "SELECT activity_type_name FROM public.activity_types";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsTypes;
	    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();	 
		    
		    rsTypes = statement.executeQuery(queryStrWeekens);
		    
		    while (rsTypes.next()) {
		    	typesL.add(rsTypes.getString("activity_type_name"));		    	
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
	
	
	private void fillCurrentVacations() {
		currentVacations.clear();
		
		String queryStrVac = "SELECT vacation_start_date, vacation_end_date, vacation_quantity, user_login, activity_type_name, activity_status_name" + 
				"	FROM public.vacations, public.users, public.activity_types, public.activity_statuses" + 
				"    WHERE vacation_user = user_id" + 
				"    AND vacation_user = (SELECT user_id FROM public.users WHERE user_login = '" + mb.getUser().getLogin() + "')" + 
				"    AND vacation_type = activity_type_id" + 
				"    AND vacation_status = activity_status_id" +
				"    AND vacation_end_date >= '" + LocalDate.now() + "'";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsVac;
	    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();	 
		    
		    rsVac = statement.executeQuery(queryStrVac);
		    
		    while (rsVac.next()) {
		    	Vacation vac = new Vacation();		    	 
		    	
		    	vac.setStartDate(LocalDate.parse(rsVac.getString("vacation_start_date")));
		    	vac.setEndDate(LocalDate.parse(rsVac.getString("vacation_end_date")));
		    	vac.setQuantity(Integer.valueOf(rsVac.getString("vacation_quantity")));
		    	vac.setStatus(rsVac.getString("activity_status_name"));
		    	vac.setType(rsVac.getString("activity_type_name"));
		    	vac.setUser(rsVac.getString("user_login"));		    	
		    	
		    	currentVacations.add(vac);		    	
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
	
	private void fillPreviosVacations() {
		previousVacations.clear();
		
		String queryStrVac = "SELECT vacation_start_date, vacation_end_date, vacation_quantity, user_login, activity_type_name, activity_status_name" + 
				"	FROM public.vacations, public.users, public.activity_types, public.activity_statuses" + 
				"    WHERE vacation_user = user_id" + 
				"    AND vacation_user = (SELECT user_id FROM public.users WHERE user_login = '" + mb.getUser().getLogin() + "')" + 
				"    AND vacation_type = activity_type_id" + 
				"    AND vacation_status = activity_status_id" +
				"    AND vacation_end_date < '" + LocalDate.now() + "'";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rsVac;
	    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();	 
		    
		    rsVac = statement.executeQuery(queryStrVac);
		    
		    while (rsVac.next()) {
		    	Vacation vac = new Vacation();		    	 
		    	
		    	vac.setStartDate(LocalDate.parse(rsVac.getString("vacation_start_date")));
		    	vac.setEndDate(LocalDate.parse(rsVac.getString("vacation_end_date")));
		    	vac.setQuantity(Integer.valueOf(rsVac.getString("vacation_quantity")));
		    	vac.setStatus(rsVac.getString("activity_status_name"));
		    	vac.setType(rsVac.getString("activity_type_name"));
		    	vac.setUser(rsVac.getString("user_login"));		    	
		    	
		    	previousVacations.add(vac);		    	
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
	
	public void onRowEdit(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("onRowEdit", "");
        FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", "");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }	
}
