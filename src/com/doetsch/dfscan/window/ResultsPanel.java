/*
 * Copyright (C) 2014 Jacob Wesley Doetsch
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.doetsch.dfscan.window;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.DefaultComboBoxModel;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import com.doetsch.dfscan.DFScan;
import com.doetsch.dfscan.core.Report;
import com.doetsch.dfscan.util.ContentIndex;
import com.doetsch.dfscan.util.FolderChooser;
import com.doetsch.dfscan.util.HashableFile;

import java.awt.Dimension;





public class ResultsPanel extends TabbedPanel {

	/*
	 * A table cell renderer that defines a cell's background color
	 * by the boolean value of the row's first element; true is 
	 * purple, false is green.
	 */
	class GroupCellRenderer extends DefaultTableCellRenderer {
		
		public Component getTableCellRendererComponent(JTable table,
	            Object value, boolean isSelected, boolean hasFocus, int row,
	            int column) {

	        super.getTableCellRendererComponent(table,
	                value, isSelected, hasFocus, row, column);
	        
	        if (table.getModel().getValueAt(row, 1) == (Boolean) true) {
	        
		        if (table.getModel().getValueAt(row, 0) == (Boolean)true) {
		            setBackground(new Color(207, 233, 255));
		        } else {
		            setBackground(new Color(255, 237, 216));
		        }
		        
	        } else {
	        	setBackground(new Color(255, 91, 91));
	        }
	        
	        if (isSelected) {
	        	setBackground(new Color(128, 196, 128));
	        }
	        
	        return this;
		}
		
	}
	
	/*
	 * GroupTableBuilder provides a method, build(), which generates and
	 * returns a JTable configured for and filled with the duplicate file
	 * indices contents.
	 */
	private class GroupTableBuilder {
		
		private ArrayList<ContentIndex> indices;
		
		private GroupTableBuilder (ArrayList<ContentIndex> indices) {
			this.indices = indices;
		}
		
		private JTable build () {
			JTable table = new JTable();
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setShowHorizontalLines(true);
			
			/*
			 * Disables dragging of columns by overriding moveColumn.
			 */
			table.setColumnModel(new DefaultTableColumnModel() {
				public void moveColumn (int column, int targetColumn) {
				}
			});
			table.setRowSelectionAllowed(false);
			table.setModel(new GroupTableModel());
			
			((JLabel)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
			
			scrollPaneDuplicateFiles.setViewportView(table);
			
			boolean alternator = true;
			
			DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
			table.setDefaultRenderer(Object.class, new GroupCellRenderer());
			
			/*
			 * Populate the results table
			 */
			for (ContentIndex index : indices) {
				alternator = !alternator;
				for (int i = 0; i < index.getSize(); i++) {
					Object[] rowEntry = {alternator,
							index.getContents().get(i).exists(),
							(i == 0 ? false : true),
							index.getContents().get(i).getName(),
							index.getContents().get(i).getPath(),
							sizeString(index.getContents().get(i).length())};
					
					tableModel.addRow(rowEntry);				
				}
			}
			
			setColumnProperties(table, 0, false, 0);
			setColumnProperties(table, 1, false, 0);
			
			setColumnProperties(table, 2, false, 24);
			setColumnProperties(table, 3, true, 350);
			setColumnProperties(table, 4, true, 650);
			setColumnProperties(table, 5, false, 100);
			
			return table;
		}
		
		private String sizeString (long length) {
			if (length > 1024)  {
				return String.valueOf((int) (length / 1024)) + " KB";
				
			} else {
				return length + " B";
			}
		}

		private void setColumnProperties (JTable table, int index, boolean isResizable, int width) {
			TableColumn column = table.getColumnModel().getColumn(index);
			column.setResizable(isResizable);
			column.setMinWidth(0);
			column.setPreferredWidth(width);
			column.setMaxWidth(1024 * 1024);
			
		}
				
	}
	
	/*
	 * 
	 */
	private class GroupTableModel extends DefaultTableModel {
		
		Class[] columnTypes = new Class[] {
				Boolean.class, Boolean.class, Boolean.class, String.class, String.class, String.class};
		boolean[] columnEditables = new boolean[] {false, false, true, false, false, false};
		
		
		private GroupTableModel() {
			super(new Object[][] {},
				new String[] {"Group Color", "Exists", "", "File Name", "Path", "Size"});
		}
		
		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}
		
		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}
		
	}
	
	//private JPanel contentPane;
	private JTable table;
	private JScrollPane scrollPaneDuplicateFiles;
	private Report resultsReport;
	private JButton moveButton;
	private JComboBox<String> sortingComboBox;
	private JLabel scanDetailsLabel;
	private JLabel scanResultsLabel;
	private JComboBox<String> selectorComboBox;
	private JButton deleteButton;
	private Box headerBox;
	private Box controlBox;
	private JTabbedPane parentPane;
	private Box verticalBox;
	private Component glue;
	private Box horizontalBox;
	private Box horizontalBox_1;
	
	/**
	 * Create the frame.
	 */
	public ResultsPanel(Report resultsReport, JTabbedPane parentPane, TrayIcon trayIcon) {

		super(resultsReport.getFinishDate() + " " + resultsReport.getFinishTime() + " " + resultsReport.getProfileName(),
				new ImageIcon(DFScan.class.getResource("resources/icons/report_icon2.gif")), trayIcon);
		setBorder(new EmptyBorder(6, 6, 6, 6));
		
		this.parentPane = parentPane;
		this.resultsReport = resultsReport;
		
		initComponents();
		setBehavior();
		setDefaultValues();
		
	}

	private void initComponents() {
		
		setLayout(new BorderLayout(0, 0));
		
		parentPane.addTab("", null, this, null);
		parentPane.setTabComponentAt(
				parentPane.indexOfComponent(this), this.getTabAsComponent());
		parentPane.setSelectedComponent(this);
		
		
		scrollPaneDuplicateFiles = new JScrollPane();
		scrollPaneDuplicateFiles.setBounds(12, 126, 840, 354);
		add(scrollPaneDuplicateFiles, BorderLayout.CENTER);
		
		int fileCount = 0;
		for (ContentIndex index : resultsReport.getGroups()) {
			for (HashableFile file : index) {
				fileCount++;
			}
		}
		
		headerBox = Box.createVerticalBox();
		add(headerBox, BorderLayout.NORTH);
		
		verticalBox = Box.createVerticalBox();
		headerBox.add(verticalBox);
		
		horizontalBox = Box.createHorizontalBox();
		verticalBox.add(horizontalBox);
		
		scanDetailsLabel = new JLabel("");
		horizontalBox.add(scanDetailsLabel);
		scanDetailsLabel.setText("Scan started by " + resultsReport.getUser()
		+ " on host " + resultsReport.getHost()
		+ " at " + resultsReport.getStartTime()
		+ " on " + resultsReport.getStartDate());
		
		horizontalBox_1 = Box.createHorizontalBox();
		verticalBox.add(horizontalBox_1);
		
		scanResultsLabel = new JLabel("");
		horizontalBox_1.add(scanResultsLabel);
		scanResultsLabel.setText("Found " + fileCount + " duplicate files in "
				+ resultsReport.getGroups().size() + " common groups");
		
		controlBox = Box.createHorizontalBox();
		controlBox.setBorder(new EmptyBorder(6, 0, 6, 0));
		headerBox.add(controlBox);
		
		selectorComboBox = new JComboBox<String>();
		selectorComboBox.setMaximumSize(new Dimension(240, 32767));
		controlBox.add(selectorComboBox);
		selectorComboBox.setModel(new DefaultComboBoxModel(new String[] {"Default Selection", "Select All Entries", "Select None"}));
		selectorComboBox.setBounds(12, 60, 258, 24);
		

		sortingComboBox = new JComboBox<String>();
		sortingComboBox.setMaximumSize(new Dimension(240, 32767));
		controlBox.add(sortingComboBox);
		sortingComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Sort By...", "Size (Ascending)", "Size (Descending)", "Name (Ascending)", "Name (Descending)"}));
		sortingComboBox.setBounds(282, 60, 570, 24);
		
		glue = Box.createGlue();
		controlBox.add(glue);
		
		deleteButton = new JButton("Delete Selected...");
		controlBox.add(deleteButton);
		deleteButton.setBounds(438, 492, 414, 24);
		
		moveButton = new JButton("Move Selected Files...");
		controlBox.add(moveButton);
		
		buildTable();
	}

	private void buildTable () {
		table = (new GroupTableBuilder(resultsReport.getGroups())).build();
		table.addMouseListener(new MouseAdapter() {

			public void mousePressed (MouseEvent e) {
				copyPathToClipboard(e);
			}
			
		});
		
		table.setShowVerticalLines(false);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
	}
	
	private void copyPathToClipboard (MouseEvent e) {
		
		String path;
		Point p = e.getPoint();
		int row = table.rowAtPoint(p);
		int col = table.columnAtPoint(p);

		if ((e.getClickCount() == 2)  &&
				((row > -1) && (col > -1))) {

			path = (String) ((DefaultTableModel)table.getModel()).getValueAt(row, 4);
		
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection clipBoardData = new StringSelection(path);
			clipboard.setContents(clipBoardData, clipBoardData);
			//labelStatusBar.setText("Copied selected path to clipboard: " + path);		
		}
	}
	
	private void setBehavior () {
		
		selectorComboBox.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed (ActionEvent e) {

				DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
				
				switch (selectorComboBox.getSelectedIndex()) {
					case 0:
						boolean groupID = true;
						for (int row = 0; row < tableModel.getRowCount(); row++) {
							if (groupID != (boolean) tableModel.getValueAt(row, 0)) {
								tableModel.setValueAt(false, row, 2);
								groupID = !groupID;
								
							} else {
								tableModel.setValueAt(true, row, 2);
							}
						}
						
						break;
						
					case 1:
						for (int row = 0; row < tableModel.getRowCount(); row++) {
							tableModel.setValueAt(true, row, 2);
						}						
						break;
						
					case 2:
						for (int row = 0; row < tableModel.getRowCount(); row++) {
							tableModel.setValueAt(false, row, 2);
						}
						break;
				}
			}
			
		});
		
		moveButton.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed (ActionEvent e) {

				File destinationPath = null;
				ArrayList<File> pathList = getSelectedEntries();
				FolderChooser folderChooser = new FolderChooser(ResultsPanel.this,
						new File(System.getProperty("user.home")),
								"Select destination folder...");
				
				if (folderChooser.getFolder()) {
					destinationPath = folderChooser.getSelectedFile();
				}
				
				if (destinationPath == null) {
					return;
				}
				
				for (File path : pathList) {

					try {
						Files.move(FileSystems.getDefault().getPath(path.getPath()),
								FileSystems.getDefault().getPath(destinationPath.getPath() + "/" + path.getName()));
						//labelStatusBar.setText("Moved " + path.getPath() + ".");

					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				buildTable();
				
			}
			
		});
		
		deleteButton.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed (ActionEvent e) {
				File destinationPath = null;
				ArrayList<File> pathList = getSelectedEntries();
								
				for (File path : pathList) {
					
					
					try {
						Files.deleteIfExists(FileSystems.getDefault().getPath(path.getPath()));
						//labelStatusBar.setText("Deleted " + path.getPath() + ".");

						
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
				
				buildTable();
				
			}
			
		});
		
	}

	private ArrayList<File> getSelectedEntries () {
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		ArrayList<File> pathList = new ArrayList<File>();
		
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			
			if (((boolean) tableModel.getValueAt(row, 2)) &&
				((boolean) tableModel.getValueAt(row, 1))) {
				
				pathList.add(new File((String) tableModel.getValueAt(row, 4)));
			}
		}
		
		return pathList;
	}
	
	private void setDefaultValues () {
//		setVisible(true);
		
		int fileCount = 0;
		for (ContentIndex i : resultsReport.getGroups()) {
			for (HashableFile f : i) {
				fileCount++;
			}
		}
		
		
	}

	@Override
	public void tabCloseButtonAction () {
		closePanel();
	}

	public void setTabTitle (String title) {
		super.setTabTitle(title);
	}

	@Override
	public void closePanel () {
		int tabIndex = parentPane.indexOfComponent(this);
		
		if (tabIndex > -1) {
			parentPane.remove(tabIndex);
		}
	}
	
}
