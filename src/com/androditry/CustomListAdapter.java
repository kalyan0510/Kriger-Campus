package com.androditry;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomListAdapter extends BaseAdapter {
	private ArrayList<CustomListItem> listData;
	private LayoutInflater layoutInflater;

	public CustomListAdapter(Context aContext, ArrayList<CustomListItem> listData) {
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
		return position;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_item_layout, null);
			holder = new ViewHolder();
			holder.nameView = (TextView) convertView.findViewById(R.id.tvNameLI);
			holder.textView = (TextView) convertView.findViewById(R.id.tvTextLI);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.nameView.setText(listData.get(position).getName());
		holder.textView.setText(listData.get(position).getText());
		
		return convertView;
	}

	static class ViewHolder {
		TextView nameView;
		TextView textView;
	}
}