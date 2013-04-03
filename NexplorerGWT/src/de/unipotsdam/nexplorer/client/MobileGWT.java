package de.unipotsdam.nexplorer.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import de.unipotsdam.nexplorer.client.mobile.MobileWrapper;

public class MobileGWT implements EntryPoint {

	@Override
	public void onModuleLoad() {		
		MobileWrapper mobileWrapper = new MobileWrapper();
		RootPanel.get("contents").add(mobileWrapper);
//		RootPanel.get("contents").add(new Label("hello"));
	}

}
