package com.ickik.ladder.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

import com.ickik.ladder.db.DBConnection;
import com.ickik.ladder.exception.LadderException;
import com.ickik.ladder.listener.TestFramework;
import com.ickik.ladder.listener.UpdateStatsListener;
import com.ickik.ladder.model.AbstractLadderTableModel;
import com.ickik.ladder.model.CampaignTableModel;
import com.ickik.ladder.model.CategoryTableModel;
import com.ickik.ladder.model.FileTestLadderModel;
import com.ickik.ladder.model.RightsFunction;
import com.ickik.ladder.model.RightsTab;
import com.ickik.ladder.model.TestCaseResultCampaignAdapter;
import com.ickik.ladder.model.TestCaseResultCampaignAdapter.Stats;
import com.ickik.ladder.model.TestCaseResultTableModel;
import com.ickik.ladder.model.TestCaseTableModel;
import com.ickik.ladder.model.TestClassTableModel;
import com.ickik.ladder.model.TestResultModel;
import com.ickik.ladder.model.UserModel;
import com.ickik.ladder.model.VersionsTableModel;
import com.ickik.ladder.model.XMLTestFileTableModel;
import com.ickik.ladder.values.Campaign;
import com.ickik.ladder.values.Category;
import com.ickik.ladder.values.Criticality;
import com.ickik.ladder.values.CriticalityEnum;
import com.ickik.ladder.values.Result;
import com.ickik.ladder.values.State;
import com.ickik.ladder.values.Test;
import com.ickik.ladder.values.TestCase;
import com.ickik.ladder.values.TestClass;
import com.ickik.ladder.values.TestResult;
import com.ickik.ladder.values.TestType;
import com.ickik.ladder.values.TypeFile;
import com.ickik.ladder.values.User;
import com.ickik.ladder.values.Version;
import com.ickik.ladder.values.XMLFile;
import com.toedter.calendar.JDateChooser;

/**
 * This class creates the frame that display all menus available on
 * every users.
 * @author Patrick Allgeyer
 * @version 0.1.000
 */
public class LadderFrame extends AbstractTestLadderView {

	/**
	 * The version constant which is display in the head of the frame.
	 */
	public static final String VERSION = "0.1";
	
	/**
	 * The name of the soft constant which is display in the head of the frame.
	 */
	public static final String SOFTWARE = "TestLadder";

	private final JFrame ladderFrame;
	private final FileTestLadderModel fileLadderModel;
	private final VersionsTableModel versionTableModel;
	private final CategoryTableModel categoryTableModel;
	private final XMLTestFileTableModel xmlTestFileTableModel;
	private final TestClassTableModel testClassTableModel;
	private final TestCaseTableModel testCaseTableModel;
	private final TestCaseResultTableModel testCaseResultTableModel;
	private final TestResultModel testResultModel;
	private final UserModel userModel;
	private final CampaignTableModel campaignModel;
	private final User user;
	private final DBConnection connection;
	private final int VERSION_MAX_LENGTH = 12;
	private final int METHOD_MAX_LENGTH = 30;
	private final int CATEGORY_MAX_LENGTH = 20;
	private final int XML_TEST_FILE_MAX_LENGTH = 20;
	private final int TEST_CLASS_MAX_LENGTH = 20;
	private final int DESCRIPTION_MAX_LENGTH = 1000;
	private final int ACTION_MAX_LENGTH = 2900;
	
	/**
	 * Constructor of the class which instantiates all models.
	 * @throws LadderException 
	 */
	public LadderFrame(ResourceBundle resourceBundle, User user, DBConnection connection) throws LadderException {
		super(resourceBundle);
		this.user = user;
		this.connection = connection;
		JWindow window = createPatientFrame();
	    centeredWindow(window);
	    ladderFrame = new JFrame(SOFTWARE + " " + VERSION);
		fileLadderModel = new FileTestLadderModel();
		versionTableModel = new VersionsTableModel(connection);
		categoryTableModel = new CategoryTableModel(connection);
		xmlTestFileTableModel = new XMLTestFileTableModel(connection);
		testClassTableModel = new TestClassTableModel(connection);
		testResultModel = new TestResultModel(connection);
		campaignModel = new CampaignTableModel(connection);
		testCaseTableModel = new TestCaseTableModel(connection, versionTableModel, categoryTableModel, xmlTestFileTableModel, testClassTableModel);
		testCaseResultTableModel = new TestCaseResultTableModel(connection);
		userModel = new UserModel(connection);
		initFrame();
		window.dispose();
		window = null;
	}
	
	private JWindow createPatientFrame() {
		JWindow window = new JWindow();
	    RoundWaiter waiter = new RoundWaiter(null, 10);
	    window.getContentPane().add(waiter);
	    window.pack();
	    window.setVisible(true);
	    return window;
	}
	
	private void initFrame() {
		ladderFrame.add(createTabbedPane());
		ladderFrame.setJMenuBar(createJMenuBar());
		//ladderFrame.pack();
		ladderFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		ladderFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ladderFrame.setVisible(true);
		centeredFrame(ladderFrame);
	}
	
	private boolean isVisible(RightsTab rTab) {
		if (user.getRights().charAt(rTab.getValue()) == '1') {
			return true;
		}
		return false;
	}
	
	private boolean isVisible(RightsFunction rFunction) {
		if (user.getRights().charAt(rFunction.getValue()) == '1') {
			return true;
		}
		return false;
	}
	
	private JTabbedPane createTabbedPane() {
		JTabbedPane tabPane = new JTabbedPane();
		if (isVisible(RightsTab.TEST_FILES)) {
			tabPane.addTab(getResourceBundle().getString("frame.tab.testfile.title"), null, createTestListPanel(), getResourceBundle().getString("frame.tab.testfile.tiptool"));
		}
		if (isVisible(RightsTab.VERSIONS)) {
			tabPane.addTab(getResourceBundle().getString("frame.tab.version.title"), null, createVersionPanel(), getResourceBundle().getString("frame.tab.version.tiptool"));
		}
		if (isVisible(RightsTab.CATEGORIES)) {
			tabPane.addTab(getResourceBundle().getString("frame.tab.category.title"), null, createCategoriesPanel(), getResourceBundle().getString("frame.tab.category.tiptool"));
		}
		if (isVisible(RightsTab.XML_TEST_FILES)) {
			tabPane.addTab(getResourceBundle().getString("frame.tab.xmltestfile.title"), null,  createXMLTestFilePanel(), getResourceBundle().getString("frame.tab.xmltestfile.tiptool"));
		}
		if (isVisible(RightsTab.TEST_CLASSES)) {
			tabPane.addTab(getResourceBundle().getString("frame.tab.testclass.title"), null, createTestClassPanel(), getResourceBundle().getString("frame.tab.testclass.tiptool"));
		}
		if (isVisible(RightsTab.TEST_CASES)) {
			tabPane.addTab(getResourceBundle().getString("frame.tab.testcase.title"), null, createTestCasePanel(), getResourceBundle().getString("frame.tab.testcase.tiptool"));
		}
		if (isVisible(RightsTab.EXECUTED_TEST)) {
			tabPane.addTab(getResourceBundle().getString("frame.tab.testresult.title"), null, createTestResultPanel(), getResourceBundle().getString("frame.tab.testresult.tiptool"));
		}
		//if (isVisible(RightsTab.)) {
			tabPane.addTab(getResourceBundle().getString("frame.tab.campaign.title"), null, createCampaignPanel(), getResourceBundle().getString("frame.tab.campaign.tiptool"));
		//}
		if (isVisible(RightsTab.USERS)) {
			tabPane.addTab(getResourceBundle().getString("frame.tab.users.title"), null, createUsersPanel(), getResourceBundle().getString("frame.tab.users.tiptool"));
		}
		return tabPane;
	}
	
	private JPanel createTestListPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		final JTextField pathField = new JTextField(20);
		JButton fileChooser = new JButton(getResourceBundle().getString("frame.button.choose_directory"));
		final JRadioButton[] typeFileArray = getRadioButtonArray();
		
		final JButton searchButton = new JButton(getResourceBundle().getString("frame.button.search_directory"));
		searchButton.setEnabled(false);
		
		fileChooser.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = getFileChooser();
				if (fc.showOpenDialog(ladderFrame) == JFileChooser.APPROVE_OPTION) {
					pathField.setText(fc.getSelectedFile().getAbsolutePath());
					searchButton.setEnabled(true);
				}
			}
		});
		
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int len = TypeFile.values().length;
				for (int i = 0; i < len; i++) {
					if (typeFileArray[i].isSelected()) {
						fileLadderModel.getFileList(new File(pathField.getText()), TypeFile.values()[i]);
						break;
					}
				}
			}
		});
		
		JPanel head = new JPanel();
		head.setLayout(new BoxLayout(head, BoxLayout.X_AXIS));
		head.add(pathField);
		head.add(fileChooser);
		head.add(new JLabel(getResourceBundle().getString("frame.radio.testfile.label")));
		for (JRadioButton rb : typeFileArray) {
			head.add(rb);
		}
		head.add(searchButton);
		
		final JTable fileTable = new JTable(fileLadderModel);
		
		JScrollPane scrollTable = new JScrollPane(fileTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JPanel foot = new JPanel();
		final JButton selectDeselectAll = new JButton(getResourceBundle().getString("frame.button.selection"));
		
		selectDeselectAll.addActionListener(new ActionListener() {
			private boolean value = true;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fileLadderModel.selectAllFile(value);
				if (value) {
					selectDeselectAll.setText(getResourceBundle().getString("frame.button.deselection"));
				} else {
					selectDeselectAll.setText(getResourceBundle().getString("frame.button.selection"));
				}
				value = !value;
			}
		});
		
		JButton executeTestButton = new JButton(getResourceBundle().getString("frame.button.execution"));
		
		foot.add(selectDeselectAll);
		foot.add(executeTestButton);
		final ButtonGroup group = new ButtonGroup();
		int len = TestFramework.values().length;
		final JRadioButton[] radioArray = new JRadioButton[len];
		for (int i = 0; i < len; i++) {
			JRadioButton rb = new JRadioButton(getResourceBundle().getString(TestFramework.values()[i].getValue()));
			radioArray[i] = rb;
			group.add(rb);
			foot.add(rb);
		}
		radioArray[0].setSelected(true);
		executeTestButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<File> fileList = fileLadderModel.getSelectedFiles();
				if (fileList.isEmpty()) {
					displayErrorMessage(ladderFrame, "frame.tab.testfile.execution.list_empty");
					return;
				}
				String campaign = null;
				boolean isCampaigneName = false;
				do {
					isCampaigneName = false;
					campaign = (String) JOptionPane.showInputDialog(ladderFrame, getResourceBundle().getString("frame.tab.testfile.insert.campaign"), SOFTWARE + " " + VERSION, JOptionPane.PLAIN_MESSAGE, null, null, "");
					if (campaign.isEmpty()) {
						displayErrorMessage(ladderFrame, "frame.tab.testfile.execution.campaign_empty");
						isCampaigneName = true;
					}
				} while(isCampaigneName);
				isCampaigneName = false;
				String version = null;
				do {
					isCampaigneName = false;
					version = (String) JOptionPane.showInputDialog(ladderFrame, getResourceBundle().getString("frame.tab.testfile.insert.version"), SOFTWARE + " " + VERSION, JOptionPane.PLAIN_MESSAGE, null, versionTableModel.getMap().keySet().toArray(), "");
					if (campaign.isEmpty()) {
						displayErrorMessage(ladderFrame, "frame.tab.testfile.execution.version_empty");
						isCampaigneName = true;
					}
				} while(isCampaigneName);
				int len = TestFramework.values().length;
				for (int i = 0 ; i < len; i++) {
					if (radioArray[i].isSelected()) {
						TestFramework.values()[i].launch(connection, campaign, testClassTableModel, xmlTestFileTableModel, testCaseTableModel, fileLadderModel, user.getId(), versionTableModel, version, testCaseResultTableModel);
					}
				}
			}
		});
		
		panel.add(head, BorderLayout.NORTH);
		panel.add(scrollTable, BorderLayout.CENTER);
		panel.add(foot, BorderLayout.SOUTH);
		return panel;
	}
	
	private JRadioButton[] getRadioButtonArray() {
		ButtonGroup groupTypeFiles = new ButtonGroup();
		int len = TypeFile.values().length;
		final JRadioButton[] array = new JRadioButton[len];
		for (int i = 0; i < len; i++) {
			JRadioButton rb = new JRadioButton(getResourceBundle().getString(TypeFile.values()[i].getValue()));
			array[i] = rb;
			groupTypeFiles.add(rb);
		}
		array[0].setSelected(true);
		return array;
	}
	
	private JFileChooser getFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		return fileChooser;
	}
	
	private JPanel createVersionPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel head = new JPanel();
		head.setLayout(new BoxLayout(head, BoxLayout.X_AXIS));
		final JTextField versionTextField = new JTextField();
		
		versionTextField.addKeyListener(getKeyListener(versionTextField, VERSION_MAX_LENGTH));
		
		JButton addVersion = new JButton("Add Version");
		addVersion.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if ("".equals(versionTextField.getText().trim())) {
					displayErrorMessage(ladderFrame, "frame.tab.version.insertion.empty");
					return ;
				}
				if (!versionTableModel.addVersion(versionTextField.getText().trim())) {
					displayErrorMessage(ladderFrame, "connection.getID.version.duplicate");
					return ;
				}
			}
		});
		head.add(versionTextField);
		head.add(addVersion);
		
		final JTable versionTable = new JTable(versionTableModel);
		JScrollPane scrollPane = new JScrollPane(versionTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		versionTableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				versionTable.validate();
				versionTextField.setText("");
			}
		});
		panel.add(head, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}
	
	private JPanel createCategoriesPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel head = new JPanel();
		head.setLayout(new BoxLayout(head, BoxLayout.X_AXIS));
		final JTextField categoryTextField = new JTextField();
		categoryTextField.addKeyListener(getKeyListener(categoryTextField, CATEGORY_MAX_LENGTH));
		
		JButton addCategory = new JButton("Add Category");
		addCategory.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if ("".equals(categoryTextField.getText().trim())) {
					displayErrorMessage(ladderFrame, "The category to insert cannot be empty");
					return;
				}
				try {
					if (!categoryTableModel.addCategory(categoryTextField.getText().trim())) {
						displayErrorMessage(ladderFrame, "The category is already existing");
						return;
					}
				} catch (LadderException e1) {
					displayErrorMessage(ladderFrame, e1.getMessage());
				}
			}
		});
		head.add(categoryTextField);
		head.add(addCategory);
		
		final JTable categoryTable = new JTable(categoryTableModel);
		JScrollPane scrollPane = new JScrollPane(categoryTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		categoryTableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				categoryTable.validate();
				categoryTextField.setText("");
			}
		});
		panel.add(head, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}
	
	private JPanel createXMLTestFilePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel head = new JPanel();
		head.setLayout(new BoxLayout(head, BoxLayout.X_AXIS));
		final JTextField xmlTestFileTextField = new JTextField();
		xmlTestFileTextField.addKeyListener(getKeyListener(xmlTestFileTextField, XML_TEST_FILE_MAX_LENGTH));
		head.setBorder(BorderFactory.createTitledBorder("Insertion"));
		JButton addXMLTestFile = new JButton("Add XML test file");
		addXMLTestFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if ("".equals(xmlTestFileTextField.getText().trim())) {
					displayErrorMessage(ladderFrame, "The XML file to insert cannot be empty");
					return ;
				}
				if (!xmlTestFileTableModel.addXMLTestFile(xmlTestFileTextField.getText().trim())) {
					displayErrorMessage(ladderFrame, "The XML file to insert cannot be empty");
					return ;
				}
			}
		});
		head.add(xmlTestFileTextField);
		head.add(addXMLTestFile);
		
		final JTable xmlTestFileTable = new JTable(xmlTestFileTableModel);
		JScrollPane scrollPane = new JScrollPane(xmlTestFileTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		scrollPane.setBorder(BorderFactory.createTitledBorder("XML Test File"));
		xmlTestFileTableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				xmlTestFileTable.validate();
				xmlTestFileTextField.setText("");
			}
		});
		panel.add(head, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}
	
	private JPanel createTestClassPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel head = new JPanel();
		head.setLayout(new BoxLayout(head, BoxLayout.X_AXIS));
		final JTextField testClassTextField = new JTextField();
		testClassTextField.addKeyListener(getKeyListener(testClassTextField, TEST_CLASS_MAX_LENGTH));
		head.setBorder(BorderFactory.createTitledBorder("Insertion"));
		JButton testClassFile = new JButton("Add Test Class");
		testClassFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if ("".equals(testClassTextField.getText().trim())) {
					displayErrorMessage(ladderFrame, "The class name insert cannot be empty");
					return ;
				}
				if (!testClassTableModel.addTestClass(testClassTextField.getText().trim())) {
					displayErrorMessage(ladderFrame, "The class name file already exist");
					return ;
				}
			}
		});
		head.add(testClassTextField);
		head.add(testClassFile);
		
		final JTable xmlTestFileTable = new JTable(testClassTableModel);
		JScrollPane scrollPane = new JScrollPane(xmlTestFileTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		scrollPane.setBorder(BorderFactory.createTitledBorder("Test Classes"));
		testClassTableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				xmlTestFileTable.validate();
				testClassTextField.setText("");
			}
		});
		panel.add(head, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}
	
	private KeyListener getKeyListener(final JTextComponent textField, final int max) {
		return new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (textField.getText().length() >= max) {
					textField.setText(textField.getText().substring(0, max));
				}
			}
		};
	}
	
	private JPanel createTestCasePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel head = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		final JComboBox categoryComboBox = createComboBox(categoryTableModel);
		final JComboBox xmlTestFileComboBox = createComboBox(xmlTestFileTableModel);
		final JComboBox testClassComboBox = createComboBox(testClassTableModel);
		final JTextField methodTextField = new JTextField();
		methodTextField.addKeyListener(getKeyListener(methodTextField, METHOD_MAX_LENGTH));
		
		final JTextArea descriptionTextArea = new JTextArea();
		descriptionTextArea.addKeyListener(getKeyListener(descriptionTextArea, DESCRIPTION_MAX_LENGTH));
		descriptionTextArea.setEditable(true);
		descriptionTextArea.setRows(4);
		descriptionTextArea.setLineWrap(true);
		JScrollPane descriptionScroll = new JScrollPane(descriptionTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		final JTextArea actionTextArea = new JTextArea();
		actionTextArea.addKeyListener(getKeyListener(actionTextArea, ACTION_MAX_LENGTH));
		actionTextArea.setEditable(true);
		actionTextArea.setRows(4);
		actionTextArea.setLineWrap(true);
		JScrollPane actionScroll = new JScrollPane(actionTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		final JComboBox stateComboBox = new JComboBox();
		stateComboBox.addItem("");
		for (String item : testCaseTableModel.getStatesMap().keySet()) {
			stateComboBox.addItem(item);
		}

		final JComboBox versionComboBox = createComboBox(versionTableModel);
		final JComboBox criticalityComboBox = new JComboBox();
		criticalityComboBox.addItem("");
		for (String item : testCaseTableModel.getCriticitiesMap().keySet()) {
			criticalityComboBox.addItem(item);
		}
		
		final JComboBox lastModifiedVersionComboBox = createComboBox(versionTableModel);
		
		
		JPanel userDateMenu = new JPanel();
		BoxLayout box = new BoxLayout(userDateMenu, BoxLayout.X_AXIS);
		userDateMenu.setLayout(box);
		final JComboBox userCreatorComboBox = createComboBox(userModel);
		final JComboBox userModifierComboBox = createComboBox(userModel);
	
		JLabel creationBeginDateLabel = new JLabel("Creation date between");
		final JDateChooser creationBeginDateChooser = new JDateChooser();

		JLabel creationEndDateLabel = new JLabel("and");
		final JDateChooser creationEndDateChooser = new JDateChooser();

		JLabel modificationBeginDateLabel = new JLabel("Modification date between");
		final JDateChooser modificationBeginDateChooser = new JDateChooser();

		JLabel modificationEndDateLabel = new JLabel("and");
		final JDateChooser modificationEndDateChooser = new JDateChooser();
		
		userDateMenu.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.user_creator")));
		userDateMenu.add(userCreatorComboBox);
		userDateMenu.add(creationBeginDateLabel);
		userDateMenu.add(creationBeginDateChooser);
		userDateMenu.add(creationEndDateLabel);
		userDateMenu.add(creationEndDateChooser);
		userDateMenu.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.user_modifier")));
		userDateMenu.add(userModifierComboBox);
		userDateMenu.add(modificationBeginDateLabel);
		userDateMenu.add(modificationBeginDateChooser);
		userDateMenu.add(modificationEndDateLabel);
		userDateMenu.add(modificationEndDateChooser);
		
		JPanel headMenu = new JPanel(new GridLayout(1, 6));
		final JButton insert = new JButton(getResourceBundle().getString("frame.button.insert"));
		final JButton modify = new JButton(getResourceBundle().getString("frame.button.modify"));
		final JButton search = new JButton(getResourceBundle().getString("frame.button.search"));
		final JButton clean = new JButton(getResourceBundle().getString("frame.button.clean"));
		final JButton duplicateSelectedTestCase = new JButton(getResourceBundle().getString("frame.button.duplicate"));
		final JButton createCampaignSelectedTestCase = new JButton(getResourceBundle().getString("frame.button.create_campaign"));
		final JTable testCaseTable = new JTable(testCaseTableModel);
		
		insert.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean error = false;
				List<String> errorList = new ArrayList<String>();
				if (categoryComboBox.getSelectedIndex() == 0) {
					error = true;
					errorList.add("update.testcase.exist.category");
				}
				if (descriptionTextArea.getText().trim().isEmpty()) {
					error = true;
					errorList.add("update.testcase.exist.description");
				}
				if (actionTextArea.getText().trim().isEmpty()) {
					error = true;
					errorList.add("update.testcase.exist.testcase");
				}
				if (stateComboBox.getSelectedIndex() == 0) {
					error = true;
					errorList.add("update.testcase.exist.state");
				}
				if (versionComboBox.getSelectedIndex() == 0) {
					error = true;
					errorList.add("update.testcase.exist.version");
				}
				if (error) {
					displayErrorMessage(ladderFrame, "update.testcase.exist", errorList.toArray(new String[errorList.size()]));
					return ;
				}
				TestCase testCase = new TestCase();
				Category c = new Category();
				c.setCategory((String)categoryComboBox.getSelectedItem());
				c.setId(categoryTableModel.getMap().get(categoryComboBox.getSelectedItem()));
				testCase.setCategory(c);
				XMLFile x = new XMLFile();
				x.setId(xmlTestFileTableModel.getMap().get(xmlTestFileComboBox.getSelectedItem()));
				x.setXmlFile((String) xmlTestFileComboBox.getSelectedItem());
				testCase.setXmlFile(x);
				TestClass tc = new TestClass();
				tc.setId(testClassTableModel.getMap().get(testClassComboBox.getSelectedItem()));
				tc.setTestClass((String) testClassComboBox.getSelectedItem());
				testCase.setTestClass(tc);
				testCase.setMethod(methodTextField.getText().trim());
				testCase.setDescription(descriptionTextArea.getText().trim());
				testCase.setAction(actionTextArea.getText().trim());
				State s = new State();
				s.setId(testCaseTableModel.getStatesMap().get(stateComboBox.getSelectedItem()));
				s.setState((String) stateComboBox.getSelectedItem());
				testCase.setState(s);
				Version v = new Version();
				v.setId(versionTableModel.getMap().get(versionComboBox.getSelectedItem()));
				v.setVersion((String) versionComboBox.getSelectedItem());
				testCase.setVersion(v);
				//Version lv = new Version();
				//lv.setId(versionTableModel.getMap().get(lastModifiedVersionComboBox.getSelectedItem()));
				testCase.setLastModifiedVersion(v);
				Criticality cr = new Criticality();
				cr.setId(testCaseTableModel.getCriticitiesMap().get(criticalityComboBox.getSelectedItem()));
				cr.setCriticality((String) criticalityComboBox.getSelectedItem());
				testCase.setCriticality(cr);
				testCase.setUserCreator(user);
				testCase.setUserModifier(user);
				if (!testCaseTableModel.addTestCase(testCase)) {
					displayErrorMessage(ladderFrame, "insertion.test_case");
					return ;
				}
				categoryComboBox.setSelectedIndex(0);
				xmlTestFileComboBox.setSelectedIndex(0);
				testClassComboBox.setSelectedIndex(0);
				methodTextField.setText("");
				descriptionTextArea.setText("");
				actionTextArea.setText("");
				stateComboBox.setSelectedIndex(0);
				versionComboBox.setSelectedIndex(0);
				lastModifiedVersionComboBox.setSelectedIndex(0);
				modify.setEnabled(false);
				criticalityComboBox.setSelectedIndex(0);
				userCreatorComboBox.setSelectedIndex(0);
				userModifierComboBox.setSelectedIndex(0);
				creationBeginDateChooser.cleanup();
				creationEndDateChooser.cleanup();
				modificationBeginDateChooser.cleanup();
				modificationEndDateChooser.cleanup();
			}
		});
		
		
		modify.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean error = false;
				List<String> errorList = new ArrayList<String>();
				if (categoryComboBox.getSelectedIndex() == 0) {
					error = true;
					errorList.add("update.testcase.exist.category");
				}
				if (descriptionTextArea.getText().trim().isEmpty()) {
					error = true;
					errorList.add("update.testcase.exist.description");
				}
				if (actionTextArea.getText().trim().isEmpty()) {
					error = true;
					errorList.add("update.testcase.exist.testcase");
				}
				if (stateComboBox.getSelectedIndex() == 0) {
					error = true;
					errorList.add("update.testcase.exist.state");
				}
				if (versionComboBox.getSelectedIndex() == 0) {
					error = true;
					errorList.add("update.testcase.exist.version");
				}
				if (lastModifiedVersionComboBox.getSelectedIndex() == 0) {
					error = true;
					errorList.add("update.testcase.exist.last_modified_version");
				}
				if (criticalityComboBox.getSelectedIndex() == 0) {
					error = true;
					errorList.add("update.testcase.exist.criticality");
				}
				if (error) {
					displayErrorMessage(ladderFrame, "update.testcase.exist", errorList.toArray(new String[errorList.size()]));
					return ;
				}
				int row = testCaseTable.getSelectedRow();
				TestCase testCase = testCaseTableModel.getTestCase(row);
				
				Category c = new Category();
				c.setCategory((String)categoryComboBox.getSelectedItem());
				c.setId(categoryTableModel.getMap().get(categoryComboBox.getSelectedItem()));
				c.setCategory((String) categoryComboBox.getSelectedItem());
				testCase.setCategory(c);
				if (xmlTestFileComboBox.getSelectedIndex() != 0) {
					XMLFile x = new XMLFile();
					x.setId(xmlTestFileTableModel.getMap().get(xmlTestFileComboBox.getSelectedItem()));
					x.setXmlFile((String) xmlTestFileComboBox.getSelectedItem());
					testCase.setXmlFile(x);
				}
				if (testClassComboBox.getSelectedIndex() != 0) {
					TestClass tc = new TestClass();
					tc.setId(testClassTableModel.getMap().get(testClassComboBox.getSelectedItem()));
					tc.setTestClass((String) testClassComboBox.getSelectedItem());
					testCase.setTestClass(tc);
				}
				testCase.setMethod(methodTextField.getText().trim());
				testCase.setDescription(descriptionTextArea.getText().trim());
				testCase.setAction(actionTextArea.getText().trim());
				State s = new State();
				s.setId(testCaseTableModel.getStatesMap().get(stateComboBox.getSelectedItem()));
				s.setState((String) stateComboBox.getSelectedItem());
				testCase.setState(s);
				Version v = new Version();
				v.setId(versionTableModel.getMap().get(versionComboBox.getSelectedItem()));
				v.setVersion((String) versionComboBox.getSelectedItem());
				testCase.setVersion(v);
				testCase.setLastModifiedVersion(v);
				testCase.setUserModifier(user);
				Criticality cr = new Criticality();
				cr.setId(testCaseTableModel.getCriticitiesMap().get(criticalityComboBox.getSelectedItem()));
				cr.setCriticality((String) criticalityComboBox.getSelectedItem());
				testCase.setCriticality(cr);

				try {
					if (!testCaseTableModel.updateTestCase(testCase)) {
						displayErrorMessage(ladderFrame, "insertion.test_case");
						return ;
					}
					lastModifiedVersionComboBox.setSelectedIndex(versionComboBox.getSelectedIndex());
				} catch (LadderException e1) {
					displayErrorMessage(ladderFrame, e1.getMessage());
				}
			}
		});
		modify.setEnabled(false);
		
		search.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				TestCase tc = new TestCase();
				if (categoryTableModel.getMap().get(categoryComboBox.getSelectedItem()) != null) {
					Category c = new Category();
					c.setId(categoryTableModel.getMap().get(categoryComboBox.getSelectedItem()));
					tc.setCategory(c);
				}
				if (xmlTestFileTableModel.getMap().get(xmlTestFileComboBox.getSelectedItem()) != null) {
					XMLFile x = new XMLFile();
					x.setId(xmlTestFileTableModel.getMap().get(xmlTestFileComboBox.getSelectedItem()));
					tc.setXmlFile(x);
				}
				if (testClassTableModel.getMap().get(testClassComboBox.getSelectedItem()) != null) {
					TestClass t = new TestClass();
					t.setId(testClassTableModel.getMap().get(testClassComboBox.getSelectedItem()));
					tc.setTestClass(t);
				}
				if (testCaseTableModel.getStatesMap().get(stateComboBox.getSelectedItem()) != null) {
					State s = new State();
					s.setId(testCaseTableModel.getStatesMap().get(stateComboBox.getSelectedItem()));
					tc.setState(s);
				}
				if (versionTableModel.getMap().get(versionComboBox.getSelectedItem()) != null) {
					Version v = new Version();
					v.setId(versionTableModel.getMap().get(versionComboBox.getSelectedItem()));
					tc.setVersion(v);
				}
				if (versionTableModel.getMap().get(lastModifiedVersionComboBox.getSelectedItem()) != null) {
					Version v = new Version();
					v.setId(versionTableModel.getMap().get(lastModifiedVersionComboBox.getSelectedItem()));
					tc.setLastModifiedVersion(v);
				}
				if (testCaseTableModel.getCriticitiesMap().get(criticalityComboBox.getSelectedItem()) != null) {
					Criticality c = new Criticality();
					c.setId(testCaseTableModel.getCriticitiesMap().get(criticalityComboBox.getSelectedItem()));
					tc.setCriticality(c);
				}
				if (userModel.getMap().get(userCreatorComboBox.getSelectedItem()) != null) {
					User u = new User();
					u.setId(userModel.getMap().get(userCreatorComboBox.getSelectedItem()));
					tc.setUserCreator(u);
				}
				if (userModel.getMap().get(userModifierComboBox.getSelectedItem()) != null) {
					User u = new User();
					u.setId(userModel.getMap().get(userModifierComboBox.getSelectedItem()));
					tc.setUserModifier(u);
				}
				tc.setMethod(methodTextField.getText().trim());
				tc.setDescription(descriptionTextArea.getText().trim());
				tc.setAction(actionTextArea.getText().trim());
				testCaseTableModel.search(tc, creationBeginDateChooser.getCalendar(), creationEndDateChooser.getCalendar(), modificationBeginDateChooser.getCalendar(), modificationEndDateChooser.getCalendar());
			}
		});
		
		clean.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				categoryComboBox.setSelectedIndex(0);
				xmlTestFileComboBox.setSelectedIndex(0);
				testClassComboBox.setSelectedIndex(0);
				methodTextField.setText("");
				descriptionTextArea.setText("");
				actionTextArea.setText("");
				stateComboBox.setSelectedIndex(0);
				versionComboBox.setSelectedIndex(0);
				lastModifiedVersionComboBox.setSelectedIndex(0);
				modify.setEnabled(false);
				criticalityComboBox.setSelectedIndex(0);
				userCreatorComboBox.setSelectedIndex(0);
				userModifierComboBox.setSelectedIndex(0);
				creationBeginDateChooser.setCalendar(null);
				creationEndDateChooser.setCalendar(null);
				modificationBeginDateChooser.setCalendar(null);
				modificationEndDateChooser.setCalendar(null);
			}
		});
		
		duplicateSelectedTestCase.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!testCaseTableModel.canDuplicate()) {
					displayErrorMessage(ladderFrame, "frame.tab.testfile.execution.no_selection");
					return ;
				}
				boolean isVersionName = false;
				String version = null;
				do {
					isVersionName = false;
					version = (String) JOptionPane.showInputDialog(ladderFrame, getResourceBundle().getString("frame.tab.testfile.insert.version"), SOFTWARE + " " + VERSION, JOptionPane.PLAIN_MESSAGE, null, null, "");
					if (version.isEmpty()) {
						displayErrorMessage(ladderFrame, "frame.tab.testfile.execution.version_empty");
						isVersionName = true;
					}
				} while(isVersionName);
				if (!versionTableModel.addVersion(version)) {
					return;
				}
				testCaseTableModel.duplicateTestCase(version, user);
			}
		});
		
		createCampaignSelectedTestCase.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean isCampaigneName = false;
				String campaign = null;
				do {
					isCampaigneName = false;
					campaign = (String) JOptionPane.showInputDialog(ladderFrame, getResourceBundle().getString("frame.tab.testfile.insert.campaign"), SOFTWARE + " " + VERSION, JOptionPane.PLAIN_MESSAGE, null, null, "");
					if (campaign.isEmpty()) {
						displayErrorMessage(ladderFrame, "frame.tab.testfile.execution.campaign_empty");
						isCampaigneName = true;
					}
				} while(isCampaigneName);
				if (!campaignModel.addCampaign(campaign, user)) {
					return;
				}
				long idCampaign = campaignModel.getMap().get(campaign);
				Campaign c = new Campaign();
				c.setCampaign(campaign);
				c.setId(idCampaign);
				c.setExecuted(false);
				c.setUser(user);
				testCaseTableModel.createTestCampaign(c, user);
			}
		});
		
		headMenu.add(insert);
		headMenu.add(modify);
		headMenu.add(search);
		headMenu.add(clean);
		headMenu.add(duplicateSelectedTestCase);
		headMenu.add(createCampaignSelectedTestCase);
		
		head.setBorder(BorderFactory.createTitledBorder(getResourceBundle().getString("frame.test_case.buttongroup.title")));
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1f;
		gbc.weighty = 0f;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.category")), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		head.add(categoryComboBox, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.xmlfile")), gbc);
		gbc.gridx = 3;
		gbc.gridy = 0;
		head.add(xmlTestFileComboBox, gbc);
		gbc.gridx = 4;
		gbc.gridy = 0;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.testclass")), gbc);
		gbc.gridx = 5;
		gbc.gridy = 0;
		head.add(testClassComboBox, gbc);
		gbc.gridx = 6;
		gbc.gridy = 0;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.method")), gbc);
		gbc.gridx = 7;
		gbc.gridy = 0;
		head.add(methodTextField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.description")), gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 4;
		gbc.gridwidth = 7;
		gbc.weighty = 1;
		head.add(descriptionScroll, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 5;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.testcase")), gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.gridheight = 4;
		gbc.gridwidth = 7;
		gbc.weighty = 1;
		head.add(actionScroll, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.weighty = 0;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.state")), gbc);
		gbc.gridx = 1;
		gbc.gridy = 9;
		head.add(stateComboBox, gbc);
		gbc.gridx = 2;
		gbc.gridy = 9;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.criticality")), gbc);
		gbc.gridx = 3;
		gbc.gridy = 9;
		head.add(criticalityComboBox, gbc);
		gbc.gridx = 4;
		gbc.gridy = 9;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.version")), gbc);
		gbc.gridx = 5;
		gbc.gridy = 9;
		head.add(versionComboBox, gbc);
		gbc.gridx = 6;
		gbc.gridy = 9;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.last_modified_version")), gbc);
		gbc.gridx = 7;
		gbc.gridy = 9;
		head.add(lastModifiedVersionComboBox, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 10;
		gbc.gridwidth = 8;
		gbc.insets = new Insets(10, 10, 10, 10);
		head.add(userDateMenu, gbc);
		
		gbc.gridy = 11;
		gbc.insets = new Insets(10, 10, 10, 10);
		head.add(headMenu, gbc);
		
		testCaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		testCaseTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			private int previousRow = -1;
			@SuppressWarnings("cast")
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row = testCaseTable.getSelectedRow();
				if (row != previousRow) {
					int col = testCaseTable.getSelectedColumn();
					if (col == 0) {
						return;
					}
					if (row > testCaseTableModel.getRowCount() -1) {
						row = testCaseTableModel.getRowCount() -1;
					}
					categoryComboBox.setSelectedItem((String)testCaseTableModel.getValueAt(row, 1));
					xmlTestFileComboBox.setSelectedItem((String) testCaseTableModel.getValueAt(row, 2));
					testClassComboBox.setSelectedItem((String)testCaseTableModel.getValueAt(row, 3));
					methodTextField.setText((String)testCaseTableModel.getValueAt(row, 4));
					descriptionTextArea.setText((String) testCaseTableModel.getValueAt(row, 5));
					actionTextArea.setText((String) testCaseTableModel.getValueAt(row, 6));
					stateComboBox.setSelectedItem((String)testCaseTableModel.getValueAt(row, 7));
					criticalityComboBox.setSelectedItem((String) testCaseTableModel.getValueAt(row, 8));
					versionComboBox.setSelectedItem((String)testCaseTableModel.getValueAt(row, 9));
					lastModifiedVersionComboBox.setSelectedItem((String)testCaseTableModel.getValueAt(row, 10));
					userCreatorComboBox.setSelectedItem((String) testCaseTableModel.getValueAt(row, 11));
					userModifierComboBox.setSelectedItem((String) testCaseTableModel.getValueAt(row, 13));
					modify.setEnabled(true);
					previousRow = row;
				}
			}
		});
		JScrollPane scrollPane = new JScrollPane(testCaseTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		scrollPane.setBorder(BorderFactory.createTitledBorder(getResourceBundle().getString("frame.test_case.test.title")));

		panel.add(head, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}
	
	private JPanel createTestResultPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel head = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		final JComboBox categoryComboBox = createComboBox(categoryTableModel);
		final JComboBox xmlTestFileComboBox = createComboBox(xmlTestFileTableModel);
		final JComboBox testClassComboBox = createComboBox(testClassTableModel);
		final JTextField methodTextField = new JTextField();
		methodTextField.addKeyListener(getKeyListener(methodTextField, METHOD_MAX_LENGTH));
		
		final JTextArea descriptionTextArea = new JTextArea();
		descriptionTextArea.addKeyListener(getKeyListener(descriptionTextArea, DESCRIPTION_MAX_LENGTH));
		descriptionTextArea.setEditable(false);
		descriptionTextArea.setRows(4);
		descriptionTextArea.setLineWrap(true);
		JScrollPane descriptionScroll = new JScrollPane(descriptionTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		final JTextArea actionTextArea = new JTextArea();
		actionTextArea.addKeyListener(getKeyListener(actionTextArea, ACTION_MAX_LENGTH));
		actionTextArea.setEditable(false);
		actionTextArea.setRows(4);
		actionTextArea.setLineWrap(true);
		JScrollPane actionScroll = new JScrollPane(actionTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		final JComboBox resultComboBox = new JComboBox();
		resultComboBox.addItem("");
		for (String item : testResultModel.getTestResults().keySet()) {
			resultComboBox.addItem(item);
		}
		
		final JComboBox typeTestComboBox = new JComboBox();
		typeTestComboBox.addItem("");
		for (String item : testCaseResultTableModel.getTestTypesMap().keySet()) {
			typeTestComboBox.addItem(item);
		}
		
		final JComboBox softwareVersionComboBox = createComboBox(versionTableModel);
		
		final JComboBox stateComboBox = new JComboBox();
		stateComboBox.addItem("");
		for (String item : testCaseTableModel.getStatesMap().keySet()) {
			stateComboBox.addItem(item);
		}

		final JComboBox criticalityComboBox = new JComboBox();
		criticalityComboBox.addItem("");
		for (String item : testCaseTableModel.getCriticitiesMap().keySet()) {
			criticalityComboBox.addItem(item);
		}
		
		final JComboBox versionComboBox = createComboBox(versionTableModel);
		final JComboBox lastModifiedVersionComboBox = createComboBox(versionTableModel);
		
		
		JPanel userDateMenu = new JPanel();
		BoxLayout box = new BoxLayout(userDateMenu, BoxLayout.X_AXIS);
		userDateMenu.setLayout(box);
		final JComboBox userCreatorComboBox = createComboBox(userModel);
		final JComboBox userModifierComboBox = createComboBox(userModel);
	
		JLabel creationBeginDateLabel = new JLabel("Creation date between");
		final JDateChooser creationBeginDateChooser = new JDateChooser();

		JLabel creationEndDateLabel = new JLabel("and");
		final JDateChooser creationEndDateChooser = new JDateChooser();

		JLabel modificationBeginDateLabel = new JLabel("Modification date between");
		final JDateChooser modificationBeginDateChooser = new JDateChooser();

		JLabel modificationEndDateLabel = new JLabel("and");
		final JDateChooser modificationEndDateChooser = new JDateChooser();
		
		userDateMenu.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.user_creator")));
		userDateMenu.add(userCreatorComboBox);
		userDateMenu.add(creationBeginDateLabel);
		userDateMenu.add(creationBeginDateChooser);
		userDateMenu.add(creationEndDateLabel);
		userDateMenu.add(creationEndDateChooser);
		userDateMenu.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.user_modifier")));
		userDateMenu.add(userModifierComboBox);
		userDateMenu.add(modificationBeginDateLabel);
		userDateMenu.add(modificationBeginDateChooser);
		userDateMenu.add(modificationEndDateLabel);
		userDateMenu.add(modificationEndDateChooser);
		userDateMenu.setBorder(BorderFactory.createTitledBorder(getResourceBundle().getString("frame.tab.testresult.title.user_testcase_modifier")));
		
		JPanel userTestDateMenu = new JPanel();
		BoxLayout box2 = new BoxLayout(userTestDateMenu, BoxLayout.X_AXIS);
		userTestDateMenu.setLayout(box2);
		final JComboBox userTestCreatorComboBox = createComboBox(userModel);
		final JComboBox userTestModifierComboBox = createComboBox(userModel);
	
		JLabel creationTestBeginDateLabel = new JLabel("Creation date between");
		final JDateChooser creationTestBeginDateChooser = new JDateChooser();

		JLabel creationTestEndDateLabel = new JLabel("and");
		final JDateChooser creationTestEndDateChooser = new JDateChooser();

		JLabel modificationTestBeginDateLabel = new JLabel("Modification date between");
		final JDateChooser modificationTestBeginDateChooser = new JDateChooser();

		JLabel modificationTestEndDateLabel = new JLabel("and");
		final JDateChooser modificationTestEndDateChooser = new JDateChooser();
		
		userTestDateMenu.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.user_test_creator")));
		userTestDateMenu.add(userTestCreatorComboBox);
		userTestDateMenu.add(creationTestBeginDateLabel);
		userTestDateMenu.add(creationTestBeginDateChooser);
		userTestDateMenu.add(creationTestEndDateLabel);
		userTestDateMenu.add(creationTestEndDateChooser);
		userTestDateMenu.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.user_test_modifier")));
		userTestDateMenu.add(userTestModifierComboBox);
		userTestDateMenu.add(modificationTestBeginDateLabel);
		userTestDateMenu.add(modificationTestBeginDateChooser);
		userTestDateMenu.add(modificationTestEndDateLabel);
		userTestDateMenu.add(modificationTestEndDateChooser);
		userTestDateMenu.setBorder(BorderFactory.createTitledBorder(getResourceBundle().getString("frame.tab.testresult.title.user_test_modifier")));
		
		JPanel headMenu = new JPanel(new GridLayout(1, 4));
		final JButton search = new JButton(getResourceBundle().getString("frame.button.search"));
		final JButton clean = new JButton(getResourceBundle().getString("frame.button.clean"));
		
		search.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				TestCase tc = new TestCase();
				Test test = new Test();
				if (categoryComboBox.getSelectedIndex() > 0) {
					Category c = new Category();
					c.setId(categoryTableModel.getMap().get(categoryComboBox.getSelectedItem()));
					tc.setCategory(c);
				}
				if (xmlTestFileComboBox.getSelectedIndex() > 0) {
					XMLFile x = new XMLFile();
					x.setId(xmlTestFileTableModel.getMap().get(xmlTestFileComboBox.getSelectedItem()));
					tc.setXmlFile(x);
				}
				if (testClassComboBox.getSelectedIndex() > 0) {
					TestClass t = new TestClass();
					t.setId(testClassTableModel.getMap().get(testClassComboBox.getSelectedItem()));
					tc.setTestClass(t);
				}
				tc.setMethod(methodTextField.getText().trim());
				tc.setDescription(descriptionTextArea.getText().trim());
				tc.setAction(actionTextArea.getText().trim());
				if (stateComboBox.getSelectedIndex() > 0) {
					State s = new State();
					s.setId(testCaseTableModel.getStatesMap().get(stateComboBox.getSelectedItem()));
					tc.setState(s);
				}
				if (versionComboBox.getSelectedIndex() > 0) {
					Version v = new Version();
					v.setId(versionTableModel.getMap().get(versionComboBox.getSelectedItem()));
					tc.setVersion(v);
				}
				if (lastModifiedVersionComboBox.getSelectedIndex() > 0) {
					Version v = new Version();
					v.setId(versionTableModel.getMap().get(lastModifiedVersionComboBox.getSelectedItem()));
					tc.setLastModifiedVersion(v);
				}
				if (resultComboBox.getSelectedIndex() > 0) {
					Result r = new Result();
					r.setId(testResultModel.getTestResults().get(resultComboBox.getSelectedItem()));
					test.setResult(r);
				}
				if (criticalityComboBox.getSelectedIndex() > 0) {
					Criticality c = new Criticality();
					c.setId(testCaseTableModel.getCriticitiesMap().get(criticalityComboBox.getSelectedItem()));
					tc.setCriticality(c);
				}
				if (softwareVersionComboBox.getSelectedIndex() > 0) {
					Version v = new Version();
					v.setId(testResultModel.getTestResults().get(softwareVersionComboBox.getSelectedItem()));
					Campaign c = new Campaign();
					c.setExecutedVersion(v);
					test.setCampaign(c);
				}
				if (typeTestComboBox.getSelectedIndex() > 0) {
					TestType t = new TestType();
					t.setId(testCaseResultTableModel.getTestTypesMap().get(typeTestComboBox.getSelectedItem()));
					test.setTestType(t);
				}
				if (userCreatorComboBox.getSelectedIndex() > 0) {
					User u = new User();
					u.setId(userModel.getMap().get(userCreatorComboBox.getSelectedItem()));
					tc.setUserCreator(u);
				}
				if (userModifierComboBox.getSelectedIndex() > 0) {
					User u = new User();
					u.setId(userModel.getMap().get(userModifierComboBox.getSelectedItem()));
					tc.setUserModifier(u);
				}
				if (userTestCreatorComboBox.getSelectedIndex() > 0) {
					User u = new User();
					u.setId(userModel.getMap().get(userTestCreatorComboBox.getSelectedItem()));
					test.setUserCreator(u);
				}
				if (userTestModifierComboBox.getSelectedIndex() > 0) {
					User u = new User();
					u.setId(userModel.getMap().get(userTestModifierComboBox.getSelectedItem()));
					test.setUserExecutor(u);
				}
				testCaseResultTableModel.search(tc, test, creationBeginDateChooser.getCalendar(), creationEndDateChooser.getCalendar(),
						modificationBeginDateChooser.getCalendar(), modificationEndDateChooser.getCalendar(),
						creationTestBeginDateChooser.getCalendar(), creationTestEndDateChooser.getCalendar(),
						modificationTestBeginDateChooser.getCalendar(), modificationTestEndDateChooser.getCalendar());
					    
				
				testCaseResultTableModel.search(categoryTableModel.getMap().get(categoryComboBox.getSelectedItem()) == null ? -1 : categoryTableModel.getMap().get(categoryComboBox.getSelectedItem())
						, xmlTestFileTableModel.getMap().get(xmlTestFileComboBox.getSelectedItem()) == null ? -1 : xmlTestFileTableModel.getMap().get(xmlTestFileComboBox.getSelectedItem())
						, testClassTableModel.getMap().get(testClassComboBox.getSelectedItem()) == null ? -1 : testClassTableModel.getMap().get(testClassComboBox.getSelectedItem())
						, methodTextField.getText().trim()
//							, descriptionTextArea.getText().trim()
//							, actionTextArea.getText().trim()
						, testCaseTableModel.getStatesMap().get(stateComboBox.getSelectedItem()) == null ? -1 : testCaseTableModel.getStatesMap().get(stateComboBox.getSelectedItem())
						, versionTableModel.getMap().get(versionComboBox.getSelectedItem()) == null ? -1 : versionTableModel.getMap().get(versionComboBox.getSelectedItem())
						, versionTableModel.getMap().get(versionComboBox.getSelectedItem()) == null ? -1 : versionTableModel.getMap().get(versionComboBox.getSelectedItem())
					    , testResultModel.getTestResults().get(resultComboBox.getSelectedItem()) == null ? -1 : testResultModel.getTestResults().get(resultComboBox.getSelectedItem())
					    , versionTableModel.getMap().get(softwareVersionComboBox.getSelectedItem()) == null ? -1 : versionTableModel.getMap().get(softwareVersionComboBox.getSelectedItem())
					    , testCaseResultTableModel.getTestTypesMap().get(typeTestComboBox.getSelectedItem()) == null ? -1 : testCaseResultTableModel.getTestTypesMap().get(typeTestComboBox.getSelectedItem())
					    ,	-1	);
			}
		});
		
		clean.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				categoryComboBox.setSelectedIndex(0);
				xmlTestFileComboBox.setSelectedIndex(0);
				testClassComboBox.setSelectedIndex(0);
				methodTextField.setText("");
				descriptionTextArea.setText("");
				actionTextArea.setText("");
				stateComboBox.setSelectedIndex(0);
				versionComboBox.setSelectedIndex(0);
				resultComboBox.setSelectedIndex(0);
				criticalityComboBox.setSelectedIndex(0);
				userCreatorComboBox.setSelectedIndex(0);
				userModifierComboBox.setSelectedIndex(0);
				userTestCreatorComboBox.setSelectedIndex(0);
				userTestModifierComboBox.setSelectedIndex(0);
				creationBeginDateChooser.setCalendar(null);
				modificationBeginDateChooser.setCalendar(null);
				creationEndDateChooser.setCalendar(null);
				modificationEndDateChooser.setCalendar(null);
				creationTestBeginDateChooser.setCalendar(null);
				creationTestEndDateChooser.setCalendar(null);
				modificationTestBeginDateChooser.setCalendar(null);
				modificationTestEndDateChooser.setCalendar(null);
			}
		});
		
		headMenu.add(search);
		headMenu.add(clean);
		
		head.setBorder(BorderFactory.createTitledBorder(getResourceBundle().getString("frame.test_case.buttongroup.title")));
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1f;
		gbc.weighty = 0f;
		gbc.gridx = 0;
		gbc.gridy = 0;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.category")), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		head.add(categoryComboBox, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.xmlfile")), gbc);
		gbc.gridx = 3;
		gbc.gridy = 0;
		head.add(xmlTestFileComboBox, gbc);
		gbc.gridx = 4;
		gbc.gridy = 0;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.testclass")), gbc);
		gbc.gridx = 5;
		gbc.gridy = 0;
		head.add(testClassComboBox, gbc);
		gbc.gridx = 6;
		gbc.gridy = 0;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.method")), gbc);
		gbc.gridx = 7;
		gbc.gridy = 0;
		head.add(methodTextField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.description")), gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 4;
		gbc.gridwidth = 7;
		gbc.weighty = 1;
		head.add(descriptionScroll, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 5;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testcase.combobox.testcase")), gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.gridheight = 4;
		gbc.weighty = 1;
		gbc.gridwidth = 7;
		head.add(actionScroll, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.weighty = 0;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testresult.combobox.result")), gbc);
		gbc.gridx = 1;
		gbc.gridy = 9;
		head.add(resultComboBox, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
		gbc.gridx = 2;
		gbc.gridy = 9;
		gbc.weighty = 0;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testresult.combobox.typetest")), gbc);
		gbc.gridx = 3;
		gbc.gridy = 9;
		head.add(typeTestComboBox, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
		gbc.gridx = 4;
		gbc.gridy = 9;
		gbc.weighty = 0;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testresult.combobox.softwareversion")), gbc);
		gbc.gridx = 5;
		gbc.gridy = 9;
		head.add(softwareVersionComboBox, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
		gbc.gridx = 6;
		gbc.gridy = 9;
		gbc.weighty = 0;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testresult.combobox.state")), gbc);
		gbc.gridx = 7;
		gbc.gridy = 9;
		head.add(stateComboBox, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 10;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testresult.combobox.criticality")), gbc);
		gbc.gridx = 1;
		gbc.gridy = 10;
		head.add(criticalityComboBox, gbc);
		gbc.gridx = 2;
		gbc.gridy = 10;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testresult.combobox.version")), gbc);
		gbc.gridx = 3;
		gbc.gridy = 10;
		head.add(versionComboBox, gbc);
		gbc.gridx = 4;
		gbc.gridy = 10;
		head.add(new JLabel(getResourceBundle().getString("frame.tab.testresult.combobox.last_modified_version")), gbc);
		gbc.gridx = 5;
		gbc.gridy = 10;
		head.add(lastModifiedVersionComboBox, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 11;
		gbc.gridwidth = 8;
		head.add(userDateMenu, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 12;
		head.add(userTestDateMenu, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 13;gbc.insets = new Insets(10, 10, 10, 10);
		head.add(headMenu, gbc);
		
		final JTable testCaseResultTable = new JTable(testCaseResultTableModel);
		testCaseResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		testCaseResultTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			private int previousRow = -1;
			@SuppressWarnings("cast")
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row = testCaseResultTable.getSelectedRow();
				if (row != previousRow) {
					categoryComboBox.setSelectedItem((String)testCaseResultTableModel.getValueAt(row, 0));
					xmlTestFileComboBox.setSelectedItem((String) testCaseResultTableModel.getValueAt(row, 1));
					testClassComboBox.setSelectedItem((String)testCaseResultTableModel.getValueAt(row, 2));
					methodTextField.setText((String)testCaseResultTableModel.getValueAt(row, 3));
	//				descriptionTextArea.setText((String) testCaseTableModel.getValueAt(row, 4));
	//				actionTextArea.setText((String) testCaseTableModel.getValueAt(row, 5));
					typeTestComboBox.setSelectedItem((String) testCaseResultTableModel.getValueAt(row, 6));
					stateComboBox.setSelectedItem((String)testCaseResultTableModel.getValueAt(row, 8));
					versionComboBox.setSelectedItem((String)testCaseResultTableModel.getValueAt(row, 9));
					lastModifiedVersionComboBox.setSelectedItem((String) testCaseResultTableModel.getValueAt(row, 10));
					resultComboBox.setSelectedItem((String) testCaseResultTableModel.getValueAt(row, 11));
					softwareVersionComboBox.setSelectedItem((String)testCaseResultTableModel.getValueAt(row, 13));
					previousRow = row;
				}
			}
		});
		JScrollPane scrollPane = new JScrollPane(testCaseResultTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		scrollPane.setBorder(BorderFactory.createTitledBorder(getResourceBundle().getString("frame.test_case.test.title")));

		
		panel.add(head, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}
	
	private JComboBox createComboBox(final AbstractLadderTableModel model) {
		final JComboBox combo = new JComboBox();
		insertItem(combo, model.getMap().keySet());
		model.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				combo.removeAllItems();
				insertItem(combo, model.getMap().keySet());
			}
		});
		return combo;
	}
	
	private void insertItem(JComboBox combo, Set<String> set) {
		combo.addItem("");
		for(String item : set) {
			combo.addItem(item);
		}
	}
	
	private void centeredWindow(JWindow window) {
		double l = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double h = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		window.setLocation((int) (l / 2 - window.getWidth() / 2), (int) (h / 2 - window.getHeight() / 2));
	}
	
	private JPanel createUsersPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel head = new JPanel(new GridLayout(2, 2));
		
		JButton addUser = new JButton("add");
		addUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new RightsTestLadderFrame(ladderFrame, getResourceBundle(), userModel);
			}
		});
		
		JButton removeUser = new JButton("remove");
		JButton modify = new JButton("Modif");
		
		JButton search = new JButton("Search");
		head.add(addUser);
		head.add(removeUser);
		head.add(modify);
		head.add(search);
		
		final JTable userTable = new JTable(userModel);
		userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		modify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = userTable.getSelectedRow();
				new RightsTestLadderFrame(ladderFrame, getResourceBundle(), index, userModel);
			}
		});
		
		removeUser.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = userTable.getSelectedRow();
				User u = userModel.getUser(index);
				int res = JOptionPane.showConfirmDialog(ladderFrame, getResourceBundle().getString("frame.tab.user.remove.confirmation") + " " + u.getLogin() + " ?", LadderFrame.SOFTWARE + " " + LadderFrame.VERSION, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (res == JOptionPane.YES_OPTION) {
					try {
						userModel.removeUser(index);
					} catch (LadderException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		JScrollPane scroll = new JScrollPane(userTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(head, BorderLayout.NORTH);
		panel.add(scroll, BorderLayout.CENTER);
		return panel;
	}
	
	private JPanel createCampaignPanel() {
		JPanel campaign = new JPanel(new GridLayout(1, 1));
		
		final JTable campaignTable = new JTable(campaignModel);
		final TestCaseResultCampaignAdapter tcrcModel = new TestCaseResultCampaignAdapter(testCaseResultTableModel, connection, testResultModel, categoryTableModel);
		campaignTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		campaignTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			private int previousRow = -1;
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row = campaignTable.getSelectedRow();
				if (row != previousRow) {
					long campaignId = campaignModel.getMap().get(campaignModel.getValueAt(row, 0));
					tcrcModel.getTestCaseByCampaignId(campaignId);
					previousRow = row;
				}
			}
		});
		
		JTable testResultTable = new JTable(tcrcModel);
		TableColumn tc = testResultTable.getColumnModel().getColumn(10);
		
		final JComboBox resultComboBox = new JComboBox();
		for (String item : testResultModel.getTestResults().keySet()) {
			resultComboBox.addItem(item);
		}
		tc.setCellEditor(new DefaultCellEditor(resultComboBox));
		
		JScrollPane campaignScroll = new JScrollPane(campaignTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane testResultScroll = new JScrollPane(testResultTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.add(campaignScroll);
		splitPane.add(testResultScroll);
		splitPane.setDividerLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.15));
		
		
		JPanel info = new JPanel();
		final JLabel executionStats = new JLabel(getExecutionStats(tcrcModel.getStats()));
		final JLabel typeStats = new JLabel(getTypeStats(tcrcModel.getStats()));
		final JPanel resultPerCriticalityStats = new JPanel();
		resultPerCriticalityStats.add(getResultPerCriticalityStats(tcrcModel.getStats()));
		final JPanel categoryCriticalityPerResultStats = new JPanel();
		categoryCriticalityPerResultStats.add(getCategoryCriticalityPerResultStats(tcrcModel.getStats()));
		
		tcrcModel.addUpdateStatsListener(new UpdateStatsListener() {
			
			@Override
			public void update() {
				executionStats.setText(getExecutionStats(tcrcModel.getStats()));
				typeStats.setText(getTypeStats(tcrcModel.getStats()));
				resultPerCriticalityStats.removeAll();
				resultPerCriticalityStats.add(getResultPerCriticalityStats(tcrcModel.getStats()));
				categoryCriticalityPerResultStats.removeAll();
				categoryCriticalityPerResultStats.add(getCategoryCriticalityPerResultStats(tcrcModel.getStats()));
				
			}
		});
		
		info.add(executionStats);
		info.add(typeStats);
		info.add(resultPerCriticalityStats);
		info.add(categoryCriticalityPerResultStats);
		JSplitPane pageSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		pageSplit.add(splitPane);
		pageSplit.add(info);
		pageSplit.setDividerLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.5));
		
		campaign.add(pageSplit);
		return campaign;
	}
	
	private String getExecutionStats(Stats stats) {
		int nbTest = stats.getNumberOfTestExecuted();
		StringBuilder statistic = new StringBuilder("Number of test : ");
		statistic.append(nbTest);
		for(String result : stats.getResultMap().keySet()) {
			int nb = stats.getResultMap().get(result);
			double percent = (nb * 100) / nbTest;
			statistic.append(result + ":" + nb + " (" + percent);
			statistic.append("%)   ");
		}
		return statistic.toString();
	}
	
	private String getTypeStats(Stats stats) {
		int nbTest = stats.getNumberOfTestExecuted();
		StringBuilder statistic = new StringBuilder();
		for(String type : stats.getTypeMap().keySet()) {
			int nb = stats.getTypeMap().get(type);
			double percent = (nb * 100) / nbTest;
			statistic.append(type + ":" + nb + " (" + percent);
			statistic.append("%)   ");
		}
		return statistic.toString();
	}
	
	private JPanel getResultPerCriticalityStats(Stats stats) {
		JPanel panel = new JPanel(new GridLayout(6, 6));
		int nbTest = stats.getNumberOfTestExecuted();
		panel.add(getStatsLabel(""));
		panel.add(getStatsLabel(TestResult.NOT_EXECUTED.toString()));
		panel.add(getStatsLabel(TestResult.PASSED.toString()));
		panel.add(getStatsLabel(TestResult.FAILED.toString()));
		panel.add(getStatsLabel(TestResult.SKIPPED.toString()));
		panel.add(getStatsLabel(getResourceBundle().getString("frame.campaign.stats.total")));

		panel.add(getStatsLabel(CriticalityEnum.LOW.toString()));
		Map<String, Integer> map = stats.getResultPerCriticalityMap().get(CriticalityEnum.LOW.toString());
		panel.add(getStatsLabel(Integer.toString(map.get(TestResult.NOT_EXECUTED.toString()))));
		
		panel.add(getStatsLabel(Integer.toString(map.get(TestResult.PASSED.toString()))));
		
		panel.add(getStatsLabel(Integer.toString(map.get(TestResult.FAILED.toString()))));
		
		panel.add(getStatsLabel(Integer.toString(map.get(TestResult.SKIPPED.toString()))));
		
		panel.add(getStatsLabel(Integer.toString(stats.getCriticalityMap().get(CriticalityEnum.LOW.toString()))));
		
		panel.add(getStatsLabel(CriticalityEnum.MEDIUM.toString()));
		Map<String, Integer> tmpMap = stats.getResultPerCriticalityMap().get(CriticalityEnum.MEDIUM.toString());
		panel.add(getStatsLabel(Integer.toString(tmpMap.get(TestResult.NOT_EXECUTED.toString()))));
		panel.add(getStatsLabel(Integer.toString(tmpMap.get(TestResult.PASSED.toString()))));
		panel.add(getStatsLabel(Integer.toString(tmpMap.get(TestResult.FAILED.toString()))));
		panel.add(getStatsLabel(Integer.toString(tmpMap.get(TestResult.SKIPPED.toString()))));
			
		panel.add(getStatsLabel(Integer.toString(stats.getCriticalityMap().get(CriticalityEnum.MEDIUM.toString()))));
		
		panel.add(getStatsLabel(CriticalityEnum.HIGH.toString()));
		Map<String, Integer> tmpMap2 = stats.getResultPerCriticalityMap().get(CriticalityEnum.HIGH.toString());
		panel.add(getStatsLabel(Integer.toString(tmpMap2.get(TestResult.NOT_EXECUTED.toString()))));
		panel.add(getStatsLabel(Integer.toString(tmpMap2.get(TestResult.PASSED.toString()))));
		panel.add(getStatsLabel(Integer.toString(tmpMap2.get(TestResult.FAILED.toString()))));
		panel.add(getStatsLabel(Integer.toString(tmpMap2.get(TestResult.SKIPPED.toString()))));
		panel.add(getStatsLabel(Integer.toString(stats.getCriticalityMap().get(CriticalityEnum.HIGH.toString()))));
		
		panel.add(getStatsLabel(CriticalityEnum.CRITICAL.toString()));
		Map<String, Integer> tmpMap3 = stats.getResultPerCriticalityMap().get(CriticalityEnum.CRITICAL.toString());
		panel.add(getStatsLabel(Integer.toString(tmpMap3.get(TestResult.NOT_EXECUTED.toString()))));
		panel.add(getStatsLabel(Integer.toString(tmpMap3.get(TestResult.PASSED.toString()))));
		panel.add(getStatsLabel(Integer.toString(tmpMap3.get(TestResult.FAILED.toString()))));
		panel.add(getStatsLabel(Integer.toString(tmpMap3.get(TestResult.SKIPPED.toString()))));
		panel.add(getStatsLabel(Integer.toString(stats.getCriticalityMap().get(CriticalityEnum.CRITICAL.toString()))));
		
		panel.add(getStatsLabel(getResourceBundle().getString("frame.campaign.stats.total")));
		
		panel.add(getStatsLabel(Integer.toString(stats.getResultMap().get(TestResult.NOT_EXECUTED.toString()))));
		panel.add(getStatsLabel(Integer.toString(stats.getResultMap().get(TestResult.PASSED.toString()))));
		panel.add(getStatsLabel(Integer.toString(stats.getResultMap().get(TestResult.FAILED.toString()))));
		panel.add(getStatsLabel(stats.getResultMap().get(TestResult.SKIPPED.toString())));
		
		panel.add(getStatsLabel());
		return panel;
	}
	
	private JPanel getCategoryCriticalityPerResultStats(Stats stats) {
		JPanel panel = new JPanel(new GridLayout(stats.getCategoryPerCriticalityResult().size() + 2, 6));
		int nbTest = stats.getNumberOfTestExecuted();
		panel.add(getStatsLabel(""));
		panel.add(getStatsLabel(TestResult.NOT_EXECUTED.toString()));
		panel.add(getStatsLabel(TestResult.PASSED.toString()));
		panel.add(getStatsLabel(TestResult.FAILED.toString()));
		panel.add(getStatsLabel(TestResult.SKIPPED.toString()));
		panel.add(getStatsLabel(getResourceBundle().getString("frame.campaign.stats.total")));
		
		for (String category : stats.getCategoryPerCriticalityResult().keySet()) {
			panel.add(new TitleColumnArray(category, getCriticalityList()));
			Map<String, Map<String, Integer>> map = stats.getCategoryPerCriticalityResult().get(category);
			Map<String, Integer> resultMap = new HashMap<String, Integer>();
			for (CriticalityEnum c : CriticalityEnum.values()) {
				resultMap.put(c.toString(), 0);
			}
			TestResult testResult = TestResult.NOT_EXECUTED;
			addStatsCriticalityPanel(panel, testResult, map, resultMap);
			testResult = TestResult.PASSED;
			addStatsCriticalityPanel(panel, testResult, map, resultMap);
			testResult = TestResult.FAILED;
			addStatsCriticalityPanel(panel, testResult, map, resultMap);
			testResult = TestResult.SKIPPED;
			addStatsCriticalityPanel(panel, testResult, map, resultMap);
			
			panel.add(new MultipleDataColumnArray(resultMap));
		}
		
		panel.add(getStatsLabel(getResourceBundle().getString("frame.campaign.stats.total")));
		
		panel.add(getStatsLabel(Integer.toString(stats.getResultMap().get(TestResult.NOT_EXECUTED.toString()))));
		panel.add(getStatsLabel(Integer.toString(stats.getResultMap().get(TestResult.PASSED.toString()))));
		panel.add(getStatsLabel(Integer.toString(stats.getResultMap().get(TestResult.FAILED.toString()))));
		panel.add(getStatsLabel(stats.getResultMap().get(TestResult.SKIPPED.toString())));
		
		panel.add(getStatsLabel(nbTest));
		
		return panel;
	}
	
	private void addStatsCriticalityPanel(JPanel panel, TestResult testResult, Map<String, Map<String, Integer>> map, Map<String, Integer> resultMap) {
		panel.add(new MultipleDataColumnArray(map.get(testResult.toString())));
		for (String s : map.get(testResult.toString()).keySet()) {
			resultMap.put(s, map.get(testResult.toString()).get(s) + resultMap.get(s));
		}
	}
	
	private List<String> getCriticalityList() {
		List<String> list = new ArrayList<String>();
		list.add(CriticalityEnum.LOW.toString());
		list.add(CriticalityEnum.MEDIUM.toString());
		list.add(CriticalityEnum.HIGH.toString());
		list.add(CriticalityEnum.CRITICAL.toString());
		return list;
	}
	
	private JLabel getStatsLabel() {
		return getStatsLabel("");
	}
	
	private JLabel getStatsLabel(int value) {
		return getStatsLabel(Integer.toString(value));
	}
	
	private JLabel getStatsLabel(String text) {
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(JLabel.RIGHT);
		label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		return label;
	}
	
	private JMenuBar createJMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createHelpMenu());
		return menuBar;
	}
	
	@SuppressWarnings("serial")
	private JMenu createHelpMenu() {
		JMenu helpMenu = new JMenu(getResourceBundle().getString("frame.menu.help"));
		
		JMenuItem about = new JMenuItem(getResourceBundle().getString("frame.menu.help.about"));
		about.setMnemonic('A');
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//try {
					//ladderFrame.setEnabled(false);
					//ICTouchAboutFrame.getNewInstance(icTouchModel, ICTouchFrame.this);
				//} catch (LadderException e1) {
				//	displayErrorMessage(ladderFrame, e1.getMessage());
				//}
			}
		});
		Action helpButton = new AbstractAction(getResourceBundle().getString("frame.menu.help.help"), new ImageIcon(getClass().getResource("help.png"))) {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					displayHelp();
				} catch (LadderException e1) {
					displayErrorMessage(ladderFrame, e1.getMessage());
				}
			}
		};
		helpButton.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK));
		helpButton.putValue(Action.MNEMONIC_KEY, Integer.valueOf('H'));
		helpButton.putValue(Action.SHORT_DESCRIPTION, getResourceBundle().getString("frame.menu.help.help.tiptool"));

		helpMenu.add(about);
		helpMenu.addSeparator();
		helpMenu.add(helpButton);
		return helpMenu;
	}

	private void displayHelp() throws LadderException {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
				File help = new File(System.getProperty("user.home") + "/.TestLadder/help.pdf");
				try {
					File inFile = new File(LadderFrame.class.getResource("help.pdf").getFile());
					if (!help.exists() || help.lastModified() != inFile.lastModified()) {
						copyHelpFile(help);
						if (!help.exists()) {
							//throw ICTouchException.createInstance(ICTouchExceptionID.HELP_FILE);
						}
						help.setLastModified(inFile.lastModified());
					}
					desktop.open(help);
				} catch (IOException e) {
					//throw ICTouchException.createInstance(ICTouchExceptionID.HELP_FILE_OPENING, e);
				}
			} else {
				//throw ICTouchException.createInstance(ICTouchExceptionID.HELP_FILE_OPENING_READER_ERROR);
			}
		} else {
			//throw ICTouchException.createInstance(ICTouchExceptionID.HELP_FILE_OPENING_NOT_SUPPORTED);
		}
	}
	
	private void copyHelpFile(File file) throws IOException {
		final int bufferSize = 4096;
		InputStream in = LadderFrame.class.getResourceAsStream("help.pdf");
		FileOutputStream out = new FileOutputStream(file);
		byte[] buffer = new byte[bufferSize];
		while (in.read(buffer) != -1) {
			out.write(buffer);
		}
		in.close();
		out.close();
	}
}
