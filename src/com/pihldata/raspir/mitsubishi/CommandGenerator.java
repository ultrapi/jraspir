package com.pihldata.raspir.mitsubishi;

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
}
