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
	
	public void onDateSelect(SelectEvent event) {
		System.out.println("(My comment) onDateSelect->SelectEvent");
		
		//получаем выбранную дату из event
		Date date = (Date) event.getObject();	
		
		//преобразуем Date в LocalDate
		LocalDate dd = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		daysInSelectedWeek.add(dd);
		System.out.println("(My comment) onDateSelect->LocalDate: " + dd);
		
		//получаем из LocalDate номер недели
		WeekFields weekFields = WeekFields.of(Locale.getDefault()); 		
		selectedWeek = dd.get(weekFields.weekOfWeekBasedYear());
		
		filldaysInSelectedWeek(dd);
		
		//получаем номер недели из Date
		//SimpleDateFormat dateFormat = new SimpleDateFormat("w");
		//selectedWeek = dateFormat.format(date);
		
		//обновляем всплывающее сообщение
		FacesMessage message = null;
		message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Selected week", selectedWeek+"");
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	private void filldaysInSelectedWeek(LocalDate date) {
		//+-6 days
		
		WeekFields weekFields = WeekFields.of(Locale.getDefault()); 		
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
			System.out.println("(My comment) Array: " + daysInSelectedWeek.get(i) );
		}
	}
		
}
