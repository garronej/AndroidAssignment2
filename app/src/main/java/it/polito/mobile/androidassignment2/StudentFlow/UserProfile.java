package it.polito.mobile.androidassignment2.StudentFlow;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.zip.Inflater;

import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.R;

public class UserProfile extends Fragment {
	private Communicator communicator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_user_profile,container,false);
	}

	public void setCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}
}
