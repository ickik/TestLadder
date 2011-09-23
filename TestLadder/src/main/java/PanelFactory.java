import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;


public class PanelFactory {

	private static final Map<Panel, String> panelMap = new HashMap<Panel, String>();
	
	static {
		panelMap.put(null, "");
	}
	
	public static JPanel getPanel(Panel panel) {
//		JPanel panel = new JPanel(new BorderLayout());
//		JPanel head = new JPanel();
//		head.setLayout(new BoxLayout(head, BoxLayout.X_AXIS));
//		final JTextField xmlTestFileTextField = new JTextField();
//		
//		JButton addXMLTestFile = new JButton("Add XML test file");
//		addXMLTestFile.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if ("".equals(xmlTestFileTextField.getText().trim())) {
//					
//				}
//				if (!model.addXMLTestFile(xmlTestFileTextField.getText().trim())) {
//					
//				}
//			}
//		});
//		head.add(xmlTestFileTextField);
//		head.add(addXMLTestFile);
//		
//		final JTable xmlTestFileTable = new JTable(model);
//		JScrollPane scrollPane = new JScrollPane(xmlTestFileTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		model.addTableModelListener(new TableModelListener() {
//			
//			@Override
//			public void tableChanged(TableModelEvent e) {
//				xmlTestFileTable.validate();
//				xmlTestFileTextField.setText("");
//			}
//		});
//		panel.add(head, BorderLayout.NORTH);
//		panel.add(scrollPane, BorderLayout.CENTER);
//		return panel;
		return new JPanel();
	}
}
