package com.ickik.ladder.model;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.ickik.ladder.utils.LadderClassLoader;
import com.ickik.ladder.values.TypeFile;

/**
 * 
 * @author Patrick Allgeyer
 * @version 0.1.000
 */
@SuppressWarnings("serial")
public class FileTestLadderModel extends AbstractTableModel {

	private TypeFile extension;
	private final List<File> fileList = new ArrayList<File>();
	private final List<Boolean> choiceList = new ArrayList<Boolean>();
	
	public FileTestLadderModel() {
	}
	
	private void addFile(File file) {
		fileList.add(file);
		choiceList.add(false);
		fireTableRowsInserted(fileList.size() - 1, fileList.size());
	}
	
	public void selectAllFile(boolean select) {
		for (int i = 0; i < choiceList.size(); i++) {
			choiceList.set(i, select);
			fireTableRowsUpdated(i, i);
		}
	}
	
	public List<File> getFileList(File directory, TypeFile extension) {
		List<File> xmlTestFileList = new ArrayList<File>();
		this.extension = extension;
		getFileList(directory, xmlTestFileList);
		return xmlTestFileList;
	}
	
	private void getFileList(File directory, List<File> fileList) {
		if (!directory.isDirectory()) {
			return ;
		}
		File[] fileArray = directory.listFiles(getXMLFileFilter());
		if (fileArray != null) {
			for (File file : fileArray) {
				if (file != null) {
					if (file.getName().endsWith(extension.getExtension())) {
						if (extension.isValidFile(file)) {
							addFile(file);
						}
//						if (isTestXMLFile(file)) {
//							addFile(file);
//							continue;
//						}
//						
//						if (isTestClassFile(file)) {
//							System.out.println(file.getAbsolutePath());
//							addFile(file);
//							continue;
//						}
					} else {
						getFileList(file, fileList);
					}
				}
			}
		}
	}

	private FileFilter getXMLFileFilter() {
		return new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory() || pathname.getName().endsWith(extension.getExtension())) {
					return true;
				}
				return false;
			}
		};
	}
	
	private boolean isTestClassFile(File file) {
		//try {
			LadderClassLoader lcl = LadderClassLoader.getInstance();
			//Class<?> classe = lcl.desChargements(file);
			Set<Class<?>> classe = null;
			try {
				classe = lcl.loadJar(file.getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (classe != null) {
				//Class<?> classe = Class.forName(file.getAbsolutePath());
//				org.testng.annotations.Test testAnnotation = classe.getAnnotation(org.testng.annotations.Test.class);
//				if (testAnnotation != null) {
//					return true;
//				}
//				org.junit.Test testAnnotationJUnit = classe.getAnnotation(org.junit.Test.class);
//				if (testAnnotationJUnit != null) {
//					return true;
//				}
//				for (Method m : classe.getDeclaredMethods()) {
//					if (m.isAnnotationPresent(org.testng.annotations.Test.class)) {
//						return true;
//					} else if (m.isAnnotationPresent(org.junit.Test.class)) {
//						return true;
//					}
//				}
			}
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return false;
	}

	@Override
	public String getColumnName(int column) {
		switch(column) {
		case 0:
			return "Selected";
		case 1:
			return "File";
		}
		return null;
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return fileList.size();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex) {
		case 0:
			return Boolean.class;
		case 1:
			return File.class;
		}
		return null;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			if (aValue instanceof Boolean) {
				Boolean b = (Boolean) aValue;
				choiceList.set(rowIndex, b);
				fireTableCellUpdated(rowIndex, columnIndex);
			}
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
		case 0:
			return new Boolean(choiceList.get(rowIndex));
		case 1:
			return fileList.get(rowIndex);
		}
		return null;
	}
	
	public List<File> getSelectedFiles() {
		List<File> list = new ArrayList<File>();
		for (int i = 0; i < choiceList.size(); i++) {
			if (choiceList.get(i)) {
				list.add(fileList.get(i));
			}
		}
		return list;
	}
	
	public TypeFile getFileType() {
		for (TypeFile tf : TypeFile.values()) {
			if (extension.getExtension().equals(tf.getExtension())) {
				return tf;
			}
		}
		return null;
	}
}
