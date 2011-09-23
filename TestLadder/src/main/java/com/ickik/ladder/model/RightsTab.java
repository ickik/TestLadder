package com.ickik.ladder.model;

import java.util.ArrayList;
import java.util.List;

public enum RightsTab {

	CATEGORIES(0) {
		@Override
		public List<RightsFunction> getTabDetails() {
			List<RightsFunction> list = new ArrayList<RightsFunction>();
			list.add(RightsFunction.ADD_CATEGORY);
			return list;
		}
	},
	VERSIONS(10) {
		@Override
		public List<RightsFunction> getTabDetails() {
			List<RightsFunction> list = new ArrayList<RightsFunction>();
			list.add(RightsFunction.ADD_VERSION);
			return list;
		}
	},
	TEST_FILES(20) {
		@Override
		public List<RightsFunction> getTabDetails() {
			List<RightsFunction> list = new ArrayList<RightsFunction>();
			list.add(RightsFunction.CHOOSE_DIRECTORY);
			list.add(RightsFunction.SEARCH_IN_DIRECTORY);
			list.add(RightsFunction.SELECT_ALL);
			list.add(RightsFunction.EXECUTE);
			return list;
		}
	},
	XML_TEST_FILES(30) {
		@Override
		public List<RightsFunction> getTabDetails() {
			List<RightsFunction> list = new ArrayList<RightsFunction>();
			list.add(RightsFunction.ADD_XML_FILE);
			return list;
		}
	},
	TEST_CLASSES(40) {
		@Override
		public List<RightsFunction> getTabDetails() {
			List<RightsFunction> list = new ArrayList<RightsFunction>();
			list.add(RightsFunction.ADD_TEST_CLASS);
			return list;
		}
	},
	TEST_CASES(50) {
		@Override
		public List<RightsFunction> getTabDetails() {
			List<RightsFunction> list = new ArrayList<RightsFunction>();
			list.add(RightsFunction.TC_VIEW_CATEGORY);
			list.add(RightsFunction.TC_VIEW_XML_TEST_FILE);
			list.add(RightsFunction.TC_VIEW_TEST_CLASS);
			list.add(RightsFunction.TC_VIEW_METHOD);
			list.add(RightsFunction.TC_VIEW_DESCRIPTION);
			list.add(RightsFunction.TC_VIEW_TEST_CASE);
			list.add(RightsFunction.TC_VIEW_STATE);
			list.add(RightsFunction.TC_VIEW_VERSION);
			list.add(RightsFunction.TC_VIEW_DATE_CREATION);
			list.add(RightsFunction.TC_VIEW_USER_CREATION);
			list.add(RightsFunction.TC_VIEW_LAST_MODIFIED_VERSION);
			list.add(RightsFunction.TC_VIEW_LAST_MODIFIED_USER);
			list.add(RightsFunction.TC_VIEW_LAST_MODIFIED_DATE);
			list.add(RightsFunction.TC_INSERTION);
			list.add(RightsFunction.TC_MODIFY);
			list.add(RightsFunction.TC_SEARCH);
			list.add(RightsFunction.TC_CLEAN);
			return list;
		}
	},
	EXECUTED_TEST(80) {
		@Override
		public List<RightsFunction> getTabDetails() {
			List<RightsFunction> list = new ArrayList<RightsFunction>();
			list.add(RightsFunction.TE_VIEW_CATEGORY);
			list.add(RightsFunction.TE_VIEW_XML_TEST_FILE);
			list.add(RightsFunction.TE_VIEW_TEST_CLASS);
			list.add(RightsFunction.TE_VIEW_METHOD);
			list.add(RightsFunction.TE_VIEW_DESCRIPTION);
			list.add(RightsFunction.TE_VIEW_TEST_CASE);
			list.add(RightsFunction.TE_VIEW_STATE);
			list.add(RightsFunction.TE_VIEW_VERSION);
			list.add(RightsFunction.TE_VIEW_DATE_CREATION);
			list.add(RightsFunction.TE_VIEW_USER_CREATION);
			list.add(RightsFunction.TE_VIEW_LAST_MODIFIED_VERSION);
			list.add(RightsFunction.TE_VIEW_LAST_MODIFIED_USER);
			list.add(RightsFunction.TE_VIEW_LAST_MODIFIED_DATE);
//			list.add(RightsFunction.TE_INSERTION);
//			list.add(RightsFunction.TE_MODIFY);
			list.add(RightsFunction.TE_SEARCH);
			list.add(RightsFunction.TE_CLEAN);
			return list;
		}
	},
	USERS(110) {
		@Override
		public List<RightsFunction> getTabDetails() {
			List<RightsFunction> list = new ArrayList<RightsFunction>();
			list.add(RightsFunction.USER_VIEW_LOGIN);
			list.add(RightsFunction.USER_VIEW_PASSWORD);
			list.add(RightsFunction.USER_VIEW_RIGHTS);
			list.add(RightsFunction.USER_ADD);
			list.add(RightsFunction.USER_MODIFY);
			list.add(RightsFunction.USER_REMOVE);
			list.add(RightsFunction.USER_SEARCH);
			list.add(RightsFunction.USER_CLEAR);
			return list;
		}
	};
	
	private int index;
	
	private RightsTab(int index) {
		this.index = index;
	}
	
	public int getValue() {
		return index;
	}
	
	public abstract List<RightsFunction> getTabDetails();
}
