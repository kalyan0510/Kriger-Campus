package com.androditry;

import java.util.concurrent.Callable;

public class CustomListItem {
	private String objid ;
	private String name;
	private String text;
	private boolean isTL;
	private int upvt;
	private int dwvt;
	public String clsnm;


	public CustomListItem(String Name, String Text, boolean IsTL)
	{
		name = Name;
		text = Text;
		isTL = IsTL;
	}
	public CustomListItem(String id,String Name, String Text, boolean IsTL,int upt,int dwt,String cl)
	{
		clsnm=cl;
		objid=id;
		name = Name;
		text = Text;
		isTL = IsTL;
		upvt=upt;
		dwvt=dwt;
	}
	public  String getobjid(){
		return objid;
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

	public void setUpvt(int Text)
	{
		upvt = Text;
	}
	public int getUpvt()
	{
		return upvt;
	}

	public void setDwvt(int Text)
	{
		dwvt = Text;
	}
	public int getDwvt()
	{
		return dwvt;
	}


}
