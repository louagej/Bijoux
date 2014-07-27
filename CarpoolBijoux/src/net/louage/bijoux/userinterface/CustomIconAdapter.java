package net.louage.bijoux.userinterface;
	
	import java.util.List;

import net.louage.bijoux.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

	public class CustomIconAdapter extends BaseAdapter {

	    Context context;
	    List<RowIconItem> rowIconItem;

	    CustomIconAdapter(Context context, List<RowIconItem> row) {
	        this.context = context;
	        this.rowIconItem = row;

	    }

	    @Override
	    public int getCount() {

	        return rowIconItem.size();
	    }

	    @Override
	    public Object getItem(int position) {

	        return rowIconItem.get(position);
	    }

	    @Override
	    public long getItemId(int position) {

	        return rowIconItem.indexOf(getItem(position));
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {

	        if (convertView == null) {
	            LayoutInflater mInflater = (LayoutInflater) context
	                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	            convertView = mInflater.inflate(R.layout.list_item, null);
	        }

	        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
	        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

	        RowIconItem row_pos = rowIconItem.get(position);
	        // setting the image resource and title
	        imgIcon.setImageResource(row_pos.getIcon());
	        txtTitle.setText(row_pos.getTitle());

	        return convertView;

	    }

}
