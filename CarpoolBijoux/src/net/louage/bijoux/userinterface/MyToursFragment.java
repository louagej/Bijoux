package net.louage.bijoux.userinterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;

import net.louage.bijoux.R;
import net.louage.bijoux.constants.DateTime;
import net.louage.bijoux.constants.SharedPreferences;
import net.louage.bijoux.model.Tour;
import net.louage.bijoux.model.User;
import net.louage.bijoux.server.AsTskArrayListCompleteListener;
import net.louage.bijoux.server.TourAsyncGetTours;
import android.app.Activity;
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

public class MyToursFragment extends ListFragment implements OnItemClickListener {
	public static final String ARG_NAVDRAWER_NUMBER = "number";
	CustomIconAdapter adapter;
	private List<RowIconItem> rowIconItems;
	private ArrayList<Tour> tours = new ArrayList<Tour>();
	static final int GET_TOUR_INFO = 1; // The request code for getting info after TourActivity and updating MyToursFragment
	private User appUser;
	public MyToursFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int i = getArguments().getInt(ARG_NAVDRAWER_NUMBER);
		MainActivity ma = (MainActivity) getActivity();
		String selNavDrawItem = ma.getmNavDrawerTitles()[i];
		View myToursView = inflater.inflate(R.layout.fragment_my_tours, container, false);
		getActivity().setTitle(selNavDrawItem);
		String[] params = getTourParams();
		new TourAsyncGetTours(getActivity(),
				new TourAsyncGetToursTaskCompleteListener(), params).execute();
		rowIconItems = new ArrayList<RowIconItem>();
		appUser=SharedPreferences.getUser(ma);
		return myToursView;
	}

	class TourAsyncGetToursTaskCompleteListener implements
			AsTskArrayListCompleteListener<Tour> {
		@Override
		public void onTaskComplete(ArrayList<Tour> tours) {
			MyToursFragment.this.onTaskComplete(tours);

		}

	}

	private String[] getTourParams() {
		String[] params = { Integer.toString(SharedPreferences
				.getUserId(getActivity())) };
		return params;
	}

	public void onTaskComplete(ArrayList<Tour> trs) {
		ArrayList<Tour> tempTours = new ArrayList<Tour>();
		for (int i = 0; i < trs.size(); i++) {
			Tour tr = trs.get(i);
			//Check if the tour will be executed by the appUser
			if(appUser.getUser_id()==tr.getUser().getUser_id()){
				//Add tour to temporary ArrayList tempTours
				tempTours.add(tr);
				}
			}
		setTourList(tempTours);
	}

	private void setTourList(ArrayList<Tour> trs) {
		String tag="MyToursFragment setTourList";
		//tours.clear();
		Log.d(tag, "trs size: "+trs.size());
		rowIconItems.clear();
		tours = trs;
		if (tours != null) {
			//Sort Arraylist before displaying it to the user
			Collections.sort(tours, new Comparator<Tour>() {
			    @Override
			    public int compare(Tour t1, Tour t2) {
			        return t1.getDate().compareTo(t2.getDate());
			    }
			});
			for (int i = 0; i < tours.size(); i++) {
				Tour tr = tours.get(i);
				Log.d(tag, "Tour: "+tr.getTour_id());
				Calendar cal = Calendar.getInstance();
				cal.setTime(tr.getDate());
				int day = cal.get(Calendar.DAY_OF_MONTH);
				String tourDate = DateTime.dateToStringMediumFormat(tr.getDate());
				String tourtime = DateTime.getLocaleTimeDefaultFormat(tr.getTime());
				//Log.d(tag, "tour date json: " + tr.getDate());
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
		// String tour = "From: "+tr.getFromAddress().getLocality() + "\nTo: "+
		// tr.getToAddress().getLocality();
		// Toast.makeText(getActivity(), tour, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(getActivity(), TourActivity.class);
		Gson gson = new Gson();
		String jsonTour = gson.toJson(tr);
		intent.putExtra("jsonTour", jsonTour);
		// startActivity(intent);
		startActivityForResult(intent, GET_TOUR_INFO);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		String tag = "MyToursFragment onActivityResult";
		if (data==null) {
			Toast.makeText(getActivity(), "Intent data is null", Toast.LENGTH_SHORT).show();
		}
 		//Toast.makeText(getActivity(), "onActivityResult was started",
		//		Toast.LENGTH_SHORT).show();
		
		Log.d(tag, "requestCode: " + requestCode);
		Log.d(tag, "resultCode: " + resultCode);
		if (requestCode == GET_TOUR_INFO) {
			ArrayList<Tour> toursUpdate = new ArrayList<Tour>();
			if (resultCode == Activity.RESULT_OK) {
				Bundle b = data.getExtras();
				if (b != null) {
					//Retrieve tour from intent
					String jsonTour = data.getStringExtra("jsonTour");
					Gson gson = new Gson();
					Tour tr = gson.fromJson(jsonTour, Tour.class);
					toursUpdate = tours;
					Log.d(tag, "toursUpdate size(): " + toursUpdate.size());
					Boolean deleted = data.getBooleanExtra("tourDeleted", false);
					Boolean newTour = data.getBooleanExtra("newTour", false);
					
					if (deleted==true) {
						//remove the tour from the ArrayList tours
						int removeIndex=-1;
						for (int i = 0; i < toursUpdate.size(); i++) {
							Tour tour = toursUpdate.get(i);
							if (tr.getTour_id() == tour.getTour_id()) {
								// capture the index position of the tour that needs to be removed
								removeIndex=i;
							}
						}
						if (removeIndex!=-1) {
							//If removeIndex isn't -1, it was updated with an index position.
							//Now we can remove this Tour object from the ArrayList tours
							toursUpdate.remove(removeIndex);
							//Toast.makeText(getActivity(), "tour at position "+removeIndex+" is deleted. " + deleted ,Toast.LENGTH_SHORT).show();
							Log.d("onTaskComplete", "Removed tour at position " + removeIndex);
						}
						
					} else if (newTour==true){
						//Add a new tour to the existing ArrayList tours
						toursUpdate.add(tr);
						//Toast.makeText(getActivity(), "tour "+tr.getTour_id()+" is added. " + newTour ,Toast.LENGTH_SHORT).show();
						Log.d("onTaskComplete", "Added tour: " + tr.getTour_id());
					}else{
						//Update the existing ArrayList tours with returned Tour tr
						for (int i = 0; i < toursUpdate.size(); i++) {
							Tour tour = tours.get(i);							
							if (tr.getTour_id() == tour.getTour_id()) {
								// Replace the old tour object with the updated object in the original ArrayList tours
								toursUpdate.set(i, tr);
								//Toast.makeText(getActivity(), "tour "+tr.getTour_id()+" is updated.",Toast.LENGTH_SHORT).show();
								Log.d("onTaskComplete", "Updated tour: " + tr.getTour_id());
							}
						}
					}
				} else {
					//Toast.makeText(getActivity(), "Bundle b was = null",
					//		Toast.LENGTH_SHORT).show();
				}
				//Log.d("onTaskComplete", "tours size(): " + toursUpdate.size());
				setTourList(toursUpdate);
			}
		}

	}

}
