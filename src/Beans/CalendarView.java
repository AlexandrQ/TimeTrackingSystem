package Beans;

import javax.faces.bean.ManagedBean;
import java.util.Date;

@ManagedBean(name = "calendarBean")
public class CalendarView {
	private Date date1;

	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}
	
	
		
}
