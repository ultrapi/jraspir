package com.pihldata.raspir;

import javax.swing.JFrame;

public class Raspir {
	public static void main(String[] args) {
		ControlFrame f = new ControlFrame();
		f.setTitle("Mitsubishi");
		f.setSize(350,100);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);				
	}
}
