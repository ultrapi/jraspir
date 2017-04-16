package com.pihldata.raspir;

import javax.swing.JFrame;

import com.pihldata.raspir.mitsubishi.Command;
import com.pihldata.raspir.mitsubishi.CommandGenerator;
import com.pihldata.raspir.mitsubishi.MitsubishiControlPanel;

public class Raspir {
	public static void main(String[] args) {

		Command defaultCommand = new Command() {{
			t=21;
			powerOn=true;
			//left_vane=VANE.VANE1;
		}};

		JFrame f = new JFrame();
		MitsubishiControlPanel mPanel = new MitsubishiControlPanel();
		f.getContentPane().add(mPanel);
		f.setSize(500,200);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		mPanel.setCommand(defaultCommand);
		
		CommandGenerator cg = new CommandGenerator();
		try {
			cg.getHex(defaultCommand);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
