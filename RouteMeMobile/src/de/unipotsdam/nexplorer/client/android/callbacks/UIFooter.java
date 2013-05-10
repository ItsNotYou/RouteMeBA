package de.unipotsdam.nexplorer.client.android.callbacks;

public interface UIFooter {

	public void updateFooter(final Integer nextItemDistance, final boolean hasRangeBooster, final boolean itemInCollectionRange, final String hint);

	public void setIsCollectingItem(boolean isCollectingItem);
}
