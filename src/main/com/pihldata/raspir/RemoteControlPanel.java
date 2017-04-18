package com.pihldata.raspir;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;

import javax.swing.JPanel;

/**
 * Any remote control panel should extend this class 
 */
abstract public class RemoteControlPanel extends JPanel {
	
	private HashSet<ActionListener> listeners=new HashSet<>();
	
	/**
	 * Get the sequence to send to IR LED
	 * @return Each element is time in microseconds, and on/off state (1=on)
	 * @throws Exception
	 */
	abstract public List<int[]> getDataSequence() throws Exception;
	
	public void addListener(ActionListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Call when command should be sent. For example when a button is clicked.
	 */
	protected void update() {		
		for (ActionListener actionListener : listeners) {
			actionListener.actionPerformed(new ActionEvent(this,0,""));
		}
	}	
}

