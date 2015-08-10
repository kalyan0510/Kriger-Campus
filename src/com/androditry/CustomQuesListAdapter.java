package com.androditry;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomQuesListAdapter extends BaseAdapter {
	private ArrayList<CustomQuesListItem> listData;
	private LayoutInflater layoutInflater;

	public CustomQuesListAdapter(Context aContext, ArrayList<CustomQuesListItem> listData) {
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
		return position + Utilities.CUSTOM_QUES_LIST_ITEM_ID_OFFSET;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_item_ques, null);
			holder = new ViewHolder();
			holder.nameView = (TextView) convertView.findViewById(R.id.tvNameLIQues);
			holder.textView = (TextView) convertView.findViewById(R.id.tvTextLIQues);
			holder.notifView = (TextView) convertView.findViewById(R.id.tvNotifLIQues);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		CustomQuesListItem obj = listData.get(position);
		holder.nameView.setText(obj.getName());
		holder.textView.setText(obj.getText());
		if(obj.showNotif())
			holder.notifView.setVisibility(View.VISIBLE);
		else
			holder.notifView.setVisibility(View.GONE);
		
		return convertView;
	}

	static class ViewHolder {
		TextView nameView;
		TextView textView;
		TextView notifView;
	}
}