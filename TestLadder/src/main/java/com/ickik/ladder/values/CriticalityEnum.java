package com.ickik.ladder.values;

public enum CriticalityEnum {

	LOW {
		@Override
		public String toString() {
			return "Low";
		}
	},
	
	MEDIUM{
		@Override
		public String toString() {
			return "Medium";
		}
	},
	
	HIGH{
		@Override
		public String toString() {
			return "High";
		}
	},
	
	CRITICAL{
		@Override
		public String toString() {
			return "Critical";
		}
	};
}
