package de.unipotsdam.nexplorer.client.android.ui;

import android.app.Activity;
import de.unipotsdam.nexplorer.client.android.callbacks.LoginError;
import de.unipotsdam.nexplorer.client.android.callbacks.RemovalReason;
import de.unipotsdam.nexplorer.client.android.callbacks.UIFooter;
import de.unipotsdam.nexplorer.client.android.callbacks.UIGameEvents;
import de.unipotsdam.nexplorer.client.android.callbacks.UIHeader;
import de.unipotsdam.nexplorer.client.android.callbacks.UILogin;
import de.unipotsdam.nexplorer.client.android.callbacks.UISensors;

public class UI extends UIElement implements UILogin, UISensors, UIGameEvents {

	private final Button loginButton;
	private final Text waitingText;
	private Text beginDialog;
	private UIFooter footer;
	private Overlay loginOverlay;
	private Overlay noPositionOverlay;
	private Overlay waitingForGameOverlay;
	private UIHeader header;

	public UI(Activity host, Button loginButton, Text waitingText, Text beginDialog, UIFooter footer, Overlay loginOverlay, Overlay waitingForGameOverlay, Overlay noPositionOverlay, UIHeader header) {
		super(host);
		this.loginButton = loginButton;
		this.waitingText = waitingText;
		this.beginDialog = beginDialog;
		this.footer = footer;
		this.loginOverlay = loginOverlay;
		this.noPositionOverlay = noPositionOverlay;
		this.waitingForGameOverlay = waitingForGameOverlay;
		this.header = header;
	}

	public void updateStatusHeaderAndFooter(final int score, final int neighbourCount, final long remainingPlayingTime, final double battery, final Integer nextItemDistance, final boolean hasRangeBooster, final boolean itemInCollectionRange, final String hint) {
		header.updateHeader(score, neighbourCount, remainingPlayingTime, battery);
		footer.updateFooter(nextItemDistance, hasRangeBooster, itemInCollectionRange, hint);
	}

	public void disableButtonForItemCollection() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				footer.setIsCollectingItem(true);
			}
		});
	}

	public void enableButtonForItemCollection() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				footer.setIsCollectingItem(false);
			}
		});
	}

	private void hideLoginOverlay() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				loginOverlay.hide();
			}
		});
	}

	private void labelButtonForLogin() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				loginButton.label("melde an...");
			}
		});
	}

	private void showLoginError(String string) {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				beginDialog.setText("Kein Spiel da. Versuchen Sie es später noch einmal!");
				loginButton.label("anmelden ");
			}
		});
	}

	private void hideNoPositionOverlay() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				noPositionOverlay.hide();
			}
		});
	}

	private void showNoPositionOverlay() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				noPositionOverlay.show();
			}
		});
	}

	private void showGameEnded() {
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

	private void showGamePaused() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				waitingText.setText("Das Spiel wurde Pausiert");
				waitingForGameOverlay.show();
			}
		});
	}

	private void hideWaitingForGameOverlay() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				waitingForGameOverlay.hide();
			}
		});
	}

	private void showBatteryEmpty() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				waitingText.setText("Dein Akku ist alle :( Vielen Dank fürs Mitspielen.");
				waitingForGameOverlay.show();
			}
		});
	}

	@Override
	public void loginStarted() {
		labelButtonForLogin();
	}

	@Override
	public void loginSucceeded(long playerId) {
		hideLoginOverlay();
	}

	@Override
	public void loginFailed(LoginError reason) {
		switch (reason) {
		case NO_ID:
			showLoginError("Keine id bekommen");
			break;
		case CAUSE_UNKNOWN:
		default:
			showLoginError("Exception wurde ausgelößt - Kein Spiel gestartet?");
			break;
		}

	}

	@Override
	public void noPositionReceived() {
		showNoPositionOverlay();
	}

	@Override
	public void positionReceived() {
		hideNoPositionOverlay();
	}

	@Override
	public void gamePaused() {
		showGamePaused();
	}

	@Override
	public void gameResumed() {
		hideWaitingForGameOverlay();
	}

	@Override
	public void gameEnded() {
		showGameEnded();
	}

	@Override
	public void playerRemoved(RemovalReason reason) {
		switch (reason) {
		case NO_BATTERY:
		default:
			showBatteryEmpty();
		}
	}
}
