package it.polito.mobile.laboratory3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;


/**
 * Created by Gabriele on 18/05/2015.
 */
public class AlertYesNo extends DialogFragment {
	public void setCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}
	int kind;
	Communicator communicator;
	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		String message = getArguments().getString("message");
		String title = getArguments().getString("title");
		kind = getArguments().getInt("kind");
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				communicator.dialogResponse(0);
			}
		});
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				communicator.dialogResponse(1);

			}
		});
		return builder.create();
	}


}
