/**
 * 
 */
package main.common;

/**
 * Enum to represent an indicator lamp that displays an up or down arrow
 */
public enum Direction {
	UP {
		public String toString() {
			return "UP";
		}
	},
	DOWN {
		public String toString() {
			return "DOWN";
		}
	},
	OFF {
		public String toString() {
			return "OFF";
		}
	}
}
