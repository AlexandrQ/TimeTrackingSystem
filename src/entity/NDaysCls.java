package entity;

public class NDaysCls {
	
	private String numberOfDays;
	private String daysClass;
	
	
	public String getNumberOfDays() {
		return numberOfDays;
	}
	public void setNumberOfDays(String numberOfDays) {
		this.numberOfDays = numberOfDays;
	}
	public String getDaysClass() {
		return daysClass;
	}
	public void setDaysClass(String daysClass) {
		this.daysClass = daysClass;
	}
	
	public NDaysCls() {
		
	}
	
	public NDaysCls(String numberOfDays, String daysClass) {
		super();
		this.numberOfDays = numberOfDays;
		this.daysClass = daysClass;
	}
	
	
	
}
