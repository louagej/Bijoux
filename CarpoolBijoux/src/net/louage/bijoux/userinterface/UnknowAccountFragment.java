package net.louage.bijoux.userinterface;

import net.louage.bijoux.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class UnknowAccountFragment extends Fragment implements View.OnClickListener{
	public static final String ARG_NAVDRAWER_NUMBER = "number";
	TextView txt_unknown_account;
	Button btnLoginFromUnknown;
	Button btnRegisterFromUnknown;

	public UnknowAccountFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int i = getArguments().getInt(ARG_NAVDRAWER_NUMBER);
		MainActivity ma = (MainActivity) getActivity();
		String selNavDrawItem = ma.getmNavDrawerTitles()[i];
		View loginView = inflater.inflate(R.layout.fragment_unknown_account, container, false);
		getActivity().setTitle(selNavDrawItem);
		btnLoginFromUnknown = (Button) loginView.findViewById(R.id.btnLoginFromUnknown);
		btnLoginFromUnknown.setOnClickListener(this);
		btnRegisterFromUnknown = (Button) loginView.findViewById(R.id.btnRegisterFromUnknown);
		btnRegisterFromUnknown.setOnClickListener(this);
		txt_unknown_account = (TextView) loginView.findViewById(R.id.txt_unknown_account);
		return loginView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLoginFromUnknown:
			MainActivity ma = (MainActivity) getActivity();
			// Toast.makeText(ma, "Login from Unknown Account was clicked",
			// Toast.LENGTH_LONG).show();
			String[] nd = ma.getmNavDrawerTitles();
			int selectLogin = 0;
			for (int i = 0; i < nd.length; i++) {
				if (nd[i].equals("Login")) {
					selectLogin = i;
				}
			}
			ma.selectItem(selectLogin);
			break;
		case R.id.btnRegisterFromUnknown:
			MainActivity mar = (MainActivity) getActivity();
			// Toast.makeText(ma,
			// "Register from Unknown Account was clicked",
			// Toast.LENGTH_LONG).show();
			String[] ndr = mar.getmNavDrawerTitles();
			int selectRegister = 0;
			for (int i = 0; i < ndr.length; i++) {
				if (ndr[i].equals("Register")) {
					selectRegister = i;
				}
			}
			mar.selectItem(selectRegister);
			break;
		default:
			break;
		}

	}

}
