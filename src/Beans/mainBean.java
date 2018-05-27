package Beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "mainBean")
@SessionScoped

public class mainBean {

	private User user;
	private Activity activity;
	
}
