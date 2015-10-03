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
 * A Custom Class to display objects in a grid
 *
 * Created by Abhinav Tripathi on 23-Sep-15.
 */
public class CustomGridView extends BaseAdapter{
    private Context mContext;
    private final String[] web;
    private final int[] Imageid;
    private boolean[] hasNotif;

    public CustomGridView(Context c,String[] web,int[] Imageid, boolean[] hasNotif ) {
        mContext = c;
        this.Imageid = Imageid;
        this.web = web;
        this.hasNotif = hasNotif;
    }

    public void setHasNotif(boolean[] hasNotif)
    {
        this.hasNotif = hasNotif;
    }

    @Override
    public int getCount() {
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
        return Utilities.CUSTOM_CAT_LIST_ITEM_ID_OFFSET + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
            if(hasNotif[position])
                textView.setTextColor(0x1215ee);
            imageView.setImageResource(Imageid[position]);
        } else {
            grid = convertView;
        }

        return grid;
    }
}