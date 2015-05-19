package it.polito.mobile.androidassignment2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import it.polito.mobile.androidassignment2.R;

/**
 * Created by Gabriele on 18/05/2015.
 */
public class AlertYesNo extends DialogFragment {
	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		String message = savedInstanceState.getString("message");
		String title =savedInstanceState.getString("title");
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO
			}
		});
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO
			}
		});
		return builder.create();
	}

	public void show(FragmentManager fragmentManager, String s) {
	}
}
