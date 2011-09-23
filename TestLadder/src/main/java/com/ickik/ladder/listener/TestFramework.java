package com.ickik.ladder.listener;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.runner.JUnitCore;
import org.testng.TestNG;

import com.ickik.ladder.db.DBConnection;
import com.ickik.ladder.model.FileTestLadderModel;
import com.ickik.ladder.model.TestCaseResultTableModel;
import com.ickik.ladder.model.TestCaseTableModel;
import com.ickik.ladder.model.TestClassTableModel;
import com.ickik.ladder.model.VersionsTableModel;
import com.ickik.ladder.model.XMLTestFileTableModel;
import com.ickik.ladder.utils.LadderClassLoader;
import com.ickik.ladder.values.TypeFile;

/**
 * Contains all methods available for every test framework.
 * @author Patrick Allgeyer
 * @version 0.1.000, 23/03/11
 */
public enum TestFramework {

	TESTNG("frame.radio.testfile.testng") {

		@Override
		public void launch(DBConnection connection, String campaign, TestClassTableModel testClassModel, XMLTestFileTableModel xmlTestFileModel, TestCaseTableModel testCaseModel, FileTestLadderModel fileTestModel, long userId, VersionsTableModel versionModel, String version, TestCaseResultTableModel testResultModel) {
			TestNGTestListener listener = new TestNGTestListener(connection, campaign, testClassModel, xmlTestFileModel, testCaseModel, userId, versionModel, version, testResultModel);
			TestNG testNG = new TestNG();
			testNG.addListener(listener);
			
			/*if (fileTestModel.getFileType() == TypeFile.XML) {
				List<String> list = new ArrayList<String>();
				StringBuilder srcPath = new StringBuilder();
				for (File f : fileTestModel.getSelectedFiles()) {
					list.add(f.getAbsolutePath());
					String path = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separatorChar));
					//srcPath.append(path);
					//srcPath.append(";");
					path = path.substring(0, f.getAbsolutePath().lastIndexOf(File.separatorChar));
					//srcPath.append(path);
					//srcPath.append(";");
					path = path.substring(0, f.getAbsolutePath().lastIndexOf(File.separatorChar));
					//srcPath.append(path);
					//srcPath.append(";");
					path = path.substring(0, f.getAbsolutePath().lastIndexOf(File.separatorChar));
					//srcPath.append(path);
					//srcPath.append(";");
					path = path.substring(0, f.getAbsolutePath().lastIndexOf(File.separatorChar));
					srcPath.append(path);
					srcPath.append(";");
				}
				testNG.setSourcePath(srcPath.toString());
				testNG.setTestSuites(list);
				//testNG.addClassLoader(LadderClassLoader.getInstance());
				testNG.run();
			} else*/ if (fileTestModel.getFileType() == TypeFile.JAR) {
				for (File f : fileTestModel.getSelectedFiles()) {
					testNG.setTestJar(f.getAbsolutePath());
					testNG.addClassLoader(LadderClassLoader.getInstance());
					testNG.run();
				}
			} /*else if (fileTestModel.getFileType() == TypeFile.CLASS) {
				List<Class<?>> list = new ArrayList<Class<?>>();
				for (File f : fileTestModel.getSelectedFiles()) {
					try {
						Set<Class<?>> set = LadderClassLoader.getInstance().loadJar(f.getAbsolutePath());
						for (Class c : set) {
							if (c.isAnnotationPresent(this.getTestAnnotation())) {
								list.add(c);
								continue;
							}
							for (Method m : c.getMethods()) {
								if (m.isAnnotationPresent(this.getTestAnnotation())) {
									list.add(c);
									break;
								}
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Class[] array = new Class[list.size()];
				testNG.setTestClasses(list.toArray(array));
			}*/
			//testNG.setXmlSuites(suites);
			
			connection.updateCampaign(campaign, true);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Class getTestAnnotation() {
			return org.testng.annotations.Test.class;
		}
	},
	
	JUNIT("frame.radio.testfile.junit") {

		@Override
		public void launch(DBConnection connection, String campaign, TestClassTableModel testClassModel, XMLTestFileTableModel xmlTestFileModel, TestCaseTableModel testCaseModel, FileTestLadderModel fileTestModel, long userId, VersionsTableModel versionModel, String version, TestCaseResultTableModel testResultModel) {
			JUnitTestListener listener = new JUnitTestListener(connection);
			//JUnitTestRunner r = new JUnitTestRunner();
			
			JUnitCore core = new JUnitCore();
			core.addListener(listener);
			
			/*if (fileTestModel.getFileType() == TypeFile.XML) {
				for (File f : fileTestModel.getSelectedFiles()) {
					//XmlSuite s = new Xml
				}
			} else*/ if (fileTestModel.getFileType() == TypeFile.JAR) {
				for (File f : fileTestModel.getSelectedFiles()) {
					try {
						Set<Class<?>> set = LadderClassLoader.getInstance().loadJar(f.getAbsolutePath());
						for (Class c : set) {
							if (c.isAnnotationPresent(this.getTestAnnotation())) {
								core.run(c);
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}
			
			connection.updateCampaign(campaign, true);
		}

		@Override
		public Class<?> getTestAnnotation() {
			return org.junit.Test.class;
		}
	};
	
	private String id;
	
	private TestFramework(String id) {
		this.id = id;
	}
	
	public String getValue() {
		return id;
	}
	
	public abstract void launch(DBConnection connection, String campaign, TestClassTableModel testClassModel, XMLTestFileTableModel xmlTestFileModel, TestCaseTableModel testCaseModel, FileTestLadderModel fileTestModel, long userId, VersionsTableModel versionModel, String version, TestCaseResultTableModel testResultModel);
	@SuppressWarnings("rawtypes")
	public abstract Class getTestAnnotation();
}
