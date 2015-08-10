package com.androditry;

public class CustomQuesListItem {
	private String name;
	private String text;
	
	private int numAns;
	
	public CustomQuesListItem(String Name, String Text, int NumAns)
	{
		name = Name;
		text = Text;
		numAns = NumAns;
	}
	
	public void setName(String Name)
	{
		name = Name;
	}
	public String getName()
	{
		return name;
	}
	
	public void setText(String Text)
	{
		text = Text;
	}
	public String getText()
	{
		return text;
	}
	
	public void setNumAns(int NumAns)
	{
		numAns = NumAns;
	}
	public int getNumAns()
	{
		return numAns;
	}
	
	public boolean showNotif()
	{
		return (numAns==0);
	}
}
