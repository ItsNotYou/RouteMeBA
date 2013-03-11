package de.unipotsdam.nexplorer.client.android;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

public class LoginDialog extends Dialog {

	public LoginDialog(Context context) {
		super(context);

		setContentView(R.layout.dialog_login);
		setTitle("Anmeldung");
		setCancelable(false);
	}

	public void setOnLoginListener(final LoginCallback listener) {
		findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText text = (EditText) findViewById(R.id.login_name);
				String name = text.getText().toString();
				listener.onLogin(name);
			}
		});
	}

	public interface LoginCallback {

		public void onLogin(String name);
	}
}
