package entity;

public class MonthLength {
	
	private String month;
	private String length;
	
	
	
	
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public MonthLength() {
		
	}
	
	public MonthLength(String m, String l) {
		this.month = m;
		this.length = l;
	}
	
	
}
