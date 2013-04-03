package de.unipotsdam.nexplorer.client.admin.viewcontroller;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unipotsdam.nexplorer.client.admin.AdminBinder;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;

public class ItemStatsUpdater<T> implements AsyncCallback<List<Items>> {

	private AdminBinder adminBinder;

	public ItemStatsUpdater(AdminBinder adminBinder) {
		this.adminBinder = adminBinder;
	}

	@Override
	public void onFailure(Throwable caught) {
		GWT.log("Item stats konnten nicht geladen werden");
	}

	@Override
	public void onSuccess(List<Items> result) {
		adminBinder.getItemStatsBinder().update(result);
	}

}
