package gui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class KeypairTable extends JTable {

	private static final long serialVersionUID = 158730540208618426L;

	TableModel model;
	
	public KeypairTable() {
		DefaultTableModel dtm = new DefaultTableModel();
		dtm.addColumn("No.");
		dtm.addColumn("Alias");
		this.setModel(dtm);		
	}
	
	public KeypairTable(TableModel model) {
		this.model = model;
	}

}
