package com.doetsch.dfscan.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import java.awt.Dimension;

import javax.swing.JMenuBar;
import javax.swing.JComboBox;
import javax.swing.Box;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTabbedPane;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.doetsch.dfscan.core.Profile;
import com.doetsch.dfscan.core.SettingsContainer;
import com.doetsch.dfscan.filter.ContentIndexFilter;
import com.doetsch.dfscan.filter.NameContainsFilter;
import com.doetsch.dfscan.util.ContentIndex;
import com.doetsch.dfscan.util.FolderChooser;
import com.doetsch.dfscan.util.HashableFile;

import javax.swing.border.EtchedBorder;

public class MainAppWindow2 extends JFrame {

	private JPanel contentPane;
	private JSplitPane splitPane; 
	private JPanel reportPanel;
	private JPanel profilePanel;
	private JMenuBar menuBar;
	private Box profileSelectionBox;
	private JComboBox<ProfileEntry> profileComboBox;
	private JButton updateButton;
	private JButton saveAsButton;
	private JButton importButton;
	private Box startScanControlBox;
	private JButton startScanButton;
	private Component startScanRightGlue;
	private Component startScanLeftGlue;
	private JScrollPane summaryScrollPane;
	private JTextArea summaryTextArea;
	private JTabbedPane profileTabbedPane;
	private JPanel summaryPanel;
	private JTabbedPane reportTabbedPane;
	private JMenu fileMenu;
	private JPanel foldersPanel;
	private Box foldersBox;
	private Box indexingOptionsBox;
	private JCheckBox scanSubFoldersCheckBox;
	private JCheckBox scanHiddenFoldersCheckBox;
	private JCheckBox scanReadOnlyFoldersCheckBox;
	private Component indexingOptionsTopGlue;
	private Component indexingOptionsBottonGlue;
	private Box foldersControlBox;
	private JButton addFolderButton;
	private JButton removeFolderButton;
	private JScrollPane foldersScrollPane;
	private JList<String> foldersList;
	private DefaultListModel<String> foldersListModel;
	private JPanel filtersPanel;
	private Box filtersBox;
	private Box filteringOptionsBox;
	private Box filtersControlBox;
	private JScrollPane filtersScrollPane;
	private JList<FilterListEntry> filtersList;
	private DefaultListModel<FilterListEntry> filtersListModel;
	private JButton addFilterButton;
	private JButton removeFilterButton;
	private JCheckBox indexInclusivelyCheckBox;
	private Component filteringOptionsTopGlue;
	private Component filteringOptionsBottomGlue;
	private JMenuItem openResultsMenuItem;
	private JMenuItem mntmNewMenuItem;
	private JSeparator separator;
	private JMenu helpMenu;
	private JMenuItem aboutMenuItem;
	private JMenuItem updateMenuItem;
	private JSeparator helpMenuSeparator;
	private JMenuItem guideMenuItem;
	private JButton moveUpButton;
	private JButton moveDownButton;
	private Component verticalStrut;

	/**
	 * Launch the application.
	 */
	public static void main (String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run () {
				try {
					MainAppWindow2 frame = new MainAppWindow2();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainAppWindow2 () {
		initComponents();
		setBehavior();
		setDefaultValues();
	}
	
	private void initComponents() {
		setTitle("dfScan");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1026, 768);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		openResultsMenuItem = new JMenuItem("Open Results...");
		fileMenu.add(openResultsMenuItem);
		
		separator = new JSeparator();
		fileMenu.add(separator);
		
		mntmNewMenuItem = new JMenuItem("Exit");
		fileMenu.add(mntmNewMenuItem);
		
		helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		
		guideMenuItem = new JMenuItem("User Guide");
		helpMenu.add(guideMenuItem);
		
		updateMenuItem = new JMenuItem("Check for Updates...");
		helpMenu.add(updateMenuItem);
		
		helpMenuSeparator = new JSeparator();
		helpMenu.add(helpMenuSeparator);
		
		aboutMenuItem = new JMenuItem("About dfScan");
		helpMenu.add(aboutMenuItem);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(12, 0, 0, 0));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		reportPanel = new JPanel();
		reportPanel.setPreferredSize(new Dimension(10, 400));
		splitPane.setLeftComponent(reportPanel);
		reportPanel.setLayout(new BorderLayout(0, 0));
		
		reportTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		reportTabbedPane.setPreferredSize(new Dimension(0, 400));
		reportPanel.add(reportTabbedPane, BorderLayout.CENTER);

		profilePanel = new JPanel();
		splitPane.setRightComponent(profilePanel);
		profilePanel.setLayout(new BorderLayout(0, 0));
		
		profileSelectionBox = Box.createHorizontalBox();
		profileSelectionBox.setBorder(new EmptyBorder(6, 6, 6, 6));
		profilePanel.add(profileSelectionBox, BorderLayout.NORTH);
		
		profileComboBox = new JComboBox<ProfileEntry>();
		profileSelectionBox.add(profileComboBox);
		
		updateButton = new JButton("Update");
		profileSelectionBox.add(updateButton);
		
		saveAsButton = new JButton("Save As...");
		profileSelectionBox.add(saveAsButton);
		
		importButton = new JButton("Import");
		profileSelectionBox.add(importButton);
		
		profileTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		profilePanel.add(profileTabbedPane, BorderLayout.CENTER);
		
		summaryPanel = new JPanel();
		summaryPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
		profileTabbedPane.addTab("Summary", null, summaryPanel, null);
		summaryPanel.setLayout(new BorderLayout(0, 0));
		
		summaryScrollPane = new JScrollPane();
		summaryPanel.add(summaryScrollPane, BorderLayout.CENTER);
		
		summaryTextArea = new JTextArea();
		summaryTextArea.setEditable(false);
		summaryScrollPane.setViewportView(summaryTextArea);
		
		foldersPanel = new JPanel();
		foldersPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
		profileTabbedPane.addTab("Target Folders", null, foldersPanel, null);
		foldersPanel.setLayout(new BorderLayout(0, 0));
		
		foldersBox = Box.createVerticalBox();
		foldersPanel.add(foldersBox, BorderLayout.CENTER);
		
		foldersControlBox = Box.createHorizontalBox();
		foldersBox.add(foldersControlBox);
		
		addFolderButton = new JButton("Add Folder");
		foldersControlBox.add(addFolderButton);
		
		removeFolderButton = new JButton("Remove Folder");
		foldersControlBox.add(removeFolderButton);
		
		foldersScrollPane = new JScrollPane();
		foldersBox.add(foldersScrollPane);
		
		foldersList = new JList<String>();
		foldersScrollPane.setViewportView(foldersList);
		
		indexingOptionsBox = Box.createVerticalBox();
		foldersPanel.add(indexingOptionsBox, BorderLayout.EAST);
		
		indexingOptionsTopGlue = Box.createVerticalGlue();
		indexingOptionsBox.add(indexingOptionsTopGlue);
		
		scanSubFoldersCheckBox = new JCheckBox("Scan sub-folders");
		indexingOptionsBox.add(scanSubFoldersCheckBox);
		
		scanHiddenFoldersCheckBox = new JCheckBox("Scan hidden folders");
		indexingOptionsBox.add(scanHiddenFoldersCheckBox);
		
		scanReadOnlyFoldersCheckBox = new JCheckBox("Scan read-only folders");
		indexingOptionsBox.add(scanReadOnlyFoldersCheckBox);
		
		indexingOptionsBottonGlue = Box.createVerticalGlue();
		indexingOptionsBox.add(indexingOptionsBottonGlue);
		
		filtersPanel = new JPanel();
		filtersPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
		profileTabbedPane.addTab("Filters", null, filtersPanel, null);
		filtersPanel.setLayout(new BorderLayout(0, 0));
		
		filtersBox = Box.createVerticalBox();
		filtersPanel.add(filtersBox, BorderLayout.CENTER);
		
		filtersControlBox = Box.createHorizontalBox();
		filtersBox.add(filtersControlBox);
		
		addFilterButton = new JButton("Add Filter");
		filtersControlBox.add(addFilterButton);
		
		removeFilterButton = new JButton("Remove Filter");
		filtersControlBox.add(removeFilterButton);
		
		filtersScrollPane = new JScrollPane();
		filtersBox.add(filtersScrollPane);
		
		filtersList = new JList<FilterListEntry>();
		filtersScrollPane.setViewportView(filtersList);
		
		filteringOptionsBox = Box.createVerticalBox();
		filtersPanel.add(filteringOptionsBox, BorderLayout.EAST);
		
		filteringOptionsTopGlue = Box.createVerticalGlue();
		filteringOptionsBox.add(filteringOptionsTopGlue);
		
		indexInclusivelyCheckBox = new JCheckBox("Index inclusively");
		filteringOptionsBox.add(indexInclusivelyCheckBox);
		
		verticalStrut = Box.createVerticalStrut(20);
		filteringOptionsBox.add(verticalStrut);
		
		moveUpButton = new JButton("Move Up");
		filteringOptionsBox.add(moveUpButton);
		
		moveDownButton = new JButton("Move Down");
		filteringOptionsBox.add(moveDownButton);
		
		filteringOptionsBottomGlue = Box.createVerticalGlue();
		filteringOptionsBox.add(filteringOptionsBottomGlue);
		
		startScanControlBox = Box.createHorizontalBox();
		startScanControlBox.setBorder(new EmptyBorder(0, 0, 6, 0));
		profilePanel.add(startScanControlBox, BorderLayout.SOUTH);
		
		startScanLeftGlue = Box.createHorizontalGlue();
		startScanControlBox.add(startScanLeftGlue);
		
		startScanButton = new JButton("Start Scan");
		startScanControlBox.add(startScanButton);
		
		startScanRightGlue = Box.createHorizontalGlue();
		startScanControlBox.add(startScanRightGlue);
		splitPane.setDividerLocation(400);
		
		showUserGuide();
//		UserGuidePanel welcomePanel = new UserGuidePanel(reportTabbedPane);
//		reportTabbedPane.addTab("", null, welcomePanel, "Welcome Page");
//		reportTabbedPane.setTabComponentAt(
//				reportTabbedPane.indexOfComponent(welcomePanel), welcomePanel.getTabAsComponent());
	}
	
	private void setBehavior () {
		
		/*
		 * Define the behavior of the UI components within the profile pane
		 * portion of the split pane.		
		 */
		startScanButton.addActionListener(new AbstractAction () {

			@Override
			public void actionPerformed (ActionEvent arg0) {
				
				ProgressPanel progressPanel = new ProgressPanel(buildCurrentDetectionProfile(), reportTabbedPane);
				reportTabbedPane.addTab("", null, progressPanel, null);
				reportTabbedPane.setTabComponentAt(
						reportTabbedPane.indexOfComponent(progressPanel), progressPanel.getTabAsComponent());
				reportTabbedPane.setSelectedComponent(progressPanel);
				
			}
			
		});
		
		profileComboBox.addActionListener(new AbstractAction () {

			@Override
			public void actionPerformed (ActionEvent e) {
				
				if (e.getActionCommand().equals("comboBoxChanged")) {
					
					populateProfileDetails();
				}
				
			}
			
		});
		
		addFilterButton.addActionListener(new AbstractAction () {
			
			@Override
			public void actionPerformed (ActionEvent e) {
				
				FilterBuilderWindow filterBuilderWindow =
						new FilterBuilderWindow(MainAppWindow2.this, filtersListModel);
				populateProfileSummary();
			}
			
		});
		
		removeFilterButton.addActionListener(new AbstractAction () {

			/*
			 * Removes the filter list element at the selected index, provided
			 * one is selected and available.
			 */
			@Override
			public void actionPerformed (ActionEvent e) {
				
				int index = filtersList.getSelectedIndex();
				
				if (index > -1) {
					filtersListModel.removeElementAt(index);
				}
				populateProfileSummary();
			}
			
		});
		
		moveUpButton.addActionListener(new AbstractAction () {

			/*
			 * Move the selected filter entry up the list
			 */
			@Override
			public void actionPerformed (ActionEvent e) {
				
				int index = filtersList.getSelectedIndex();
				
				if ((index < 0) || (filtersListModel.size() < 2) || (index < 1)) {
					return;
				}
				
				FilterListEntry entry = filtersListModel.remove(index);
								
				filtersListModel.insertElementAt(entry, index - 1);
				filtersList.setSelectedIndex(index - 1);
				populateProfileSummary();
			}
			
		});
		
		moveDownButton.addActionListener(new AbstractAction () {

			/*
			 * Move the selected filter entry down the list
			 */
			@Override
			public void actionPerformed (ActionEvent e) {
			
				
				int index = filtersList.getSelectedIndex();
				
				if ((index < 0) || (filtersListModel.size() < 2) || (index > (filtersListModel.size() - 2))) {
					return;
				}
				
				FilterListEntry entry = filtersListModel.remove(index);
				filtersListModel.insertElementAt(entry, index + 1);
				filtersList.setSelectedIndex(index + 1);
				populateProfileSummary();
			}
			
		});
		
		addFolderButton.addActionListener(new AbstractAction () {
			
			/*
			 * Displays a folder choosing dialog box and adds the selected folder
			 * to the target folder list
			 */
			@Override
			public void actionPerformed (ActionEvent e) {
				
				FolderChooser folderChooser = new FolderChooser(
						MainAppWindow2.this, new File(System.getProperty("user.home")), "Select a target folder");
				
				if (folderChooser.getFolder() == true) {
					foldersListModel.addElement(folderChooser.getSelectedFile().getPath());
				}
				
				populateProfileSummary();				
			}
			
		});
		
		removeFolderButton.addActionListener(new AbstractAction () {

			@Override
			public void actionPerformed (ActionEvent e) {

				int index = foldersList.getSelectedIndex();
				
				if (index > -1) {
					foldersListModel.removeElementAt(index);
				}
				
				populateProfileSummary();
			}
			
		});
		
		/*
		 * Defines the behavior of the menu bar items.
		 */
		guideMenuItem.addActionListener(new AbstractAction () {

			@Override
			public void actionPerformed (ActionEvent arg0) {
				showUserGuide();
			}
			
		});
		
		
	}
	
	private void setDefaultValues () {
		populateComboBoxProfiles();
	}
	
	private Profile buildCurrentDetectionProfile () {
		Profile selectedProfile;
		Profile detectionProfile;
		
		/*
		 * While the profile within the profile selector ComboBox could be used,
		 * the user is able to change the options once the profile is loaded. So the
		 * current options & settings need to be used.
		 */
		if (profileComboBox.getSelectedIndex() > -1) {
			selectedProfile = ((ProfileEntry) profileComboBox.getSelectedItem()).getProfile();
			detectionProfile = new Profile(selectedProfile.getName(), selectedProfile.getDescription());					
		} else {
			detectionProfile = new Profile("Custom Profile", "Custom Profile Decsription");
		}
		
		//Set the indexing and scanning settings of the detection profile
		detectionProfile.setSettings(new SettingsContainer(
				indexInclusivelyCheckBox.isSelected(), scanSubFoldersCheckBox.isSelected(),
				scanReadOnlyFoldersCheckBox.isSelected(), scanHiddenFoldersCheckBox.isSelected()));
		
		//Add the current entries within the filter list to the detection profile
		for (int i = 0; i < filtersListModel.getSize(); i++) {
			detectionProfile.getFilters().add(filtersListModel.get(i).getContentIndexFilter());
		}
		
		//Add the current entries within the folder list to the detection profile
		for (int i = 0; i < foldersListModel.getSize(); i++) {
			detectionProfile.getTargets().add(foldersListModel.get(i));
		}
		
		return detectionProfile;
	}

	private void showUserGuide () {
		UserGuidePanel userGuidePanel = new UserGuidePanel(reportTabbedPane);
		reportTabbedPane.addTab("", null, userGuidePanel, "Welcome Page");
		reportTabbedPane.setTabComponentAt(
				reportTabbedPane.indexOfComponent(userGuidePanel), userGuidePanel.getTabAsComponent());
		reportTabbedPane.setSelectedComponent(userGuidePanel);
	}
	
	/*
	 * Loads the detection scan profiles from the resources/profiles folder
	 */
	public void populateComboBoxProfiles () {
		
		ContentIndex sourceIndex = new ContentIndex("profiles/");
		ContentIndex profileIndex = (new NameContainsFilter(".dfscan.profile.xml", true)).enforce(sourceIndex);
		Profile profile;
		
		profileComboBox.setModel(new DefaultComboBoxModel<ProfileEntry>() );
		
		for (HashableFile file : profileIndex) {

			try {
				profile = Profile.load(file.getPath());
				((DefaultComboBoxModel<ProfileEntry>)profileComboBox.getModel()).addElement(new ProfileEntry(profile));	

			} catch (SAXException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			
		}
		
		populateProfileDetails();
		
	}
	
	private void populateProfileSummary () {
		summaryTextArea.setText("");
		summaryTextArea.append(buildCurrentDetectionProfile().getDetailedDescription());
		summaryTextArea.setCaretPosition(0);
	}
	
	private void populateProfileDetails () {
		
		if (profileComboBox.getSelectedIndex() > -1) {
			
			Profile profile = profileComboBox.getModel().getElementAt(profileComboBox.getSelectedIndex())
					.getProfile();
			
//			summaryTextArea.setText("");
//			summaryTextArea.append(profile.getDetailedDescription());
			
			scanSubFoldersCheckBox.setSelected(profile.getSettings().getIndexRecursively());
			scanHiddenFoldersCheckBox.setSelected(profile.getSettings().getIndexHiddenFolders());
			scanReadOnlyFoldersCheckBox.setSelected(profile.getSettings().getIndexReadOnlyFolders());
			indexInclusivelyCheckBox.setSelected(profile.getSettings().getIndexInclusively());			
			
			foldersListModel = new DefaultListModel<String>();
			foldersList.setModel(foldersListModel);
			for (String targetPath : profile.getTargets()) {
				foldersListModel.addElement(targetPath);
			}
			
			filtersListModel = new DefaultListModel<FilterListEntry>();
			filtersList.setModel(filtersListModel);
			for (ContentIndexFilter filter : profile.getFilters()) {
				filtersListModel.addElement(new FilterListEntry(filter));
			}
			
			populateProfileSummary();
			
		}
		
	}
	
}