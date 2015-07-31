package com.androditry;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomCatListAdapter extends BaseAdapter {
	private ArrayList<CustomCatListItem> listData;
	private LayoutInflater layoutInflater;

	public CustomCatListAdapter(Context aContext, ArrayList<CustomCatListItem> listData) {
		this.listData = listData;
		layoutInflater = LayoutInflater.from(aContext);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position + Utilities.CUSTOM_CAT_LIST_ITEM_ID_OFFSET;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_item_category, null);
			holder = new ViewHolder();
			holder.nameView = (TextView) convertView.findViewById(R.id.tvCatName);
			holder.notifView = (TextView) convertView.findViewById(R.id.tvNotification);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String strName = listData.get(position).getName();
		if(listData.get(position).getIsAnon())
			strName += " (Anonymous)";
		holder.nameView.setText(strName);
		int numNotif = listData.get(position).getNumNotifications();
		if(numNotif <= Utilities.MAX_NOTIFICATIONS)
			holder.notifView.setText("" + numNotif);
		else
			holder.notifView.setText(Utilities.MAX_NOTIFICATIONS + "+");
		
		if(numNotif == 0)
			holder.notifView.setVisibility(View.GONE);
		else
			holder.notifView.setVisibility(View.VISIBLE);
		
		return convertView;
	}

	static class ViewHolder {
		TextView nameView;
		TextView notifView;
	}
}