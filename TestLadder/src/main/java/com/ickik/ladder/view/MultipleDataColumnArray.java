package com.ickik.ladder.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ickik.ladder.values.CriticalityEnum;

@SuppressWarnings("serial")
final class MultipleDataColumnArray extends JPanel {

	public MultipleDataColumnArray(Map<String, Integer> dataList) {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1f;
		gbc.weighty = 0f;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		int y = 0;
		int total = 0;
		for (CriticalityEnum c : CriticalityEnum.values()) {
			int val = dataList.get(c.toString());
			gbc.gridy = y++;
			total += val;
			JLabel data = getStatsLabel(Integer.toString(val));
			add(data, gbc);
		}
		gbc.gridy = y;
		JLabel totalLbl = getStatsLabel(Integer.toString(total));
		add(totalLbl, gbc);
	}
	
	private JLabel getStatsLabel(String text) {
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(JLabel.RIGHT);
		label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		return label;
	}
}
