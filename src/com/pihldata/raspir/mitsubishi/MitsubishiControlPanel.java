package com.pihldata.raspir.mitsubishi;

import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;

public class MitsubishiControlPanel extends JPanel {
	
	private JComboBox<Integer> temperatureComboBox;
	private JCheckBox chckbxOn;
	private JCheckBox chckbxAutoSend;
	
	private HashSet<ActionListener> listeners=new HashSet<>();
	
	public MitsubishiControlPanel() { 
		GridBagLayout gridBagLayout = new GridBagLayout();
		//gridBagLayout.columnWidths = new int[]{107, 107, 70, 129, 0};
		gridBagLayout.rowHeights = new int[]{24, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblTemperature = new JLabel("Temperature");
		GridBagConstraints gbc_lblTemperature = new GridBagConstraints();
		gbc_lblTemperature.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTemperature.insets = new Insets(5, 5, 5, 5);
		gbc_lblTemperature.gridx = 0;
		gbc_lblTemperature.gridy = 0;
		add(lblTemperature, gbc_lblTemperature);
		
		Integer[] mitsubishiAllowedTemperatures = new Integer[] {10,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
		DefaultComboBoxModel<Integer> tempModel = new DefaultComboBoxModel<Integer>(mitsubishiAllowedTemperatures);
		temperatureComboBox = new JComboBox<>();
		temperatureComboBox.setModel(tempModel);
		temperatureComboBox.setSelectedItem(20);
		temperatureComboBox.addActionListener(a->autoUpdate());
		GridBagConstraints gbc_temperatureComboBox = new GridBagConstraints();
		gbc_temperatureComboBox.weightx = 1.0;
		gbc_temperatureComboBox.anchor = GridBagConstraints.NORTH;
		gbc_temperatureComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_temperatureComboBox.insets = new Insets(5, 0, 5, 5);
		gbc_temperatureComboBox.gridx = 1;
		gbc_temperatureComboBox.gridy = 0;
		add(temperatureComboBox, gbc_temperatureComboBox);
		
		JLabel lblC = new JLabel("C");
		GridBagConstraints gbc_lblC = new GridBagConstraints();
		gbc_lblC.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblC.insets = new Insets(5, 0, 5, 5);
		gbc_lblC.gridx = 2;
		gbc_lblC.gridy = 0;
		add(lblC, gbc_lblC);
		
		chckbxOn = new JCheckBox("On");
		chckbxOn.addActionListener(a->autoUpdate());
		GridBagConstraints gbc_chckbxOn = new GridBagConstraints();
		gbc_chckbxOn.insets = new Insets(5, 0, 5, 0);
		gbc_chckbxOn.anchor = GridBagConstraints.SOUTH;
		gbc_chckbxOn.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxOn.gridx = 3;
		gbc_chckbxOn.gridy = 0;
		add(chckbxOn, gbc_chckbxOn);
				
		JButton btnSend = new JButton("Send");
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.anchor = GridBagConstraints.WEST;
		gbc_btnSend.gridwidth = 3;
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 1;
		gbc_btnSend.gridy = 1;
		btnSend.addActionListener(a->update());
		add(btnSend, gbc_btnSend);
		
		chckbxAutoSend = new JCheckBox("Auto send");
		GridBagConstraints gbc_chckbxAutoSend = new GridBagConstraints();
		gbc_chckbxAutoSend.insets = new Insets(5, 5, 0, 5);
		gbc_chckbxAutoSend.gridx = 0;
		gbc_chckbxAutoSend.gridy = 1;
		chckbxAutoSend.addActionListener(a->btnSend.setEnabled(!chckbxAutoSend.isSelected()));
		add(chckbxAutoSend, gbc_chckbxAutoSend);
	}


	private void autoUpdate() {
		if (chckbxAutoSend.isSelected()) update();
	}
	
	private void update() {		
		Command c = new Command();
		c.t= (Integer)temperatureComboBox.getSelectedItem();
		c.powerOn=chckbxOn.isSelected();
		
		//System.out.println(c);
		
		for (ActionListener actionListener : listeners) {
			actionListener.actionPerformed(new ActionEvent(c,0,""));
		}
	}
	
	public void addListener(ActionListener listener) {
		listeners.add(listener);
	}
	
	public void setCommand(Command c) {
		temperatureComboBox.setSelectedItem(c.t);
		chckbxOn.setSelected(c.powerOn);
	}
}
