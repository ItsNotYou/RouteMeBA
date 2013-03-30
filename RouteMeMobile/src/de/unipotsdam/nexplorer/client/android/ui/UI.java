package de.unipotsdam.nexplorer.client.android.ui;

import android.app.Activity;
import de.unipotsdam.nexplorer.client.android.R;

public class UI extends UIElement {

	private final Button collectItemButton;
	private final Button loginButton;
	private final Text activeItems;
	private final Text hint;
	private final Text nextItemDistance;
	private final Text waitingText;
	private Text beginDialog;
	private MainPanelToolbar mainPanelToolbar;
	private Overlay loginOverlay;
	private Overlay noPositionOverlay;
	private Overlay waitingForGameOverlay;

	public UI(Activity host, Button collectItemButton, Button loginButton, Text activeItems, Text hint, Text nextItemDistance, Text waitingText, Text beginDialog, MainPanelToolbar mainPanelToolbar, Overlay loginOverlay, Overlay waitingForGameOverlay, Overlay noPositionOverlay) {
		super(host);
		this.collectItemButton = collectItemButton;
		this.loginButton = loginButton;
		this.activeItems = activeItems;
		this.hint = hint;
		this.nextItemDistance = nextItemDistance;
		this.waitingText = waitingText;
		this.beginDialog = beginDialog;
		this.mainPanelToolbar = mainPanelToolbar;
		this.loginOverlay = loginOverlay;
		this.noPositionOverlay = noPositionOverlay;
		this.waitingForGameOverlay = waitingForGameOverlay;
	}

	private String addZ(double n) {
		return (n < 10 ? "0" : "") + n;
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

	public void updateStatusHeaderAndFooter(final int score, final int neighbourCount, final long remainingPlayingTime, final double battery, final Object nextItemDistance, final boolean hasRangeBooster, final boolean isCollectingItem, final boolean itemInCollectionRange, final String hint) {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				updateStatusHeader(score, neighbourCount, remainingPlayingTime, battery);
				updateStatusFooter(nextItemDistance, hasRangeBooster, isCollectingItem, itemInCollectionRange, hint);
			}
		});
	}

	private void updateStatusHeader(final Integer score, final Integer neighbourCount, final Long remainingPlayingTime, final Double battery) {
		mainPanelToolbar.items.getItems()[0].setText(score + "");
		mainPanelToolbar.items.getItems()[2].setText(neighbourCount + "");
		mainPanelToolbar.items.getItems()[4].setText(convertMS(remainingPlayingTime));
		mainPanelToolbar.items.getItems()[6].setText((battery + "%").replace(".", ","));
	}

	private void updateStatusFooter(final Object nextItemDistance, final boolean hasRangeBooster, final boolean isCollectingItem, final boolean itemInCollectionRange, final String hint) {
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

		if (!isCollectingItem) {
			collectItemButton.html("Gegenstand einsammeln");

			boolean isDisabled = collectItemButton.isDisabled();
			if (itemInCollectionRange && isDisabled) {
				collectItemButton.enable();
			} else if (!itemInCollectionRange && !isDisabled) {
				collectItemButton.disable();
			}
		}
	}

	public void disableButtonForItemCollection() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				UI.this.collectItemButton.disable();
				UI.this.collectItemButton.html("Gegenstand wird eingesammelt...<img src='media/images/ajax-loader.gif' />");
			}
		});
	}

	public void hideLoginOverlay() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				loginOverlay.hide();
			}
		});
	}

	public void labelButtonForLogin() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				loginButton.label("melde an...");
			}
		});
	}

	public void showLoginError(String string) {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				beginDialog.setText("Kein Spiel da. Versuchen Sie es später noch einmal!");
				loginButton.label("anmelden ");
			}
		});
	}

	public void hideNoPositionOverlay() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				noPositionOverlay.hide();
			}
		});
	}

	public void showNoPositionOverlay() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				noPositionOverlay.show();
			}
		});
	}

	public void showGameEnded() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				waitingText.setText("Das Spiel ist zu Ende. Vielen Dank fürs Mitspielen.");
				waitingForGameOverlay.show();
			}
		});
	}

	public void showWaitingForGameStart() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				waitingText.setText("Warte auf Spielstart");
				waitingForGameOverlay.show();
			}
		});
	}

	public void showGamePaused() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				waitingText.setText("Das Spiel wurde Pausiert");
				waitingForGameOverlay.show();
			}
		});
	}

	public void hideWaitingForGameOverlay() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				waitingForGameOverlay.hide();
			}
		});
	}

	public void showBatteryEmpty() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				waitingText.setText("Dein Akku ist alle :( Vielen Dank fürs Mitspielen.");
				waitingForGameOverlay.show();
			}
		});
	}
}
