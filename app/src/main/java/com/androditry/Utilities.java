package com.androditry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;

public class Utilities {
	protected static final String IPM_EMAIL_SUFFIX = "@iimidr.ac.in";
	protected static final String IPM_EMAIL_PREFIX = "i";
	protected static final String TL_CHANNEL_NAME = "ThoughtLeaders";
	
	public static final int MAX_NOTIFICATIONS = 9;
	public static final int CUSTOM_ANS_LIST_ITEM_ID_OFFSET = 0;
	public static final int CUSTOM_CAT_LIST_ITEM_ID_OFFSET = 1000;
	public static final int CUSTOM_QUES_LIST_ITEM_ID_OFFSET = 2000;
	private static ParseUser curUser=null;
	
	public enum UserType
	{
		USER_TYPE_IPM,
		USER_TYPE_SCHOOL,
		USER_TYPE_NONE
	}
	
	public static class AllClassesNames
	{
		public static final String UserDetails = "Users__Details";
		public static final String[] UserDetailsColumns = new String[]{"username","allTagsFollowed","profilePicApplied","Name"};
		public static final int USER_DETAILS_COLUMN_USERNAME = 0;
		public static final int USER_DETAILS_COLUMN_ALLTAGSFOLLOWED = 1;
		public static final int USER_DETAILS_COLUMN_HASPROFILEPIC = 2;
		public static final int USER_DETAILS_COLUMN_NAME = 3;
		
		public static final String AllTagsList = "All__Tags";
		public static final String[] AllTagsListColumns = new String[]{"tagName","numFollowers","details","numQuestions","isAnonymous","displayOrder"};
		public static final int ALL_TAGS_LIST_TAGNAME = 0;
		public static final int ALL_TAGS_LIST_NUMFOLLOWERS = 1;
		public static final int ALL_TAGS_LIST_DETAILS = 2;
		public static final int ALL_TAGS_LIST_NUMQUESTIONS = 3;
		public static final int ALL_TAGS_LIST_ISANONYMOUS = 4;
		public static final int ALL_TAGS_LIST_DISPORDER = 5;
		
		private static final String TAG_CLASS_PREFIX = "TAG___";
		private static final String TAG_CLASS_SUFFIX = "___";
		public static String getClassNameForTag(String tagName)
		{
			tagName = tagName.replace(' ', '_').replace('(', '_').replace(')','_');
			return (TAG_CLASS_PREFIX + tagName + TAG_CLASS_SUFFIX);
		}
		public static String getTagNameForClass(String className) {
			int prelen = TAG_CLASS_PREFIX.length();
			int postlen = TAG_CLASS_SUFFIX.length();
			return className.substring(prelen, className.length()-postlen);
		}
		
		public static final String[] AnyTagColumns = new String[]{"objectId","quesTitle","quesDetails","questionBy","numAnswers","numAnsSeen"};
		public static final int ANY_TAG_QID = 0;
		public static final int ANY_TAG_QTITLE = 1;
		public static final int ANY_TAG_QDETAILS = 2;
		public static final int ANY_TAG_QBY = 3;
		public static final int ANY_TAG_NUMANSWERS = 4;
		public static final int ANY_TAG_NUMANSSEEN = 5;
		
		private static final String QUES_CLASS_PREFIX="QUES__";
		private static final String QUES_CLASS_SUFFIX="__";
		public static String getClassNameForQues(String id)
		{
			return (QUES_CLASS_PREFIX + id + QUES_CLASS_SUFFIX);
		}
		
		public static final String[] AnyQuesAnswers = new String[]{"answerBy","answerText"};
		public static final int ANY_ANS_BY = 0;
		public static final int ANY_ANS_TEXT = 1;
		
	}
	
	public static String alias_UNAME = AllClassesNames.UserDetailsColumns[AllClassesNames.USER_DETAILS_COLUMN_USERNAME];
	public static String alias_TAGSFOLLOWED = AllClassesNames.UserDetailsColumns[AllClassesNames.USER_DETAILS_COLUMN_ALLTAGSFOLLOWED];
	public static String alias_HASPPIC = AllClassesNames.UserDetailsColumns[AllClassesNames.USER_DETAILS_COLUMN_HASPROFILEPIC];
	public static String alias_UFULLNAME = AllClassesNames.UserDetailsColumns[AllClassesNames.USER_DETAILS_COLUMN_NAME];
	
	public static String alias_TAGNAME = AllClassesNames.AllTagsListColumns[AllClassesNames.ALL_TAGS_LIST_TAGNAME];
	public static String alias_TAGFOLLOWERS = AllClassesNames.AllTagsListColumns[AllClassesNames.ALL_TAGS_LIST_NUMFOLLOWERS];
	public static String alias_TAGDETAILS = AllClassesNames.AllTagsListColumns[AllClassesNames.ALL_TAGS_LIST_DETAILS];
	public static String alias_TAGQUES = AllClassesNames.AllTagsListColumns[AllClassesNames.ALL_TAGS_LIST_NUMQUESTIONS];
	public static String alias_TAGISANON = AllClassesNames.AllTagsListColumns[AllClassesNames.ALL_TAGS_LIST_ISANONYMOUS];
	public static String alias_TAGDISPORDER = AllClassesNames.AllTagsListColumns[AllClassesNames.ALL_TAGS_LIST_DISPORDER];
	
	public static String alias_QID = AllClassesNames.AnyTagColumns[AllClassesNames.ANY_TAG_QID];
	public static String alias_QTITLE = AllClassesNames.AnyTagColumns[AllClassesNames.ANY_TAG_QTITLE];
	public static String alias_QDETAILS = AllClassesNames.AnyTagColumns[AllClassesNames.ANY_TAG_QDETAILS];
	public static String alias_QBY = AllClassesNames.AnyTagColumns[AllClassesNames.ANY_TAG_QBY];
	public static String alias_QNUMANSWERS = AllClassesNames.AnyTagColumns[AllClassesNames.ANY_TAG_NUMANSWERS];
	public static String alias_QNUMANSSEEN = AllClassesNames.AnyTagColumns[AllClassesNames.ANY_TAG_NUMANSSEEN];
	
	public static String alias_ANSBY = AllClassesNames.AnyQuesAnswers[AllClassesNames.ANY_ANS_BY];
	public static String alias_ANSTEXT = AllClassesNames.AnyQuesAnswers[AllClassesNames.ANY_ANS_TEXT];
	
	static public boolean checkLoggedInUser()
	{
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
		    return false;
		}
		curUser = currentUser;
		return true;
	}

	public static void setCurrentUser(ParseUser user) {
		curUser=user;
	}
	
	public static ParseUser getCurrentUser()
	{
		return curUser;
	}
	
	public static String getCurUsername()
	{
		if(curUser==null)
			return "";
		
		return curUser.getUsername();
	}
	
	public static String getCurUserEmail()
	{
		if(curUser==null)
			return "";
		
		return curUser.getEmail();
	}
	
	public static UserType getCurUserType()
	{
		if(curUser==null)
			return UserType.USER_TYPE_NONE;
		
		return ((curUser.getEmail().endsWith(IPM_EMAIL_SUFFIX) 
				&& curUser.getEmail().startsWith(IPM_EMAIL_PREFIX))) ?
						UserType.USER_TYPE_IPM : UserType.USER_TYPE_SCHOOL;
	}
	
	public static String getCurName()
	{
		if(curUser==null)
			return "";
		
		return curUser.getString("Name");
	}

	public static void InitialiseUserDetails(String username) {
		ParseObject init = new ParseObject(AllClassesNames.UserDetails);
		init.put(alias_UNAME, username);
		init.put(alias_TAGSFOLLOWED, "");
		init.put(alias_HASPPIC, false);
		init.saveInBackground();
	}

	public static void logOutCurUser() {
		ParseUser.logOut();
		curUser = null;
	}
	
	private static ArrayList<ParseObject> allTagsDetails;

	public static void storeAllTags(List<ParseObject> postList) {
		allTagsDetails = new ArrayList<>();
		for(ParseObject obj : postList)
		{
			allTagsDetails.add(obj);
		}
	}
	public static ParseObject getObjectByTagName(String tagName)
	{
        if(allTagsDetails == null)
            return null;
		for(ParseObject obj : allTagsDetails)
		{
			if(obj.getString(alias_TAGNAME).equals(tagName))
			{
				return obj;
			}
		}
		return null;
	}
	public static ParseObject getCurTagObject()
	{
		return getObjectByTagName(getCategory().replace(' ', '_'));
	}
	
	private static ParseObject userDetailsObj;
	public static void setUserDetailsObj(ParseObject parseObject) {
		userDetailsObj = parseObject;
	}
	
	public static ParseObject getUserDetailsObj()
	{
		return userDetailsObj;
	}
	
	public static boolean doesFollow(String tagName)
	{
		String allLiked = userDetailsObj.getString(alias_TAGSFOLLOWED);
		for(String likedTag : allLiked.split("-"))
		{
			if(likedTag.equals(tagName))
			{
				return true;
			}
		}
		return false;
	}
	
	public static String tagName = "";
	public static void setCategory(String cat)
	{
		tagName=cat.replace(' ', '_');
	}
	public static String getCategory()
	{
		return tagName;
	}
	
	private static ArrayList<ParseObject> curTagQuestions;
	public static void saveCurTagQuestions(List<ParseObject> postList) {
		curTagQuestions = new ArrayList<>();
		for(ParseObject obj : postList)
		{
			curTagQuestions.add(obj);
		}
		setTagQuesLoaded(getCategory());
	}
	
	public static ParseObject getQuesObjectByQIndex(int index)
	{
		return curTagQuestions.get(index);
	}
	
	private static ParseObject curQuesObj;
	public static void setCurQuesObj(ParseObject quesObjByQID)
	{
		curQuesObj = quesObjByQID;
	}
	public static ParseObject getCurQuesObj()
	{
		return curQuesObj;
	}
	
	private static ArrayList<ParseObject> curQuesAnswers;
	public static void storeAllAnswers(List<ParseObject> postList) {
		curQuesAnswers = new ArrayList<>();
		for(ParseObject obj : postList)
		{
			curQuesAnswers.add(obj);
		}
        setCurQuesAnsLoaded();
	}
	public static ParseObject getAnswerObjByIndex(int index)
	{
		if(curQuesAnswers == null)
			return null;
		return curQuesAnswers.get(index);
	}
	public static String getAnswerUsernameByText(String text)
	{
		for(ParseObject obj : curQuesAnswers)
		{
			if(obj.getString(alias_ANSTEXT).equals(text))
			{
				return obj.getString(alias_ANSBY);
			}
		}
		return "";
	}
	
	public static boolean isNetworkAvailable(Context ctx)
	{
		ConnectivityManager ctvMngr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo aNetInfo = ctvMngr.getActiveNetworkInfo();
		return aNetInfo != null && aNetInfo.isAvailable();
	}
	
	public static boolean haveAllTags = false;
	
	private static Map<String, Boolean> tagQuestionsLoaded = new HashMap<>();
	public static void setTagQuesLoaded(String tagName, boolean newVal)
	{
		tagQuestionsLoaded.put(tagName, newVal);
	}
	public static void setTagQuesLoaded(String tagName)
	{
		tagQuestionsLoaded.put(tagName, true);
	}
	public static void setCurTagQuesLoaded()
	{
		setTagQuesLoaded(getCategory());
	}
	public static boolean hasTagQuesLoaded(String tagName)
	{
		if(!tagQuestionsLoaded.containsKey(tagName))
			return false;
		return tagQuestionsLoaded.get(tagName);
	}
	public static boolean hasCurTagQuesLoaded()
	{
		return hasTagQuesLoaded(getCategory());
	}
	
	private static Map<String, Boolean> quesAnsLoaded = new HashMap<>();
	public static void setQuesAnsLoaded(String quesId, boolean newVal)
	{
		quesAnsLoaded.put(quesId, newVal);
	}
	public static void setQuesAnsLoaded(String quesId)
	{
		quesAnsLoaded.put(quesId, true);
	}
	public static void setCurQuesAnsLoaded()
	{
		setQuesAnsLoaded(getCurQuesObj().getString(alias_QID));
	}
	public static boolean hasQuesAnsLoaded(String quesId)
	{
		if(!quesAnsLoaded.containsKey(quesId))
			return false;
		return quesAnsLoaded.get(quesId);
	}
	public static boolean hasCurQuesAnsLoaded()
	{
		return hasQuesAnsLoaded(getCurQuesObj().getString(alias_QID));
	}
	
	public static void CheckUpdateSubscriptionInBackground()
	{
		if(curUser == null)
			return;
		//List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");
		//if(subscribedChannels == null || subscribedChannels.size() == 0)
		//{
			if(getCurUserType() == UserType.USER_TYPE_IPM)
			{
				ParsePush.subscribeInBackground(TL_CHANNEL_NAME);
			}
			else if(getCurUserType() == UserType.USER_TYPE_SCHOOL)
			{
				ParseInstallation installation = ParseInstallation.getCurrentInstallation();
				installation.put("username", getCurUsername());
				installation.saveInBackground();
			}
		//}
	}

	public static String[] AllCategoriesNamesString = {"Test", "Interview", "Campus", "Academics",
					"Sports", "Ragging", "Office", "Faculty", "Fun N Party"};
	public static int[] AllCategoriesImagesIds = {R.drawable.test, R.drawable.interview,
					R.drawable.campus, R.drawable.academics, R.drawable.sports, R.drawable.ragging,
					R.drawable.office, R.drawable.faculty, R.drawable.funnparty};

	public static int ScreenWidth =0, ScreenHeight =0;
	public static void CalcScreenWH(Activity ctx)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		ctx.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		ScreenWidth = metrics.widthPixels;
		ScreenHeight = metrics.heightPixels;
	}
	public static int GridHeight =0;
	public static Typeface FontTypeFace = null;
	//public static List<String> AllCategoriesNames = new ArrayList<String>(AllCategoriesNamesString);
}
