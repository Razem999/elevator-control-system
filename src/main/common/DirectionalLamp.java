/**
 * 
 */
package main.common;

/**
 * Enum to represent an indicator lamp that displays an up or down arrow
 */
public enum DirectionalLamp {
	UP {
		@Override
		public String toString() {
			return "UP";
		}
	},
	DOWN {
		@Override
		public String toString() {
			return "DOWN";
		}
	}
}
