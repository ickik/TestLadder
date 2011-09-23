package com.ickik.ladder.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.BiMap;
import com.ickik.ladder.db.DBConnection;
import com.ickik.ladder.values.User;

/**
 * Handle the JTable associates to the campaign.
 * @author Patrick Allgeyer
 * @version 0.1.000
 */
@SuppressWarnings("serial")
public class CampaignTableModel extends AbstractLadderTableModel {

	private final BiMap<String, Long> campaignMap;
	private final DBConnection connection;
	
	public CampaignTableModel(DBConnection connection) {
		this.connection = connection;
		campaignMap = connection.getCampaignMap();
	}
	
	public boolean addCampaign(String campaign, User user) {
		if (campaignMap.containsKey(campaign)) {
			return false;
		}
		long id = connection.insertCampaign(campaign, user);
		campaignMap.put(campaign, id);
		fireTableRowsInserted(campaignMap.size() - 1, campaignMap.size());
		return true;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}
	
	@Override
	public String getColumnName(int arg0) {
		return "Campaign";
	}

	@Override
	public int getRowCount() {
		return campaignMap.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		List<String> list = new ArrayList<String>();
		list.addAll(campaignMap.keySet());
		return list.get(row);
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}
	
	@Override
	public BiMap<String, Long> getMap() {
		return campaignMap;
	}

}
