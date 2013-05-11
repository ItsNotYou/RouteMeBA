package de.unipotsdam.nexplorer.client.android.net;

import de.unipotsdam.nexplorer.client.android.callbacks.AjaxResult;
import de.unipotsdam.nexplorer.client.android.callbacks.Collectable;
import de.unipotsdam.nexplorer.client.android.callbacks.Loginable;
import de.unipotsdam.nexplorer.client.android.ui.UI;

public class CollectItem implements Collectable, Loginable {

	private final RestMobile rest;
	private final UI ui;
	private Long playerId;
	private boolean isCollectingItem;

	public CollectItem(RestMobile rest, UI ui) {
		this.rest = rest;
		this.ui = ui;

		this.playerId = null;
		this.isCollectingItem = false;
	}

	@Override
	public void loggedIn(long playerId) {
		this.playerId = playerId;
	}

	@Override
	public void collectRequested(Integer itemId) {
		if (!isCollectingItem && playerId != null) {
			isCollectingItem = true;

			ui.disableButtonForItemCollection();
			rest.collectItem(playerId, new AjaxResult<Object>() {

				@Override
				public void success() {
					isCollectingItem = false;
					ui.enableButtonForItemCollection();
				}

				@Override
				public void error() {
					isCollectingItem = false;
					ui.enableButtonForItemCollection();
				}
			});
		}
	}
}
