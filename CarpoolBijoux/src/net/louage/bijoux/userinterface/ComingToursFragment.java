package net.louage.bijoux.userinterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;

import net.louage.bijoux.R;
import net.louage.bijoux.constants.DateTime;
import net.louage.bijoux.constants.SharedPreferences;
import net.louage.bijoux.model.Tour;
import net.louage.bijoux.server.AsTskArrayListCompleteListener;
import net.louage.bijoux.server.TourAsyncGetTours;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ComingToursFragment extends ListFragment implements
		OnItemClickListener {
	public static final String ARG_NAVDRAWER_NUMBER = "number";
	CustomIconAdapter adapter;
	private List<RowIconItem> rowIconItems;
	private ArrayList<Tour> tours = new ArrayList<Tour>();

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
		String[] params = getTourParams();
		new TourAsyncGetTours(getActivity(),
				new TourAsyncGetToursTaskCompleteListener(), params).execute();
		return comingToursView;
	}

	class TourAsyncGetToursTaskCompleteListener implements
			AsTskArrayListCompleteListener<Tour> {
		@Override
		public void onTaskComplete(ArrayList<Tour> tours) {
			ComingToursFragment.this.onTaskComplete(tours);

		}

	}

	private String[] getTourParams() {
		String[] params = { Integer.toString(SharedPreferences
				.getUserId(getActivity())) };
		return params;
	}

	public void onTaskComplete(ArrayList<Tour> trs) {
		String tag="ComingToursFragment onTaskComplete";
		tours.clear();
		tours = trs;
		rowIconItems = new ArrayList<RowIconItem>();
		if (tours != null) {
			for (int i = 0; i < tours.size(); i++) {
				Tour tr = tours.get(i);
				Calendar cal = Calendar.getInstance();
				cal.setTime(tr.getDate());
				int day = cal.get(Calendar.DAY_OF_MONTH);
				String tourDate=DateTime.getDateMediumFormat(tr.getDate());
				String tourtime=DateTime.getLocaleTimeDefaultFormat(tr.getTime());
				Log.d(tag, "tour date json: "+ tr.getDate());
				String trData = tourDate + " - " + tourtime + "\nFrom: "
						+ tr.getFromAddress().getLocality() + "\nTo: "
						+ tr.getToAddress().getLocality();
				String logo = "calender" + day;
				int id = getActivity().getResources().getIdentifier(logo,
						"drawable", getActivity().getPackageName());
				RowIconItem items = new RowIconItem(trData, id);
				rowIconItems.add(items);
			}

			adapter = new CustomIconAdapter(getActivity(), rowIconItems);
			setListAdapter(adapter);
			getListView().setOnItemClickListener(this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Tour tr = (Tour) tours.get(position);
		//String tour = "From: "+tr.getFromAddress().getLocality() + "\nTo: "+ tr.getToAddress().getLocality();
		//Toast.makeText(getActivity(), tour, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(getActivity(), TourActivity.class);
		Gson gson = new Gson();
		String jsonTour = gson.toJson(tr);
		intent.putExtra("jsonTour", jsonTour);
		startActivity(intent);
	}
}
