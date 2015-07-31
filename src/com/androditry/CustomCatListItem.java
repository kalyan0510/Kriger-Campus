package com.androditry;

public class CustomCatListItem {
	private String name;
	private int numNotif;
	private boolean isAnon;
	
	public CustomCatListItem(String Name, int NumNotif, boolean IsAnon)
	{
		name = Name;
		numNotif = NumNotif;
		isAnon = IsAnon;
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
	
	public void setIsAnon(boolean IsAnon)
	{
		isAnon = IsAnon;
	}
	public boolean getIsAnon()
	{
		return isAnon;
	}
}
