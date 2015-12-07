package com.androditry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
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
			tagName = tagName.replace(' ', '_');
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

	public static UserType getUserType(ParseUser user)
	{
		if(user==null)
			return UserType.USER_TYPE_NONE;

		return ((user.getEmail().endsWith(IPM_EMAIL_SUFFIX)
				&& user.getEmail().startsWith(IPM_EMAIL_PREFIX))) ?
				UserType.USER_TYPE_IPM : UserType.USER_TYPE_SCHOOL;
	}

	public static UserType getCurUserType()
	{
		return getUserType(curUser);
	}

	public static boolean isIPMUser(ParseUser user)
	{
		return getUserType(user) == UserType.USER_TYPE_IPM;
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

	private static Map<String, ParseObject> allInterests = new HashMap<>();
	public static ParseObject getObjectByTagName(String tagName)
	{
        if(allInterests.containsKey(tagName))
			return allInterests.get(tagName);
		return null;
	}
	public static ParseObject getCurTagObject()
	{
		return getObjectByTagName(getCategory());
	}
	public static void setCurTagObject(ParseObject obj)
	{
		allInterests.put(getCategory(), obj);
	}
	
	public static String tagName = "";
	public static void setCategory(String cat)
	{
		tagName = cat.replace(' ','_');
	}
	public static String getItem()
	{
		return tagName;
	}

	public static String getCategory()
	{
		return tagName;
	}

	private static ParseObject curQuesObj;
	public static void setCurQuesObj(ParseObject quesObj)
	{
		curQuesObj = quesObj;
	}
	public static ParseObject getCurQuesObj()
	{
		return curQuesObj;
	}
	
	public static boolean isNetworkAvailable(Context ctx)
	{
		ConnectivityManager ctvMngr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo aNetInfo = ctvMngr.getActiveNetworkInfo();
		return aNetInfo != null && aNetInfo.isAvailable();
	}
	
	public static boolean haveAllTags = false;
	
	private static Map<String, List<ParseObject>> tagQuestions = new HashMap<>();
	public static void saveCurTagQuestions(List<ParseObject> postList) {
		List<ParseObject> curTagQuestions = new ArrayList<>();
		for(ParseObject obj : postList)
		{
			curTagQuestions.add(obj);
		}
		tagQuestions.put(getCategory(), curTagQuestions);
	}
	public static boolean hasTagQuesLoaded(String tagName)
	{
		return tagQuestions.containsKey(tagName);
	}
	public static boolean hasCurTagQuesLoaded()
	{
		return tagQuestions.containsKey(getCategory());
	}
	public static List<ParseObject> getCurTagQuestions()
	{
		if(tagQuestions.containsKey(getCategory()))
			return tagQuestions.get(getCategory());
		return null;
	}
	public static ParseObject getQuesObjectByQIndex(int index)
	{
		if(!tagQuestions.containsKey(getCategory()))
			return null;
		List<ParseObject> curTagQuestions = tagQuestions.get(getCategory());
		return curTagQuestions.get(index);
	}

	private static Map<String, List<ParseObject>> quesAnswers = new HashMap<>();
	public static void saveCurQuesAnswers(List<ParseObject> postList) {
		List<ParseObject> curQuesAns = new ArrayList<>();
		for(ParseObject obj : postList)
		{
			curQuesAns.add(obj);
		}
		quesAnswers.put(getCurQuesObj().getObjectId(), curQuesAns);
	}
	public static boolean hasQuesAnsLoaded(String qID)
	{
		return quesAnswers.containsKey(qID);
	}
	public static boolean hasCurQuesAnsLoaded()
	{
		return quesAnswers.containsKey(getCurQuesObj().getObjectId());
	}
	public static List<ParseObject> getCurQuesAnswers()
	{
		if(quesAnswers.containsKey(getCurQuesObj().getObjectId()))
			return quesAnswers.get(getCurQuesObj().getObjectId());
		return null;
	}
	public static ParseObject getAnsObjectByAIndex(int index)
	{
		if(!quesAnswers.containsKey(getCurQuesObj().getObjectId()))
			return null;
		List<ParseObject> curQuesAns = quesAnswers.get(getCurQuesObj().getObjectId());
		return curQuesAns.get(index);
	}

	private static Map<String, ParseUser> usersObjects = new HashMap<>();
	public static void saveUserObject(ParseUser usr)
	{
		if(usr == null) return;
		usersObjects.put(usr.getString(alias_UNAME), usr);
	}
	public static boolean hasUserObject(String usrname)
	{
		return usersObjects.containsKey(usrname);
	}
	public static ParseUser getUserObject(String usrname)
	{
		if(!usersObjects.containsKey(usrname))
			return null;
		return usersObjects.get(usrname);
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

	public final static String[] AllCategoriesNamesString = {"Test", "Interview", "Campus", "Academics",
					"Sports", "Ragging", "Office", "Faculty", "Fun N Party"};
	public final static int[] AllCategoriesImagesIds = {R.drawable.test, R.drawable.interview,
					R.drawable.campus, R.drawable.academics, R.drawable.sports, R.drawable.ragging,
					R.drawable.office, R.drawable.faculty, R.drawable.funnparty};
	public static boolean[] AllCategoriesHaveNotif = {false, false, false, false, false, false,
														false, false, false};

	public static int ScreenWidth =0, ScreenHeight =0;
	public static void CalcScreenWH(Activity ctx)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		ctx.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		ScreenWidth = metrics.widthPixels;
		ScreenHeight = metrics.heightPixels;
	}
	public static Typeface FontTypeFace = null;
	//public static List<String> AllCategoriesNames = new ArrayList<String>(AllCategoriesNamesString);

	public static Context contextLogout;
	public static class LogoutTask extends AsyncTask<Void,Void, Void> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(contextLogout);
			pd.setMessage("Logging out...\nPlease wait...");
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			Utilities.logOutCurUser();
			Intent i = new Intent(contextLogout, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			contextLogout.startActivity(i);
			return null;
		}

		@Override
		protected void onPostExecute(Void state) {
			pd.setMessage("");
			pd.dismiss();
		}
	}
}
