package net.louage.bijoux.constants;

import java.util.ArrayList;

import com.google.gson.Gson;

import net.louage.bijoux.R;
import net.louage.bijoux.model.Team;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class NoticeDialogFragmentMultiChoice extends DialogFragment {

	//mSelectedItems Where we track the selected items
	private ArrayList<Integer> mSelectedItems;
	//Building a list of teams the user (as team leader) can manage.
	ArrayList<Team> teams;
	String[] teamnames;
	String[] resultSelection;

	public static DialogFragment newInstance(int i) {
		NoticeDialogFragmentMultiChoice dialogFragment = new NoticeDialogFragmentMultiChoice();
	    Bundle bundle = new Bundle();
	    bundle.putInt("num", i);
	    dialogFragment.setArguments(bundle);
	    return dialogFragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String tag="NoticeDialogFragmentMultiChoice onCreateDialog";
		mSelectedItems = new ArrayList<Integer>();
		teams = new ArrayList<Team>();
		teams = SharedPreferences.getUser(getActivity()).getManagerOf();
		teamnames=new String[teams.size()];
		Log.d(tag, "teams.size(): "+teams.size());
		for (int i = 0; i < teams.size(); i++) {
			Team tm = new Team();
			tm=teams.get(i);
			teamnames[i] = tm.getTeamname();
			Log.d(tag, "teamname "+i+": "+tm.getTeamname());
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Set the dialog title
		builder.setTitle(R.string.dia_multi_listener_title)
				// Specify the list array, the items to be selected by default (null for none),
				// and the listener through which to receive callbacks when items are selected
				.setMultiChoiceItems(teamnames, null, new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
								if (isChecked) {
									// If the user checked the item, add it to the selected items
									mSelectedItems.add(which);
								} else if (mSelectedItems.contains(which)) {
									// Else, if the item is already in the array, remove it
									mSelectedItems.remove(Integer.valueOf(which));
								}								
							}
						})
				// Set the action buttons
				.setPositiveButton(R.string.dia_multi_listener_ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// User clicked OK, so save the mSelectedItems
								// results somewhere
								// or return them to the component that opened
								// the dialog
								resultSelection=new String[mSelectedItems.size()];
								for (int i = 0; i < mSelectedItems.size(); i++) {
									int selection = mSelectedItems.get(i);
									// Add team names to the result to return
									resultSelection[i] = teams.get(selection).getTeamname();
								}
								Gson gson = new Gson();
								String jsonResultSelection = gson.toJson(resultSelection);
								Log.d("setPositiveButton" + " resultSelection:", jsonResultSelection);
								getActivity().getIntent().putExtra("jsonResultSelection", jsonResultSelection);
								//mListener.onDialogPositiveClick(NoticeDialogFragmentMultiChoice.this);
								getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
							}
						})
				.setNegativeButton(R.string.dia_multi_listener_cancel, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								//mListener.onDialogNegativeClick(NoticeDialogFragmentMultiChoice.this);
								getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
							}
						}).create();;

		return builder.create();
	}

	public String[] getResultSelection() {
		return resultSelection;
	}

	public void setResultSelection(String[] resultSelection) {
		this.resultSelection = resultSelection;
	}

}