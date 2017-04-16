package com.pihldata.raspir;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Send commands to compiled raspir.c native C executable program
 * the command will send pulses on GPIO pin connected to IR LED to control decvice
 */
public class RaspirSender {
	
	private File execFile;
	private int gpioPin;
	
	public RaspirSender(int gpioPin) {
		this.gpioPin=gpioPin;
		execFile=new File("./raspir");		
	}
	
	public void sendHexData(String hex) throws Exception {
		//Check exec
		if (!execFile.exists()) throw new Exception("Cannot find file \""+execFile+"\"");
		if (!execFile.isFile()) throw new Exception("Not a file \""+execFile+"\"");
		if (!execFile.canExecute()) throw new Exception("Not an executable file \""+execFile+"\"");

		//Start exec
		String command = "sudo "+execFile.toString()+" -p "+gpioPin+" -h "+hex; //+" -o out.txt";
		System.out.println("STARTING: "+command);
		Process process = Runtime.getRuntime().exec(command);
		InputStream ins = process.getInputStream();
		InputStream errs = process.getErrorStream();
		
		monitorStream(ins);
		monitorStream(errs);
	}

	private void monitorStream(InputStream is) {
		Thread task2 = new Thread(() -> { 
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader buff = new BufferedReader (isr);

			String line;
			try {
				while((line = buff.readLine()) != null)
				    System.out.print(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		task2.start();
	}
	
	public void setGpioPin(int pin) {
		gpioPin=pin;
	}
}
