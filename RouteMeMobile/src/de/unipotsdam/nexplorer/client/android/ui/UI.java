package de.unipotsdam.nexplorer.client.android.ui;

import static de.unipotsdam.nexplorer.client.android.js.Window.beginDialog;
import static de.unipotsdam.nexplorer.client.android.js.Window.loginButton;
import static de.unipotsdam.nexplorer.client.android.js.Window.loginOverlay;
import static de.unipotsdam.nexplorer.client.android.js.Window.mainPanelToolbar;
import static de.unipotsdam.nexplorer.client.android.js.Window.noPositionOverlay;
import static de.unipotsdam.nexplorer.client.android.js.Window.waitingForGameOverlay;
import static de.unipotsdam.nexplorer.client.android.js.Window.waitingText;
import android.app.Activity;
import de.unipotsdam.nexplorer.client.android.R;
import de.unipotsdam.nexplorer.client.android.js.Window;

public class UI extends UIElement {

	public UI(Activity host) {
		super(host);
	}

	private String addZ(double n) {
		return (n < 10 ? "0" : "") + n;
	}

	/**
	 * 
	 * @param ms
	 * @returns {String}
	 */
	private String convertMS(Integer seconds) {
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

	public void updateStatusHeaderAndFooter(final int score, final int neighbourCount, final int remainingPlayingTime, final double battery, final Object nextItemDistance, final boolean hasRangeBooster, final boolean isCollectingItem, final boolean itemInCollectionRange, final String hint) {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				updateStatusHeader(score, neighbourCount, remainingPlayingTime, battery);
				updateStatusFooter(nextItemDistance, hasRangeBooster, isCollectingItem, itemInCollectionRange, hint);
			}
		});
	}

	private void updateStatusHeader(final Integer score, final Integer neighbourCount, final Integer remainingPlayingTime, final Double battery) {
		mainPanelToolbar.items.getItems()[0].setText(score + "");
		mainPanelToolbar.items.getItems()[2].setText(neighbourCount + "");
		mainPanelToolbar.items.getItems()[4].setText(convertMS(remainingPlayingTime));
		mainPanelToolbar.items.getItems()[6].setText((battery + "%").replace(".", ","));
	}

	private void updateStatusFooter(final Object nextItemDistance, final boolean hasRangeBooster, final boolean isCollectingItem, final boolean itemInCollectionRange, final String hint) {
		Window.hint.setText(hint);

		if (nextItemDistance != null)
			Window.nextItemDistance.setText("Entfernung zum nächsten Gegenstand " + nextItemDistance + " Meter.");
		else
			Window.nextItemDistance.setText("Keine Gegenstände in der Nähe.");

		int boosterImageElement;
		if (hasRangeBooster) {
			boosterImageElement = R.drawable.mobile_phone_cast;
		} else {
			boosterImageElement = R.drawable.mobile_phone_cast_gray;
		}

		Window.activeItems.html("Aktive Gegenstände: ", boosterImageElement);

		if (!isCollectingItem) {
			Window.collectItemButton.html("Gegenstand einsammeln");

			boolean isDisabled = Window.collectItemButton.isDisabled();
			if (itemInCollectionRange && isDisabled) {
				Window.collectItemButton.enable();
			} else if (!itemInCollectionRange && !isDisabled) {
				Window.collectItemButton.disable();
			}
		}
	}

	public void disableButtonForItemCollection() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				Window.collectItemButton.disable();
				Window.collectItemButton.html("Gegenstand wird eingesammelt...<img src='media/images/ajax-loader.gif' />");
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
