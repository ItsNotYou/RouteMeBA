package de.unipotsdam.nexplorer.client.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import de.unipotsdam.nexplorer.client.admin.viewcontroller.StatsUpdateTimer;

public class AdminWrapper extends Composite {

	private static AdminWrapperUiBinder uiBinder = GWT
			.create(AdminWrapperUiBinder.class);

	interface AdminWrapperUiBinder extends UiBinder<Widget, AdminWrapper> {
	}

	@UiField
	HTMLPanel htmlPanel;
	private AdminBinder adminBinder;

	public AdminWrapper() {
		initWidget(uiBinder.createAndBindUi(this));		
		this.adminBinder = new AdminBinder();
		this.htmlPanel.getElement().appendChild(adminBinder.getElement());		
	}

	
}
