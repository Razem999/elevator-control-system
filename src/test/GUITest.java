package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.common.Constants;
import main.common.PacketHandler;
import main.common.Input.Fault;
import main.common.Input.FaultType;
import main.common.Input.Instructions;
import main.elevator.Elevator;
import main.elevator.Elevator.ElevatorState;
import main.floor.*;
import main.gui.Model;
import main.scheduler.*;

public class GUITest {
	static Model model;
	static ArrayList<PacketHandler> packetHandlers;
	
	/**
	 * Setup the model and packet handlers
	 */
	@BeforeAll
	static void setup() {
		packetHandlers = new ArrayList<PacketHandler>();
		for (int i = 0; i < Constants.NUM_CARS; i++) {
			packetHandlers.add(new PacketHandler(Constants.MODEL_PORT+i));
		}
		model = new Model();
		model.startListeners();
		
	}
	
	/**
	 * Test sending elevator statistics to listeners
	 */
	@Test
	void receiveElevatorStatusTest() {
		byte[] message = {1,2,0,0};
		for (int i = 0; i < Constants.NUM_CARS; i++) {
			packetHandlers.get(i).send(message);
		}
		for (int i = 0 ; i < Constants.NUM_CARS; i++) {
			System.out.println();

			assertTrue(model.getCurrentFloors()[i] == 1);
			assertTrue(model.getNextFloors()[i] == 2);
			assertTrue(model.getStates()[i] == ElevatorState.Idle);
			assertTrue(model.getFaults()[i] == null);
		}
	}
	
	/**
	 * Test for sending elevator faults to listeners
	 */
	@Test
	void receiveMotorFaultTest() {
		byte[] message = {1,2,0,1};
		for (int i = 0; i < Constants.NUM_CARS; i++) {
			packetHandlers.get(i).send(message);
		}
		for (int i = 0 ; i < Constants.NUM_CARS; i++) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			assertTrue(model.getFaults()[i] == FaultType.MotorFault);
		}
	}
	
	/**
	 * Test for sending open door faults to listeners
	 */
	@Test
	void receiveOpenDoorFaultTest() {
		byte[] message = {1,2,0,2};
		for (int i = 0; i < Constants.NUM_CARS; i++) {
			packetHandlers.get(i).send(message);
		}
		for (int i = 0 ; i < Constants.NUM_CARS; i++) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			assertTrue(model.getFaults()[i] == FaultType.OpenDoorFault);
		}
	}
	
	/**
	 * Test for sending close door faults to listeners
	 */
	@Test
	void receiveCloseDoorFaultTest() {
		byte[] message = {1,2,0,3};
		for (int i = 0; i < Constants.NUM_CARS; i++) {
			packetHandlers.get(i).send(message);
		}
		for (int i = 0 ; i < Constants.NUM_CARS; i++) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			assertTrue(model.getFaults()[i] == FaultType.CloseDoorFault);
		}
	}
	
	/**
	 * Test number of elevators that are tracking their current floors
	 */
	@Test
	void numCurrentFloorsTest() {
		assertTrue(model.getCurrentFloors().length == Constants.NUM_CARS);
	}
	
	/**
	 * Test number of elevators that are tracking their next floors
	 */
	@Test
	void numNextFloorsTest() {
		assertTrue(model.getNextFloors().length == Constants.NUM_CARS);
	}
	
	/**
	 * Test number of elevators that are tracking their current state
	 */
	@Test
	void numStatesTest() {
		assertTrue(model.getStates().length == Constants.NUM_CARS);
	}
	
	/**
	 * Test number of elevators that are tracking their fault state
	 */
	@Test
	void numFaultsTest() {
		assertTrue(model.getFaults().length == Constants.NUM_CARS);
	}
	
	/**
	 * Test number of elevators that are tracking their alive state
	 */
	@Test
	void numAliveTest() {
		assertTrue(model.getAreAlive().length == Constants.NUM_CARS);
	}
	
}
