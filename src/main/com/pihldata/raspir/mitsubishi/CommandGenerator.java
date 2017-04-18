package com.pihldata.raspir.mitsubishi;

import java.util.ArrayList;
import java.util.List;

public class CommandGenerator {
	
	private final int ON_OFF_INDEX=5;
	private final int ON_OFF_BIT=5;
	
	private final int TEMPERATURE_INDEX=7;
	
	private final int PLASMA_INDEX=15;
	private final int ISAVE_BIT=5;
	
	private final int CRC_INDEX=17;
	private final int commandLength=18;
	
	public String getHex(Command command) throws Exception {
		//Example commands
		//String h16HexCommand="23CB260100204800304000000000080000F5";
		String h10HexCommand="23CB26010020480030800000000008200055";
		
		String hexCommand=h10HexCommand;
		int[] bytes = getBytes(hexCommand);
		
		//On/off
		bytes[ON_OFF_INDEX]=setBit(bytes[ON_OFF_INDEX],ON_OFF_BIT, command.powerOn);
		
		//Temperature
		if (command.t==10) {
			//For 10 degrees, byte 15 bit 5 is set to 1
			bytes[TEMPERATURE_INDEX]=0;
		} else if (16<=command.t||command.t<=31) {
			bytes[TEMPERATURE_INDEX]=command.t-16;
		} else throw new Exception("Temperature out of range "+command.t+"C");
		bytes[PLASMA_INDEX]=setBit(bytes[PLASMA_INDEX],ISAVE_BIT, command.t==10);
		

		//Simple 8-bit CRC
		int crc=0;
		for (int i = 0; i < bytes.length-1; i++) crc+=bytes[i];
		bytes[CRC_INDEX]=crc%256;
		
		return getHex(bytes);
	}
	
	public Command getCommand(String hexCommand) throws Exception {
		if (hexCommand.length()!=commandLength*2) throw new Exception("Command must be "+commandLength+" bytes hex, string length is "+hexCommand.length()+" expected "+commandLength*2);
		int[] bytes = getBytes(hexCommand);
		
		//Check CRC
		int crc=0;
		for (int i = 0; i < bytes.length-1; i++) crc+=bytes[i];
		if (bytes[CRC_INDEX]!=crc%256) throw new Exception("Invalid CRC "+bytes[CRC_INDEX]+", should be "+crc%256);
		
		Command result = new Command();
		
		//Power
		result.powerOn=0<(bytes[ON_OFF_INDEX]&ON_OFF_BIT);
		
		//Temperature
		int t=16+(bytes[TEMPERATURE_INDEX]&0x0F);
		if (0<(bytes[PLASMA_INDEX]&(1<<ISAVE_BIT)) && t==16) result.t=10;
		else result.t=t;
		
		
		return result;
	}

	private void printBinary(int[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			System.out.println("Byte "+i+"\t"+String.format("%8s", Integer.toBinaryString(bytes[i])).replace(' ', '0')+" "+String.format("%02X", bytes[i]));
		}
	}
	
	private int setBit(int byteVal, int bitIndex, boolean bitValue) {
		return bitValue ? byteVal | (1 << bitIndex) : byteVal & ~(1 << bitIndex);
	}
	
	public String getHex(int[] bytes) throws Exception {
		String s="";
		for (int b : bytes) {
			s+=String.format("%02X", b)+"";
		}
		return s;
	}
	
	private int[] getBytes(String hex) {
		int resultLen = hex.length()/2;
		int[] r = new int[resultLen];
		for (int i = 0; i < resultLen; i++) {
			r[i]=Integer.parseInt(hex.substring(i*2,i*2+2), 16);
		}
		return r;
	}
	
	//Add pulses, return consumed samples of sampleTime
	//sampleTime first point is already set as starting point and samples is added after this
	List<int[]> getSample(int startTime, int hTime, int lTime, int pulseHLength, int pulseLLength) {
		List<int[]> result = new ArrayList<>();
		int t = startTime;
		while (t+hTime+lTime<=startTime+pulseHLength) {   
			result.add(new int[] {t,1});
			result.add(new int[] {t+hTime,0});
			t+=hTime+lTime;
		}
		return result;
	}
	
	/**
	 * Get sequence of timed switch on/off to send IR command 
	 * @param hexCommand
	 * @return Each element is a pair of time in microseconds, and the state on/off (1=on 0=off)
	 * @throws Exception
	 */
	public List<int[]> getDataSequence(String hexCommand) throws Exception {
		
		int[] bytes = getBytes(hexCommand);
		
		//Timing of pulses [µs], measured
		int hTime=15; //Time for short high pulse
		int lTime=11; //Time for short low pulse
		int initOffsetTime=700;
		int initPulseHLength=3344;
		int initPulseLLength=1688;
		int bitHLength=428; //Normal 1 bit high time
		int oneBitLLength=1268; //Normal 1 bit low time
		int zeroBitLLength=430; //Normal 0 bit low time
		int repeatePause=11354;
		
		List<int[]> result = new ArrayList<>();
		result.add(new int[]{0,0}); //Starting point
		int t=initOffsetTime;
		for (int repeatIndex=0;repeatIndex<2;repeatIndex++) {
			//Start pulse
			result.addAll(getSample(t, hTime, lTime, initPulseHLength, initPulseLLength));
			int pulseCount = (initPulseHLength)/(hTime+lTime);
			t+=pulseCount*(hTime+lTime)+initPulseLLength;
			for (int byteIndex=0;byteIndex<bytes.length;byteIndex++) {
				for (int bitIndex=0;bitIndex<8;bitIndex++) {
					boolean bitVal = 0<((((byte)bytes[byteIndex]) >> bitIndex)  & 1);
					int lLength=bitVal?oneBitLLength:zeroBitLLength;
					result.addAll(getSample(t, hTime, lTime, bitHLength, lLength));
					pulseCount = (bitHLength)/(hTime+lTime);
					t+=pulseCount*(hTime+lTime)+lLength;
					//t+=bitHLength+lLength;
				}
			}
			//Stop bit
			result.addAll(getSample(t, hTime, lTime, bitHLength, 0));
			pulseCount = (bitHLength)/(hTime+lTime);
			t+=pulseCount*(hTime+lTime)+repeatePause;
			//Repeat pause
			result.addAll(getSample(t, hTime, lTime, 0, repeatePause));
		}
		
		return result;
	}	
}
