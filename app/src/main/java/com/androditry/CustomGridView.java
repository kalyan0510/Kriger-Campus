package com.androditry;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Abhinav Tripathi on 23-Sep-15.
 */
public class CustomGridView extends BaseAdapter{
    private Context mContext;
    private final String[] web;
    private final int[] Imageid;

    public CustomGridView(Context c,String[] web,int[] Imageid ) {
        mContext = c;
        this.Imageid = Imageid;
        this.web = web;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return web.length;
    }

    @Override
    public Object getItem(int position) {
        return web[position];
    }

    public String getString(int position)
    {
        return web[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return Utilities.CUSTOM_CAT_LIST_ITEM_ID_OFFSET + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_item_single, null);
            grid.setMinimumWidth(Utilities.ScreenWidth / 3 - 10);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
            textView.setText(web[position]);
            textView.setTypeface(Utilities.FontTypeFace);
            imageView.setImageResource(Imageid[position]);
        } else {
            grid = (View) convertView;
        }

        return grid;
    }
}