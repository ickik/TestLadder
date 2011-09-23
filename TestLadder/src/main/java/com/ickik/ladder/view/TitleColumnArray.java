package com.ickik.ladder.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
final class TitleColumnArray extends JPanel {

	public TitleColumnArray(String category, List<String> criticalityList) {
		super(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1f;
		gbc.weighty = 0f;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 5;
		gbc.gridwidth = 1;
		JLabel categoryTitle = new JLabel(category);
		add(categoryTitle, gbc);
		gbc.gridx = 1;
		gbc.gridheight = 1;
		int y = 0;
		for (String s : criticalityList) {
			gbc.gridy = y++;
			JLabel critcality = getStatsLabel(s);
			add(critcality, gbc);
		}
		gbc.gridy = y;
		JLabel total = getStatsLabel("total");
		add(total, gbc);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
	}
	
	private JLabel getStatsLabel(String text) {
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(JLabel.RIGHT);
		label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		return label;
	}
}
