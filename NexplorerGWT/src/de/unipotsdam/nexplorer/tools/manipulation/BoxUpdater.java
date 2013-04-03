package de.unipotsdam.nexplorer.tools.manipulation;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BoxUpdater implements ChangeListener {

	private JTextField valueLabel;
	private JSlider source;

	public BoxUpdater(JTextField valueLabel, JSlider slider) {
		this.valueLabel = valueLabel;
		this.source = slider;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		valueLabel.setText(Integer.toString(source.getValue()));
	}
}
