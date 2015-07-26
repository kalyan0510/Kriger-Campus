package com.androditry;

public class CustomCatListItem {
	private String name;
	private int numNotif;
	
	public CustomCatListItem(String Name, int NumNotif)
	{
		name = Name;
		numNotif = NumNotif;
	}
	
	public void setName(String Name)
	{
		name = Name;
	}
	public String getName()
	{
		return name;
	}
	
	public void setNumNotifications(int NumNotifs)
	{
		numNotif = NumNotifs;
	}
	public int getNumNotifications()
	{
		return numNotif;
	}
}
