package com.androditry;

public class CustomListItem {
	private String name;
	private String text;
	
	public CustomListItem(String Name, String Text)
	{
		name = Name;
		text = Text;
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
}
