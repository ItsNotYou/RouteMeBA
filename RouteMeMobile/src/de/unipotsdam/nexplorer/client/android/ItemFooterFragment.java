package de.unipotsdam.nexplorer.client.android;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import de.unipotsdam.nexplorer.client.android.callbacks.UIFooter;

public class ItemFooterFragment extends Fragment implements UIFooter {

	private Button collectItem;
	private TextView activeItems;
	private TextView hint;
	private TextView nextItemDistance;
	private boolean isCollectingItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.isCollectingItem = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.fragment_item_footer, container, false);

		collectItem = (Button) result.findViewById(R.id.collectItem);
		activeItems = (TextView) result.findViewById(R.id.activeItems);
		hint = (TextView) result.findViewById(R.id.hint);
		nextItemDistance = (TextView) result.findViewById(R.id.nextItemDistance);

		return result;
	}

	@Override
	public void updateFooter(Integer nextItemDistance, boolean hasRangeBooster, boolean itemInCollectionRange, String hint) {
		this.hint.setText(hint);

		if (nextItemDistance != null)
			setText(this.nextItemDistance, "Entfernung zum nächsten Gegenstand " + nextItemDistance + " Meter.");
		else
			setText(this.nextItemDistance, "Keine Gegenstände in der Nähe.");

		int boosterImageElement;
		if (hasRangeBooster) {
			boosterImageElement = R.drawable.mobile_phone_cast;
		} else {
			boosterImageElement = R.drawable.mobile_phone_cast_gray;
		}

		setText(activeItems, "Aktive Gegenstände: ", boosterImageElement);

		if (!this.isCollectingItem) {
			setText(collectItem, "Gegenstand einsammeln");

			boolean isDisabled = !collectItem.isEnabled();
			if (itemInCollectionRange && isDisabled) {
				collectItem.setEnabled(true);
			} else if (!itemInCollectionRange && !isDisabled) {
				collectItem.setEnabled(false);
			}
		}
	}

	@Override
	public void setIsCollectingItem(boolean isCollectingItem) {
		this.isCollectingItem = isCollectingItem;

		if (isCollectingItem) {
			collectItem.setEnabled(false);
			collectItem.setText("Gegenstand wird eingesammelt...<img src='media/images/ajax-loader.gif' />");
		}
	}

	private void setText(TextView text, final String string, final Integer imageId) {
		if (imageId != null) {
			Drawable image = getResources().getDrawable(imageId);
			text.setCompoundDrawablesWithIntrinsicBounds(null, null, image, null);
		}

		text.setText(string);
		if (text.getVisibility() != View.VISIBLE) {
			text.setVisibility(View.VISIBLE);
		}
	}

	void setText(TextView text, final String string) {
		setText(text, string, null);
	}
}
