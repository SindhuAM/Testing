package com.cerner.vbrick.data;

public class Events 
{
	private String event_ID;
	private String userType;
	private String name;
	private String username;
	private String email;
	private String ipAddress;
	private String browser;
	private String deviceType;
	private String zone;
	private String deviceAccessed;
	private String streamAccessed;
	private String enteredDate;
	private String exitedDate;
	private String viewingTime;
	
	public String getEventID()
	{
		return event_ID;
	}
	
	public String getUserType()
	{
		return userType;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public String getipAddress()
	{
		return ipAddress;
	}
	
	public String getBrowser()
	{
		return browser;
	}
	
	public String getDeviceType()
	{
		return deviceType;
	}
	
	public String getZone()
	{
		return zone;
	}
	
	public String getDeviceAccessed()
	{
		return deviceAccessed;
	}
	
	public String getStreamAccessed()
	{
		return streamAccessed;
	}
	
	public String getEnteredDate()
	{
		return enteredDate;
	}
	
	public String getExitedDate()
	{
		return exitedDate;
	}
	
	public String getViewingTime()
	{
		return viewingTime;
	}
	
	@Override
	public String toString() 
	{
		return "Reports [event_ID=" + event_ID + ", userType=" + userType + ", name=" + name + ", username=" + username + ", email=" + email + ", ipAddress=" + ipAddress + ", browser=" + browser + ", deviceType=" + deviceType +", zone=" + zone +", deviceAccessed=" + deviceAccessed +", streamAccessed=" + streamAccessed +", enteredDate=" + enteredDate +", exitedDate=" + exitedDate +"]";
	}
	
	public void setEvent_ID(String event_ID)
	{
		this.event_ID=event_ID;
	}
	
	public void setUserType(String userType)
	{
		this.userType=userType;
	}
	
	public void setName(String name)
	{
		this.name=name;
	}
	
	public void setUsername(String username)
	{
		this.username=username;
	}
	
	public void setEmail(String email)
	{
		this.email=email;
	}
	
	public void setipAddress(String ipAddress)
	{
		this.ipAddress=ipAddress;
	}
	
	public void setBrowser(String browser)
	{
		this.browser=browser;
	}
	
	public void setDeviceType(String deviceType)
	{
		this.deviceType=deviceType;
	}
	
	public void setZone(String zone)
	{
		this.zone=zone;
	}
	
	public void setDeviceAccessed(String deviceAccessed)
	{
		this.deviceAccessed=deviceAccessed;
	}
	
	public void setStreamAccessed(String streamAccessed)
	{
		this.streamAccessed=streamAccessed;
	}
	
	public void setEnteredDate(String enteredDate)
	{
		this.enteredDate=enteredDate;
	}
	
	public void setExitedDate(String exitedDate)
	{
		this.exitedDate=exitedDate;
	}
	
	public void setViewingTime(String viewingTime)
	{
		this.viewingTime=viewingTime;
	}
}
