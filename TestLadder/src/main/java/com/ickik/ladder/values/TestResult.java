package com.ickik.ladder.values;


/**
 * Enum that represents the state of an executed test.
 * @author Patrick Allgeyer, pallgeyer@abusinessware.com
 * @version 0.1
 */
public enum TestResult {

	/**
	 * The executed test was successful.
	 */
	PASSED {
		@Override
		public String toString() {
			return "Passed";
		}
	},
	
	/**
	 * The test was not executed because the test on this test
	 * depends has occur an error.
	 */
	SKIPPED {
		@Override
		public String toString() {
			return "Skipped";
		}
	},
	
	/**
	 * The execution of the test occurs an error.
	 */
	FAILED {
		@Override
		public String toString() {
			return "Failed";
		}
	},
	
	NOT_EXECUTED {
		@Override
		public String toString() {
			return "Not Executed";
		}
	};
	
	/**
	 * Return the ID of the result given by argument.
	 * @param connection the database connection.
	 * @return the ID of the result given in  argument if it has been found.
	 */
	//public abstract int getID(DBConnection connection);
}
