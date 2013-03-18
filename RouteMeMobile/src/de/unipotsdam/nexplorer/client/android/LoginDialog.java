package de.unipotsdam.nexplorer.client.android;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LoginDialog extends Dialog {

	private LoginCallback loginListener;

	public LoginDialog(Context context) {
		super(context);

		setContentView(R.layout.dialog_login);
		setTitle("Anmeldung");
		setCancelable(false);

		EditText loginName = (EditText) findViewById(R.id.login_name);
		loginName.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == R.id.login_attempt || actionId == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				attemptLogin();
			}
		});
	}

	private void attemptLogin() {
		EditText text = (EditText) findViewById(R.id.login_name);
		String name = text.getText().toString();
		if (loginListener != null) {
			loginListener.onLogin(name);
		}
	}

	public void setOnLoginListener(final LoginCallback listener) {
		this.loginListener = listener;
	}

	public interface LoginCallback {

		public void onLogin(String name);
	}
}
