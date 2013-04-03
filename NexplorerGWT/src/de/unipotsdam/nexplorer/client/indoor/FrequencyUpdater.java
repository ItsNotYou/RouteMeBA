package de.unipotsdam.nexplorer.client.indoor;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class FrequencyUpdater<T> implements AsyncCallback<Integer> {

	private PlayerInfoBinder playerInfoBinder;

	public FrequencyUpdater(PlayerInfoBinder playerInfoBinder) {
		this.playerInfoBinder = playerInfoBinder;
	}

	@Override
	public void onFailure(Throwable caught) {
		this.playerInfoBinder.finishConstructorAfterUpdate(1000);
	}

	@Override
	public void onSuccess(Integer result) {
		this.playerInfoBinder.finishConstructorAfterUpdate(result);
	}

}
