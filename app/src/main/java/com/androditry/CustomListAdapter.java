package com.androditry;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class CustomListAdapter extends BaseAdapter {
	private ArrayList<CustomListItem> listData;
	private LayoutInflater layoutInflater;
	Context pars;

	public CustomListAdapter(Context aContext, ArrayList<CustomListItem> listData) {
		this.listData = listData;
		pars=aContext;
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
		return position + Utilities.CUSTOM_ANS_LIST_ITEM_ID_OFFSET;
	}

	@SuppressLint("InflateParams")
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_item_layout, null);
			holder = new ViewHolder();
			holder.nameView  = (TextView) convertView.findViewById(R.id.tvNameLI);
			holder.textView  = (TextView) convertView.findViewById(R.id.tvTextLI);
			holder.notifView = (TextView) convertView.findViewById(R.id.tvNotifLI);
			holder.dwvote = (Button) convertView.findViewById(R.id.dwvt);
			holder.upvote = (Button) convertView.findViewById(R.id.upvt);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}



		//convertView.setTag(holder);


		holder.upvote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String objid = listData.get(position).getobjid();
				ParseObject po;
				if(Utilities.liked.contains(objid)){
					Toast.makeText(pars, "Already given a vote", Toast.LENGTH_SHORT).show();
					return;
				}

				try {

					ParseQuery<ParseObject> query = ParseQuery.getQuery(listData.get(position).clsnm);
					/*Toast.makeText(pars, "Catched Exception "+query.getClassName()+"  "+objid, Toast.LENGTH_SHORT).show();*/
					po = query.get(objid);
					Utilities.liked.add(objid);
					po.put("aUP_VOTE", po.getInt("aUP_VOTE") + 1);
					po.save();
					holder.upvote.setText(po.getInt("aUP_VOTE") + "");
					Toast.makeText(pars, "UpVoted", Toast.LENGTH_SHORT).show();
				} catch (ParseException e) {
					e.printStackTrace();
					//Toast.makeText(pars, "Catched Exception "+e.getMessage(), Toast.LENGTH_SHORT).show();
					Toast.makeText(pars, "Catched Exception "+e.getMessage(), Toast.LENGTH_SHORT).show();
				}

			}

		});
		holder.dwvote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String objid= listData.get(position).getobjid();
				if(Utilities.liked.contains(objid)){
					Toast.makeText(pars, "Already given a vote", Toast.LENGTH_SHORT).show();
					return;
				}
				ParseObject po;
				try {
					ParseQuery<ParseObject> query = ParseQuery.getQuery(listData.get(position).clsnm);
				/*	Toast.makeText(pars, "Catched Exception "+query.getClassName()+"  "+objid, Toast.LENGTH_SHORT).show();*/
					po=query.get(objid);
					po.put("aDW_VOTE",po.getInt("aDW_VOTE")+1);
					po.save();
					Utilities.liked.add(objid);
					holder.dwvote.setText(po.getInt("aDW_VOTE")+"");
					Toast.makeText(pars, "DownVoted", Toast.LENGTH_SHORT).show();
				} catch (ParseException e) {
					e.printStackTrace();
					//Toast.makeText(, "No tags could be loaded due to error:", Toast.LENGTH_SHORT).show();
					Toast.makeText(pars, "Catched Exception "+e.getMessage(), Toast.LENGTH_SHORT).show();
				}

			}

		});

		CustomListItem li = listData.get(position);
		holder.nameView.setText(li.getName());
		holder.textView.setText(li.getText());
		holder.upvote.setText(li.getUpvt()+"");
		holder.dwvote.setText(li.getDwvt()+"");


		if(li.getIsTL())
		{
			holder.nameView.setTextColor(Color.parseColor("#000000"));
			holder.notifView.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.nameView.setTextColor(Color.parseColor("#3178be"));
			holder.notifView.setVisibility(View.GONE);
		}
		return convertView;
	}

	static class ViewHolder {
		TextView nameView;
		TextView notifView;
		TextView textView;
		TextView upvote;
		TextView dwvote;
	}
}