package entity;

public class DaysWeekCls {
	
	private String nameOfDays;
	private String daysClass;
	
	
	public String getNameOfDays() {
		return nameOfDays;
	}
	public void setNameOfDays(String nameOfDays) {
		this.nameOfDays = nameOfDays;
	}
	public String getDaysClass() {
		return daysClass;
	}
	public void setDaysClass(String daysClass) {
		this.daysClass = daysClass;
	}
	
	public DaysWeekCls() {
		
	}
	
	public DaysWeekCls(String nameOfDays, String daysClass) {
		super();
		this.nameOfDays = nameOfDays;
		this.daysClass = daysClass;
	}
	
	
	
}
