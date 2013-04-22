package de.unipotsdam.nexplorer.client.android.ui;

import android.app.Activity;
import android.widget.TextView;
import de.unipotsdam.nexplorer.client.android.R;
import de.unipotsdam.nexplorer.client.android.callbacks.UICallback;

public class MainPanelToolbar implements UICallback {

	public Items items;
	private Text hint;
	private Text nextItemDistance;
	private Text activeItems;
	private Button collectItemButton;
	private boolean isCollectingItem;

	public MainPanelToolbar(TextView score, TextView neighbourCount, TextView remainingPlayingTime, TextView battery, Activity host, Button collectItemButton, Button loginButton, Text activeItems, Text hint, Text nextItemDistance, Text waitingText, Text beginDialog, Overlay loginOverlay, Overlay waitingForGameOverlay, Overlay noPositionOverlay) {
		this.items = new Items(host, score, null, neighbourCount, null, remainingPlayingTime, null, battery);
		this.hint = hint;
		this.nextItemDistance = nextItemDistance;
		this.activeItems = activeItems;
		this.collectItemButton = collectItemButton;

		this.isCollectingItem = false;
	}

	@Override
	public void updateHeader(Integer score, Integer neighbourCount, Long remainingPlayingTime, Double battery) {
		items.getItems()[0].setText(score + "");
		items.getItems()[2].setText(neighbourCount + "");
		items.getItems()[4].setText(convertMS(remainingPlayingTime));
		items.getItems()[6].setText((battery + "%").replace(".", ","));
	}

	/**
	 * 
	 * @param ms
	 * @returns {String}
	 */
	private String convertMS(Long seconds) {
		if (seconds == null) {
			return null;
		}

		double s = seconds;
		double ms = s % 1000;
		s = (s - ms) / 1000;
		double secs = s % 60;
		s = (s - secs) / 60;
		double mins = s % 60;

		return addZ(mins);
	}

	private String addZ(double n) {
		return (n < 10 ? "0" : "") + n;
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
