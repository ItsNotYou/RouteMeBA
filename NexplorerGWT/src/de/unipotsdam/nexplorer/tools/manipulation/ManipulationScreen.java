package de.unipotsdam.nexplorer.tools.manipulation;

import static layout.TableLayoutConstants.FILL;
import static layout.TableLayoutConstants.PREFERRED;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import layout.TableLayout;

public class ManipulationScreen extends JPanel {

	private static final long serialVersionUID = -1503198135879965002L;
	private static final double SPACE = 8;

	private JCheckBox autoUpdateBox;
	private JButton sendUpdateButton;
	private UpdateClient client;
	private JSlider baseNodeRangeSlider;
	private JSlider itemCollectionRangeSlider;
	private JSlider maxBatteriesSlider;
	private JSlider maxBoostersSlider;

	public ManipulationScreen(UpdateClient client) {
		this.client = client;

		double[][] layout = { { SPACE, PREFERRED, SPACE, FILL, SPACE, PREFERRED, SPACE }, { SPACE, PREFERRED, SPACE, PREFERRED, SPACE, PREFERRED, SPACE, PREFERRED, SPACE, FILL, PREFERRED, SPACE } };
		setLayout(new TableLayout(layout));

		baseNodeRangeSlider = insertLabeledSlider("BaseNodeRange", 1, 20, client.getBaseNodeRange());
		baseNodeRangeSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				setBaseNodeRange(e);
			}
		});

		itemCollectionRangeSlider = insertLabeledSlider("ItemCollectionRange", 3, 20, client.getItemCollectionRange());
		itemCollectionRangeSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				setItemCollectionRange(e);
			}
		});

		maxBatteriesSlider = insertLabeledSlider("MaxBatteries", 5, 20, client.getMaxBatteries());
		maxBatteriesSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				setMaxBatteries(e);
			}
		});

		maxBoostersSlider = insertLabeledSlider("MaxBoosters", 7, 20, client.getMaxBoosters());
		maxBoostersSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				setMaxBoosters(e);
			}
		});

		autoUpdateBox = new JCheckBox("Autosend");
		autoUpdateBox.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				changeAutoUpdate(e);
			}
		});
		add(autoUpdateBox, "1, 10");

		sendUpdateButton = new JButton("Daten senden");
		sendUpdateButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				sendUpdate(e);
			}
		});
		add(sendUpdateButton, "3, 10");

		autoUpdateBox.setSelected(true);
	}

	private void setBaseNodeRange(ChangeEvent e) {
		client.setBaseNodeRange(baseNodeRangeSlider.getValue());
	}

	private void setItemCollectionRange(ChangeEvent e) {
		client.setItemCollectionRange(itemCollectionRangeSlider.getValue());
	}

	private void setMaxBatteries(ChangeEvent e) {
		client.setMaxBatteries(maxBatteriesSlider.getValue());
	}

	private void setMaxBoosters(ChangeEvent e) {
		client.setMaxBoosters(maxBoostersSlider.getValue());
	}

	private JSlider insertLabeledSlider(String labelText, int row, int maximum, int current) {
		JLabel label = new JLabel(labelText);
		add(label, "1, " + row);

		JTextField valueLabel = new JTextField(3);
		valueLabel.setText(Integer.toString(current));
		valueLabel.setEditable(false);
		add(valueLabel, "5, " + row);

		JSlider slider = new JSlider();
		slider.setValue(current);
		slider.setMaximum(maximum);
		slider.addChangeListener(new BoxUpdater(valueLabel, slider));
		add(slider, "3, " + row);

		return slider;
	}

	private void changeAutoUpdate(ChangeEvent e) {
		boolean isAuto = autoUpdateBox.isSelected();
		sendUpdateButton.setEnabled(!isAuto);

		client.setAutoMode(isAuto);
	}

	private void sendUpdate(ActionEvent e) {
		client.sendUpdate();
	}
}
