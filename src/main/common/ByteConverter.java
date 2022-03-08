package main.common;

import java.util.Arrays;

public class ByteConverter {
	protected static byte[] instructionToByteArray(Instructions instruction) {		
		String ins = instruction.toString();
		
		// get rid of everything except the direction, starting floor and destination floor, then split into arrays
		String[] instructions = ins.substring(ins.indexOf(",")+1).replaceFirst("]", "").split(","); 
		byte[] directionBytes = instructions[0].getBytes();
		byte[] startBytes = instructions[1].getBytes();
		byte[] endBytes = instructions[2].getBytes();
		
		// create new array of right size to copy into, note the + 2 is to add 0 bytes between instructions
		byte[] insByte = new byte[directionBytes.length + startBytes.length + endBytes.length + 2]; 
		System.arraycopy(directionBytes, 0, insByte, 0, directionBytes.length);
		insByte[directionBytes.length] = 0;
		System.arraycopy(startBytes, 0, insByte, directionBytes.length + 1, startBytes.length);
		insByte[directionBytes.length + 1 + startBytes.length] = 0;
		System.arraycopy(endBytes, 0, insByte, directionBytes.length + 1 + startBytes.length + 1, endBytes.length);

		return insByte;
	}
	
	protected static Instructions byteArrayToInstructions(byte[] insArray) {
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
		
		return new Instructions("00:00:00.0", start, direction, dest);
	}
	
	public static void main(String[] args) {
		Instructions instruction = new Instructions("14:05:15.0", "3", "Up", "4");
		System.out.println(instruction.toString());
		byte[] test = instructionToByteArray(instruction);
		
		instruction = byteArrayToInstructions(test);
		
		System.out.println(instruction.toString());
		
		
	}
	
}
