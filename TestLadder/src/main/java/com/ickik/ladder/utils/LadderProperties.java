package com.ickik.ladder.utils;

/**
 * Properties saved in the properties file. This enum
 * contains all key word and default values of the TestLadder.
 * @author Patrick Allgeyer, p.allgeyer@gmail.com
 * @version 0.1.000, 08/03/11
 */
public enum LadderProperties {

	SERVER_NAME {
		@Override
		public String getDefaultValue() {
			return "";
		}
	},
	
	SERVER_PORT {
		@Override
		public String getDefaultValue() {
			return "";
		}
	},
	
	DATABASE {
		@Override
		public String getDefaultValue() {
			return "";
		}
	},
	
	SERVER_LOGIN {
		@Override
		public String getDefaultValue() {
			return "";
		}
	},
	
	SERVER_PASSWORD {
		@Override
		public String getDefaultValue() {
			return "";
		}
	},
	
	LOGIN {
		@Override
		public String getDefaultValue() {
			return "";
		}
	},
	
	DATABASE_TYPE {
		@Override
		public String getDefaultValue() {
			return "DB2";
		}
	};
	
	public abstract String getDefaultValue();
}
