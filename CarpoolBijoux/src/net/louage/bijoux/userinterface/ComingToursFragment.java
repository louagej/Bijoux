package net.louage.bijoux.userinterface;

import java.util.ArrayList;
import java.util.List;

import net.louage.bijoux.R;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ComingToursFragment extends ListFragment implements
		OnItemClickListener {
	public static final String ARG_NAVDRAWER_NUMBER = "number";
	// Example array until php script can return array of coming tours
	// TODO get Array from MySQL
	String[] menutitles = { "Sept 15 2014, 18:00, Anoka, Minneapolis",
			"Sept 15 2014, 15:30, Maple Grove, Lake Elmo",
			"Sept 18 2014, 08:30, Bloomington, Shorewood",
			"Sept 24 2014, 20:15, Minnetonka, Cottage Grove",
			"Sept 25 2014, 18:00, Burnsville, Lake Elmo" };;
	// TypedArray menuIcons;
	CustomIconAdapter adapter;
	private List<RowIconItem> rowIconItems;

	public ComingToursFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int i = getArguments().getInt(ARG_NAVDRAWER_NUMBER);
		MainActivity ma = (MainActivity) getActivity();
		String selNavDrawItem = ma.getmNavDrawerTitles()[i];
		View comingToursView = inflater.inflate(R.layout.fragment_coming_tours,
				container, false);
		getActivity().setTitle(selNavDrawItem);
		return comingToursView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(getActivity(), menutitles[position], Toast.LENGTH_SHORT)
				.show();

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		// menuIcons =
		// getResources().obtainTypedArray(R.array.list_item_img_icons);
		rowIconItems = new ArrayList<RowIconItem>();

		for (int i = 0; i < menutitles.length; i++) {
			// RowIconItem items = new RowIconItem(menutitles[i],
			// menuIcons.getResourceId(i, -1));
			RowIconItem items = new RowIconItem(menutitles[i],
					R.drawable.calender);
			rowIconItems.add(items);
		}

		adapter = new CustomIconAdapter(getActivity(), rowIconItems);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
	}
}
