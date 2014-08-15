package net.louage.bijoux.userinterface;

import net.louage.bijoux.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {
	public static final String ARG_NAVDRAWER_NUMBER = "number";

	public MainFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int i = getArguments().getInt(ARG_NAVDRAWER_NUMBER);
		MainActivity ma = (MainActivity) getActivity();
		String selNavDrawItem = ma.getmNavDrawerTitles()[i];
		switch (selNavDrawItem) {
		case "Coming Tours":
			View commingToursView = inflater.inflate(
					R.layout.fragment_main, container, false);
			getActivity().setTitle(selNavDrawItem);
			return commingToursView;
		case "My Tours":
			View myToursView = inflater.inflate(R.layout.fragment_main,
					container, false);
			getActivity().setTitle(selNavDrawItem);
			return myToursView;
		case "My Seats":
			View mySeatsView = inflater.inflate(R.layout.fragment_main,
					container, false);
			getActivity().setTitle(selNavDrawItem);
			return mySeatsView;
		case "My Team":
			// TODO Build fragment for My Team
			View myTeamView = inflater.inflate(R.layout.fragment_main,
					container, false);
			getActivity().setTitle(selNavDrawItem);
			return myTeamView;
		case "Approve Member":
			// TODO Build fragment for Approve Member
			View approveMemberView = inflater.inflate(
					R.layout.fragment_main, container, false);
			getActivity().setTitle(selNavDrawItem);
			return approveMemberView;
		case "Admin":
			// TODO Build fragment for Admin
			View adminView = inflater.inflate(R.layout.fragment_main,
					container, false);
			getActivity().setTitle(selNavDrawItem);
			return adminView;
		default:
			View defaultView = inflater.inflate(
					R.layout.fragment_unknown_account, container, false);
			getActivity().setTitle(selNavDrawItem);
			return defaultView;
		}
	}

}
