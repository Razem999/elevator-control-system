/**
 * 
 */
package main.common.Input;

/**
 * An enumeration class representing valid fault types.
 */
public enum FaultType {
	MotorFault {
		@Override
		public String toString() {
			return "MOTORFAULT";
		}
	},
	OpenDoorFault {
		@Override
		public String toString() {
			return "OPENDOORFAULT";
		}
	},
	CloseDoorFault {
		@Override
		public String toString() {
			return "CLOSEDOORFAULT";
		}
	}
}
