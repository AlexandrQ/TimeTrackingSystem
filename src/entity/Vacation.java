package entity;

import java.io.Serializable;


public class Vacation implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String startDate;
	private String endDate;
	private String user;
	private String type;
	private String status;
	private Integer quantity;
	
	
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {		
			this.startDate = startDate;
	}
	
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {		
		this.endDate = endDate;		
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public Vacation(){
		
	}
	
	public Vacation(String SD, String ED, String typeV){
		super();
		this.startDate = SD;
		this.endDate = ED;
		this.type = typeV;
	}
	
	public Vacation(String Status, String SD, String ED, String days, String typeV){
		super();
		this.startDate = SD;
		this.endDate = ED;
		this.type = typeV;
		this.status = Status;
		this.quantity = Integer.valueOf(days);
	}
	
	
}
