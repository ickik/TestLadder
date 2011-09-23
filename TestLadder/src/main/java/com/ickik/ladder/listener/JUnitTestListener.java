package com.ickik.ladder.listener;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.ickik.ladder.db.DBConnection;

/**
 * 
 * @author Patrick Allgeyer, pallgeyer@abusinessware.com
 * @version 0.1
 */
public class JUnitTestListener extends RunListener {
	
	private final DBConnection connection;
	
	public JUnitTestListener(DBConnection connection) {
		if (connection == null) {
			throw new NullPointerException();
		}
		this.connection = connection;
	}
	
	@Override
	public void testFinished(Description description) throws Exception {
		// TODO Auto-generated method stub
		super.testFinished(description);
	}
	
	@Override
	public void testFailure(Failure failure) throws Exception {
		// TODO Auto-generated method stub
		super.testFailure(failure);
	}
	
	@Override
	public void testIgnored(Description description) throws Exception {
		// TODO Auto-generated method stub
		super.testIgnored(description);
	}
}
