package Beans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.DateViewChangeEvent;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Collections;

@ManagedBean(name = "calendarBean")
public class CalendarView {
	private Date date1;
	private int selectedWeek;
	private ArrayList<LocalDate> daysInSelectedWeek = new ArrayList<>();

	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}
	
	
		
}
