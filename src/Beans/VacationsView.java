package Beans;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import dbCon.SingletonDBConnection;
import entity.Activity;
import entity.MonthLength;

@ManagedBean(name = "vacationBean")
@SessionScoped

public class VacationsView implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value="#{mainBean}")
	private MainBean mb;
	
	private ArrayList<Activity> activitiesListYear = new ArrayList<>();
	//private ArrayList<StatusesAndClases> activitiesListYearAndClasses = new ArrayList<>();
	private ArrayList<String> classesListYear = new ArrayList<>();
	
	private ArrayList<String> daysOfWeekInYear = new ArrayList<>();
	private ArrayList<Integer> numbersOfDaysOfWeekYear = new ArrayList<>();
	private ArrayList<MonthLength> monthLengthList = new ArrayList<>(); 
	
	

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

	public ArrayList<String> getDaysOfWeekInYear() {
		return daysOfWeekInYear;
	}

	public void setDaysOfWeekInYear(ArrayList<String> daysOfWeekInYear) {
		this.daysOfWeekInYear = daysOfWeekInYear;
	}

	public ArrayList<Integer> getNumbersOfDaysOfWeekYear() {
		return numbersOfDaysOfWeekYear;
	}

	public void setNumbersOfDaysOfWeekYear(ArrayList<Integer> numbersOfDaysOfWeekYear) {
		this.numbersOfDaysOfWeekYear = numbersOfDaysOfWeekYear;
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
		
		/*LocalDate d = LocalDate.now();
		int year = d.getYear();
		LocalDate startOfYear = LocalDate.of(year, 1, 1);
		LocalDate endOfYear = LocalDate.of(year, 12, 31);*/
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
	    fillStatusesAndClases();
	    //fillDaysForCalendarHighlighter();
	}
	
	
	private ArrayList<LocalDate> fillDaysInSelectedYear(LocalDate date) {
		//+-31 days
		int selectedYear;
		ArrayList<LocalDate> daysInSelectedYear = new ArrayList<>();
		daysInSelectedYear.add(date);
		
		//�������� �� LocalDate ����� ������
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
	
	//��������� ������ ������������ ���� activitiesListWeek � activitiesListMonth
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
		//activitiesListYear 
		//daysOfWeekInYear 
		//numbersOfDaysOfWeekYear
		daysOfWeekInYear.clear();
		numbersOfDaysOfWeekYear.clear();
		boolean sw = true;
		for (Activity obj:activitiesListYear) {
			sw = true;
			daysOfWeekInYear.add(LocalDate.parse(obj.getDate()).getDayOfWeek().toString().substring(0, 2) );
			numbersOfDaysOfWeekYear.add(LocalDate.parse(obj.getDate()).getDayOfMonth());
			
			for (MonthLength m : monthLengthList) {
				if (m.getMonth().equals(LocalDate.parse(obj.getDate()).getMonth().toString())) {
					sw = false;
				}
			}
			if (sw) monthLengthList.add(new MonthLength( LocalDate.parse(obj.getDate()).getMonth().toString(), LocalDate.parse(obj.getDate()).getMonth().length(false)+"" )); 
		}
	}
	
	private void fillStatusesAndClases() {
		//activitiesListYear
		//classesListYear
		
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
			else if (obj.getType().equals("Not filled")) {
				classesListYear.add("nf_class");
			}
			else {
				classesListYear.add("nf_class");
			}
		}
	}
	
	
}
