package com.pihldata.raspir;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.pihldata.raspir.mitsubishi.Command;
import com.pihldata.raspir.mitsubishi.CommandGenerator;
import com.pihldata.raspir.mitsubishi.MitsubishiControlPanel;

public class ControlFrame extends JFrame {
	
	private RaspirSender raspirSender=new RaspirSender();
	
	public ControlFrame() {
		MitsubishiControlPanel mPanel = new MitsubishiControlPanel();
		setTitle("Mitsubishi");
		getContentPane().add(mPanel);

		
		Command defaultCommand = new Command() {{
			t=21;
			powerOn=true;
			//left_vane=VANE.VANE1;
		}};
		mPanel.setCommand(defaultCommand);
		
		mPanel.addListener(a->newCommand(a));
	}

	private void newCommand(ActionEvent a) {
		
		if (!(a.getSource() instanceof Command)) return;
		Command command = (Command)a.getSource();
		
		CommandGenerator cg = new CommandGenerator();
		try {
			String hex = cg.getHex(command);
			System.out.println("Sending hex="+hex);
			
			raspirSender.sendHexData(hex);
			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}
	}
}
