package de.unipotsdam.nexplorer.client.android.callbacks;

public interface UICallback {

	public void updateHeader(final Integer score, final Integer neighbourCount, final Long remainingPlayingTime, final Double battery);

	public void updateFooter(final Integer nextItemDistance, final boolean hasRangeBooster, final boolean itemInCollectionRange, final String hint);

	public void setIsCollectingItem(boolean isCollectingItem);
}
