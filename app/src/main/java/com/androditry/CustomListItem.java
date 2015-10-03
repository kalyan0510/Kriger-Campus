package com.androditry;

public class CustomListItem {
	private String name;
	private String text;
	private boolean isTL;
	
	public CustomListItem(String Name, String Text, boolean IsTL)
	{
		name = Name;
		text = Text;
		isTL = IsTL;
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

	public void setIsTL(boolean IsTL)
	{
		isTL = IsTL;
	}
	public boolean getIsTL()
	{
		return isTL;
	}
}
