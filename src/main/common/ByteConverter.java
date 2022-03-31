package main.common;

import java.util.Arrays;

import main.common.Input.Instructions;

public class ByteConverter {
	
	public static byte[] instructionToByteArray(Instructions instruction) {		
		byte[] directionBytes = instruction.getDirection().toString().getBytes();
		byte[] startBytes = String.valueOf(instruction.getCurrentFloor()).getBytes();
		byte[] endBytes = String.valueOf(instruction.getDestinationFloor()).getBytes();
		
		// create new array of right size to copy into, note the + 2 is to add 0 bytes between instructions
		byte[] insByte = new byte[directionBytes.length + startBytes.length + endBytes.length + 2]; 
		System.arraycopy(directionBytes, 0, insByte, 0, directionBytes.length);
		insByte[directionBytes.length] = 0;
		System.arraycopy(startBytes, 0, insByte, directionBytes.length + 1, startBytes.length);
		insByte[directionBytes.length + 1 + startBytes.length] = 0;
		System.arraycopy(endBytes, 0, insByte, directionBytes.length + 1 + startBytes.length + 1, endBytes.length);
		
		return insByte;
	}
	
	public static Instructions byteArrayToInstructions(byte[] insArray) {
		int zeroPos1 = 0; 
		int zeroPos2 = 0;
		int count = 0;
		
		// get the position of byte 0s
		for (int i = 0; i < insArray.length; i++) {
			if (insArray[i] == (byte) 0) {
				if (count == 0) {
					zeroPos1 = i;
					count++;
				}
				else {
					zeroPos2 = i;
				}
			}
		}
		
		//Split into 3 strings for each attribute
		String direction = new String(Arrays.copyOfRange(insArray, 0, zeroPos1));
		String start = new String(Arrays.copyOfRange(insArray, zeroPos1 + 1, zeroPos2));
		String dest = new String(Arrays.copyOfRange(insArray, zeroPos2 + 1, insArray.length));
		
		// Elevator doesn't care about the time it receives a request, so we just default it to 0
		return new Instructions("00:00:00.0", start, direction, dest);
	}
}
