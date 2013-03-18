package de.unipotsdam.nexplorer.client.android;

import android.app.Dialog;
import android.content.Context;

public class WaitingDialog extends Dialog {

	public WaitingDialog(Context context) {
		super(context);

		setContentView(R.layout.dialog_waiting);
		setCancelable(false);
	}
}
