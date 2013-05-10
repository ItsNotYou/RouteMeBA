package de.unipotsdam.nexplorer.client.android.ui;

import android.app.Activity;
import de.unipotsdam.nexplorer.client.android.R;
import de.unipotsdam.nexplorer.client.android.callbacks.UICallback;

public class MainPanelToolbar implements UICallback {

	private Text hint;
	private Text nextItemDistance;
	private Text activeItems;
	private Button collectItemButton;
	private boolean isCollectingItem;

	public MainPanelToolbar(Activity host, Button collectItemButton, Button loginButton, Text activeItems, Text hint, Text nextItemDistance, Text waitingText, Text beginDialog, Overlay loginOverlay, Overlay waitingForGameOverlay, Overlay noPositionOverlay) {
		this.hint = hint;
		this.nextItemDistance = nextItemDistance;
		this.activeItems = activeItems;
		this.collectItemButton = collectItemButton;

		this.isCollectingItem = false;
	}

	public void updateFooter(final Integer nextItemDistance, final boolean hasRangeBooster, final boolean itemInCollectionRange, final String hint) {
		this.hint.setText(hint);

		if (nextItemDistance != null)
			this.nextItemDistance.setText("Entfernung zum nächsten Gegenstand " + nextItemDistance + " Meter.");
		else
			this.nextItemDistance.setText("Keine Gegenstände in der Nähe.");

		int boosterImageElement;
		if (hasRangeBooster) {
			boosterImageElement = R.drawable.mobile_phone_cast;
		} else {
			boosterImageElement = R.drawable.mobile_phone_cast_gray;
		}

		activeItems.html("Aktive Gegenstände: ", boosterImageElement);

		if (!this.isCollectingItem) {
			collectItemButton.html("Gegenstand einsammeln");

			boolean isDisabled = collectItemButton.isDisabled();
			if (itemInCollectionRange && isDisabled) {
				collectItemButton.enable();
			} else if (!itemInCollectionRange && !isDisabled) {
				collectItemButton.disable();
			}
		}
	}

	@Override
	public void setIsCollectingItem(boolean isCollectingItem) {
		this.isCollectingItem = isCollectingItem;
	}
}
