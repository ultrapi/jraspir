package com.pihldata.raspir.mitsubishi;

import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public class MitsubishiControlPanel extends JPanel {
	
	private JComboBox<Integer> temperatureComboBox;
	private JCheckBox chckbxOn;
	
	private HashSet<ActionListener> listeners=new HashSet<>();
	
	public MitsubishiControlPanel() {
		setLayout(null);
		
		chckbxOn = new JCheckBox("On");
		chckbxOn.setBounds(309, 13, 129, 23);
		chckbxOn.addActionListener(a->update());
		add(chckbxOn);
		
		temperatureComboBox = new JComboBox<>();
		Integer[] mitsubishiAllowedTemperatures = new Integer[] {10,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
		DefaultComboBoxModel<Integer> tempModel = new DefaultComboBoxModel<Integer>(mitsubishiAllowedTemperatures);
		temperatureComboBox.setModel(tempModel);
		temperatureComboBox.setSelectedItem(20);
		temperatureComboBox.addActionListener(a->update());
		temperatureComboBox.setBounds(121, 12, 107, 24);
		add(temperatureComboBox);
		
		JLabel lblTemperature = new JLabel("Temperature");
		lblTemperature.setBounds(12, 17, 107, 15);
		add(lblTemperature);
		
		JLabel lblC = new JLabel("C");
		lblC.setBounds(233, 17, 70, 15);
		add(lblC);
	}

	private void update() {
		Command c = new Command();
		c.t= (Integer)temperatureComboBox.getSelectedItem();
		c.powerOn=chckbxOn.isSelected();
		
		System.out.println(c);
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
