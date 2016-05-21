package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class KeypairTable extends JTable {

	private static final long serialVersionUID = 158730540208618426L;
	
	public KeypairTable() {
		DefaultTableModel dtm = new DefaultTableModel();
		dtm.addColumn("No.");
		dtm.addColumn("Alias");
		this.setModel(dtm);
		
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) { }
			
			@Override
			public void mousePressed(MouseEvent e) { }
			
			@Override
			public void mouseExited(MouseEvent e) { }
			
			@Override
			public void mouseEntered(MouseEvent e) { }
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
					String v = (String) getValueAt(getSelectedRow(), 1);
					
					KeyStore ks = MainWindow.getInstance().getCurrentKeystore();
					System.out.println(ks);
					/*
					try {
						if(ks.isCertificateEntry(v)) {
							Certificate cert = ks.getCertificate(v);
							System.out.println(cert);
						}
					} catch (KeyStoreException e1) {
						e1.printStackTrace();
					}*/
					
					//ShowCertDialog scd = new ShowCertDialog();
					//scd.setTitle("");
				}
			}
			
		});
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

}
