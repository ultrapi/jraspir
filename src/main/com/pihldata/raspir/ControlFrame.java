package com.pihldata.raspir;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.pihldata.raspir.mitsubishi.Command;
import com.pihldata.raspir.mitsubishi.MitsubishiControlPanel;

public class ControlFrame extends JFrame {
	
	private RaspirSender raspirSender;
	
	public ControlFrame() {
		
		int gpioOutPin = 21;
		raspirSender=new RaspirSender(gpioOutPin);
		
		setTitle("Mitsubishi");

		RemoteControlPanel mPanel = getMitsubishiControlPanel();
		
		mPanel.addListener(a->newCommand(a));
	}

	private RemoteControlPanel getMitsubishiControlPanel() {
		MitsubishiControlPanel mPanel = new MitsubishiControlPanel();
		getContentPane().add(mPanel);
		Command defaultCommand = new Command() {{
			t=21;
			powerOn=true;
			//left_vane=VANE.VANE1;
		}};
		mPanel.setCommand(defaultCommand);
		return mPanel;
	}

	private void newCommand(ActionEvent a) {
		
		if (!(a.getSource() instanceof RemoteControlPanel)) return;
		RemoteControlPanel panel = (RemoteControlPanel)a.getSource();
				
		try {
			List<int[]> ds = panel.getDataSequence();
			raspirSender.sendSequence(ds);	
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}
	}
}
