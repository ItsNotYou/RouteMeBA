package de.unipotsdam.nexplorer.client.android.js;

import android.os.Handler;

public class UpdateGameStatusInterval extends Interval {

	private static final long updateDisplayIntervalTime = 300;
	private FunctionsMobile functionsMobile;

	public UpdateGameStatusInterval(Handler handler, FunctionsMobile functionsMobile) {
		super(handler, updateDisplayIntervalTime);
		this.functionsMobile = functionsMobile;
	}

	@Override
	public void call() {
		functionsMobile.updateGameStatus(true);
	}
}
