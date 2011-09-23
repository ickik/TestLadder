package com.ickik.ladder.values;

public enum Type {

	MANUAL {
		@Override
		public String toString() {
			return "Manual";
		}
	},
	AUTOMATED {
		@Override
		public String toString() {
			return "Automated";
		}
	};
}
