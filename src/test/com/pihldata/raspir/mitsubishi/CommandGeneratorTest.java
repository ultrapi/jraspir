package com.pihldata.raspir.mitsubishi;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CommandGeneratorTest {

	@Test
	public void test() {
		
		Command defaultCommand = new Command() {{
			t=21;
			powerOn=true;
			//left_vane=VANE.VANE1;
		}};
		
		CommandGenerator cg = new CommandGenerator();
		try {
			String hex = cg.getHex(defaultCommand);
			assertEquals("Get hex","23CB2601002048053080000000000800003A", hex);

			List<int[]> ds = cg.getDataSequence("23CB26010020480030800000000008200055");
			
			int expectedLength = 9793;
			assertEquals("Sequence length", expectedLength,ds.size());
			assertEquals("Last time", 306779,ds.get(expectedLength-1)[0]);
		
			/*
			int gpioOutPin = -1;
			RaspirSender raspirSender = new RaspirSender(gpioOutPin);
			raspirSender.sendSequence(ds);	
			*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
