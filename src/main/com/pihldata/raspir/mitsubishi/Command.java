package com.pihldata.raspir.mitsubishi;


public class Command {
	
	//public enum Mode {HEAT, AUTO, COOL};
	//public enum VANE {VANE1,VANE2,VANE3,VANE4,VANE5,VANEAUTO};
	
	public int t; //[C]
	public boolean powerOn;

	//public Mode mode;
	//public VANE left_vane;
	
	public String toString() {
		return "t="+t+"C\nPower="+(powerOn?"ON":"OFF");
	}
}
