package entity;

import java.io.Serializable;
import java.time.LocalDate;

//@ManagedBean(name = "activity")
//@SessionScoped

public class Activity implements Serializable{
	private String date;
	private String type;
	private String proportion;
	private String project;		
	private String taskGroup;
	private String task;
	private String comment;	
	private String percentage;
	private String status;
	
	
	public Activity() {
		
	}	
	
	public Activity(String date) {
		this.date = date;
		this.status = "Not filled";
	}
	
	public Activity(String date, String type, String proportion, String project, String taskGroup, String task,
			String comment, String percentage,  String status) {
		super();
		this.date = date;
		this.project = project;
		this.proportion = proportion;		
		this.type = type;
		this.taskGroup = taskGroup;
		this.task = task;
		this.comment = comment;
		this.percentage = percentage;
		this.status = status;
	}



	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getPercentage() {
		return percentage;
	}
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTaskGroup() {
		return taskGroup;
	}
	public void setTaskGroup(String taskGroup) {
		this.taskGroup = taskGroup;
	}
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getProportion() {
		return proportion;
	}
	public void setProportion(String proportion) {
		this.proportion = proportion;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		status = status;
	}
	
	
}
