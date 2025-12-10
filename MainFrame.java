
/**
 *
 * @author Pearly Jaleco
 */
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.undo.UndoManager;
import javax.swing.text.Document;
import java.awt.Color;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;


public class MainFrame extends JFrame {
    
    private File projectDirectory = new File("project");
    private File currentFile = null;
    private UndoManager undoManager = new UndoManager();
    
    public MainFrame() {
        initComponents();
        setupComponents();
    }
    
    private void setupComponents() {
        // Setup undo manager
        Document doc = CodeEditorTextArea.getDocument();
        doc.addUndoableEditListener(undoManager);
        
        // Setup tree renderer
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) jTree1.getCellRenderer();
        renderer.setLeafIcon(new ImageIcon("src/icons/file.png"));
        renderer.setTextNonSelectionColor(Color.BLACK);
        
        // Ensure project directory exists
        if (!projectDirectory.exists()) {
            projectDirectory.mkdir();
        }
         // Load files into the tree at startup
        refreshTree();
        jTree1.setRootVisible(false);
  

    
    // DETECTS WHEN NODE IS SELECTED 

     jTree1.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
            if (node == null) return;
            
            File file = buildFilePath(node);
            if (!file.isFile()) return;
            
            try {
                currentFile = file;
                String content = new String(Files.readAllBytes(file.toPath()));
                CodeEditorTextArea.setText(content);
                CodeEditorPanel.setTitleAt(0, file.getName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    
    private File getSelectedTreeFile() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
        if (node == null) return null;
        return buildFilePath(node);
    }
    
    private File buildFilePath(DefaultMutableTreeNode node) {
        ArrayList<String> parts = new ArrayList<>();
        while (node.getParent() != null) {
            parts.add(0, node.toString());
            node = (DefaultMutableTreeNode) node.getParent();
        }
        File f = projectDirectory;
        for (String p : parts) {
            f = new File(f, p);
        }
        return f;
    }
    
    // REFRESH JTREE TO DISPLAY FILE/FOLDER
     private void refreshTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(projectDirectory.getName());
        addFilesToNode(root, projectDirectory);
        
        DefaultTreeModel model = new DefaultTreeModel(root) {
            @Override
            public boolean isLeaf(Object node) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                File f = buildFilePath(treeNode);
                return f.isFile();
            }
        };
        
        jTree1.setModel(model);
    }

    // RECURSIVELY ADD FILES AND DIRECTORIES TO THE TREE NODE 
     private void addFilesToNode(DefaultMutableTreeNode node, File file) {
        File[] files = file.listFiles();
        if (files == null) return;
        for (File f : files) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(f.getName());
            node.add(child);
            if (f.isDirectory()) {
                addFilesToNode(child, f);
            }
        }
    }
    
   private void saveFileAs() {
        JFileChooser chooser = new JFileChooser(projectDirectory);
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File newFile = chooser.getSelectedFile();
            
            try {
                if (currentFile != null && !currentFile.equals(newFile)) {
                    currentFile.delete();
                }
                
                FileWriter writer = new FileWriter(newFile);
                writer.write(CodeEditorTextArea.getText());
                writer.close();
                
                currentFile = newFile;
                CodeEditorPanel.setTitleAt(0, currentFile.getName());
                refreshTree();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
   

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PopMenuFILE = new javax.swing.JPopupMenu();
        NEWFileMenu = new javax.swing.JMenuItem();
        SAVEFileMenu = new javax.swing.JMenuItem();
        SAVEASFileMenu = new javax.swing.JMenuItem();
        PopMenuEDIT = new javax.swing.JPopupMenu();
        DELETEMenu = new javax.swing.JMenuItem();
        COPYMenu = new javax.swing.JMenuItem();
        PASTEMenu = new javax.swing.JMenuItem();
        PopMenuHELP = new javax.swing.JPopupMenu();
        USERGUIDEMenu = new javax.swing.JMenuItem();
        ABOUTMenu = new javax.swing.JMenuItem();
        PopMenuSETTINGS = new javax.swing.JPopupMenu();
        TextColorMenu = new javax.swing.JMenuItem();
        BackgroundColorMenu = new javax.swing.JMenuItem();
        BorderPanel = new javax.swing.JPanel();
        TopPanel = new javax.swing.JPanel();
        LOGO = new javax.swing.JButton();
        BtnFILE = new javax.swing.JButton();
        BtnEDIT = new javax.swing.JButton();
        BtnHELP = new javax.swing.JButton();
        BtnRUN = new javax.swing.JButton();
        SideBarPanel = new javax.swing.JPanel();
        BtnSETTINGS = new javax.swing.JButton();
        MainSlpitPanel = new javax.swing.JSplitPane();
        ProjectPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        RightSplit = new javax.swing.JSplitPane();
        OutputTabbedPanel = new javax.swing.JTabbedPane();
        OutputTextPanel = new javax.swing.JScrollPane();
        OutputTextArea = new javax.swing.JTextArea();
        CodeEditorPanel = new javax.swing.JTabbedPane();
        CodeEditorScrollPanel = new javax.swing.JScrollPane();
        CodeEditorTextArea = new javax.swing.JTextArea();

        PopMenuFILE.setBackground(new java.awt.Color(188, 166, 134));

        NEWFileMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        NEWFileMenu.setBackground(new java.awt.Color(188, 166, 134));
        NEWFileMenu.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        NEWFileMenu.setForeground(new java.awt.Color(78, 36, 2));
        NEWFileMenu.setText("New File");
        NEWFileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NEWFileMenuActionPerformed(evt);
            }
        });
        PopMenuFILE.add(NEWFileMenu);

        SAVEFileMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        SAVEFileMenu.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        SAVEFileMenu.setForeground(new java.awt.Color(78, 36, 2));
        SAVEFileMenu.setText("Save");
        SAVEFileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SAVEFileMenuActionPerformed(evt);
            }
        });
        PopMenuFILE.add(SAVEFileMenu);

        SAVEASFileMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        SAVEASFileMenu.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        SAVEASFileMenu.setForeground(new java.awt.Color(78, 36, 2));
        SAVEASFileMenu.setText("Save As");
        SAVEASFileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SAVEASFileMenuActionPerformed(evt);
            }
        });
        PopMenuFILE.add(SAVEASFileMenu);

        DELETEMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        DELETEMenu.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        DELETEMenu.setForeground(new java.awt.Color(78, 36, 2));
        DELETEMenu.setText("Delete");
        DELETEMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DELETEMenuActionPerformed(evt);
            }
        });
        PopMenuEDIT.add(DELETEMenu);

        COPYMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        COPYMenu.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        COPYMenu.setForeground(new java.awt.Color(78, 36, 2));
        COPYMenu.setText("Copy");
        COPYMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                COPYMenuActionPerformed(evt);
            }
        });
        PopMenuEDIT.add(COPYMenu);

        PASTEMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        PASTEMenu.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        PASTEMenu.setForeground(new java.awt.Color(78, 36, 2));
        PASTEMenu.setText("Paste");
        PASTEMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PASTEMenuActionPerformed(evt);
            }
        });
        PopMenuEDIT.add(PASTEMenu);

        USERGUIDEMenu.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        USERGUIDEMenu.setForeground(new java.awt.Color(78, 36, 2));
        USERGUIDEMenu.setText("User Guide");
        USERGUIDEMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                USERGUIDEMenuActionPerformed(evt);
            }
        });
        PopMenuHELP.add(USERGUIDEMenu);

        ABOUTMenu.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        ABOUTMenu.setForeground(new java.awt.Color(78, 36, 2));
        ABOUTMenu.setText("About ");
        ABOUTMenu.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        ABOUTMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ABOUTMenuActionPerformed(evt);
            }
        });
        PopMenuHELP.add(ABOUTMenu);

        TextColorMenu.setBackground(new java.awt.Color(255, 234, 218));
        TextColorMenu.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        TextColorMenu.setForeground(new java.awt.Color(78, 36, 2));
        TextColorMenu.setText("Text Color");
        TextColorMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextColorMenuActionPerformed(evt);
            }
        });
        PopMenuSETTINGS.add(TextColorMenu);

        BackgroundColorMenu.setBackground(new java.awt.Color(255, 234, 218));
        BackgroundColorMenu.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        BackgroundColorMenu.setForeground(new java.awt.Color(78, 36, 2));
        BackgroundColorMenu.setText("Background Color ");
        BackgroundColorMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackgroundColorMenuActionPerformed(evt);
            }
        });
        PopMenuSETTINGS.add(BackgroundColorMenu);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1400, 900));
        setMinimumSize(new java.awt.Dimension(1400, 900));
        setPreferredSize(new java.awt.Dimension(1490, 900));

        BorderPanel.setBackground(new java.awt.Color(255, 234, 218));
        BorderPanel.setMaximumSize(new java.awt.Dimension(1400, 1000));
        BorderPanel.setMinimumSize(new java.awt.Dimension(1400, 1000));
        BorderPanel.setPreferredSize(new java.awt.Dimension(1400, 1000));
        BorderPanel.setLayout(new java.awt.BorderLayout());

        TopPanel.setBackground(new java.awt.Color(78, 36, 2));
        TopPanel.setMaximumSize(new java.awt.Dimension(60, 60));
        TopPanel.setPreferredSize(new java.awt.Dimension(1000, 80));

        LOGO.setBackground(new java.awt.Color(78, 36, 2));
        LOGO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/1.png"))); // NOI18N
        LOGO.setContentAreaFilled(false);

        BtnFILE.setBackground(new java.awt.Color(78, 36, 2));
        BtnFILE.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        BtnFILE.setForeground(new java.awt.Color(255, 234, 218));
        BtnFILE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/2.png"))); // NOI18N
        BtnFILE.setText("File");
        BtnFILE.setToolTipText("");
        BtnFILE.setContentAreaFilled(false);
        BtnFILE.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BtnFILE.setIconTextGap(0);
        BtnFILE.setPreferredSize(new java.awt.Dimension(80, 70));
        BtnFILE.setVerifyInputWhenFocusTarget(false);
        BtnFILE.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        BtnFILE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnFILEActionPerformed(evt);
            }
        });

        BtnEDIT.setBackground(new java.awt.Color(78, 36, 2));
        BtnEDIT.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        BtnEDIT.setForeground(new java.awt.Color(255, 234, 218));
        BtnEDIT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/3.png"))); // NOI18N
        BtnEDIT.setText("Edit");
        BtnEDIT.setContentAreaFilled(false);
        BtnEDIT.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BtnEDIT.setIconTextGap(0);
        BtnEDIT.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        BtnEDIT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEDITActionPerformed(evt);
            }
        });

        BtnHELP.setBackground(new java.awt.Color(78, 36, 2));
        BtnHELP.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        BtnHELP.setForeground(new java.awt.Color(255, 234, 218));
        BtnHELP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/4.png"))); // NOI18N
        BtnHELP.setText("Help");
        BtnHELP.setContentAreaFilled(false);
        BtnHELP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BtnHELP.setIconTextGap(0);
        BtnHELP.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        BtnHELP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHELPActionPerformed(evt);
            }
        });

        BtnRUN.setBackground(new java.awt.Color(78, 36, 2));
        BtnRUN.setFont(new java.awt.Font("Bookman Old Style", 3, 14)); // NOI18N
        BtnRUN.setForeground(new java.awt.Color(255, 234, 218));
        BtnRUN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/5.png"))); // NOI18N
        BtnRUN.setText("Run");
        BtnRUN.setContentAreaFilled(false);
        BtnRUN.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BtnRUN.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        BtnRUN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRUNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout TopPanelLayout = new javax.swing.GroupLayout(TopPanel);
        TopPanel.setLayout(TopPanelLayout);
        TopPanelLayout.setHorizontalGroup(
            TopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TopPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(LOGO, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(BtnFILE, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BtnEDIT)
                .addGap(2, 2, 2)
                .addComponent(BtnHELP)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 946, Short.MAX_VALUE)
                .addComponent(BtnRUN, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(121, 121, 121))
        );
        TopPanelLayout.setVerticalGroup(
            TopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TopPanelLayout.createSequentialGroup()
                .addGroup(TopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(BtnFILE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtnRUN, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LOGO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtnHELP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtnEDIT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        BorderPanel.add(TopPanel, java.awt.BorderLayout.PAGE_START);

        SideBarPanel.setBackground(new java.awt.Color(78, 36, 2));
        SideBarPanel.setMaximumSize(new java.awt.Dimension(90, 1000));
        SideBarPanel.setMinimumSize(new java.awt.Dimension(90, 1000));
        SideBarPanel.setPreferredSize(new java.awt.Dimension(90, 1000));
        SideBarPanel.setLayout(new java.awt.BorderLayout());

        BtnSETTINGS.setBackground(new java.awt.Color(78, 36, 2));
        BtnSETTINGS.setFont(new java.awt.Font("Bookman Old Style", 3, 12)); // NOI18N
        BtnSETTINGS.setForeground(new java.awt.Color(255, 234, 218));
        BtnSETTINGS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/8.png"))); // NOI18N
        BtnSETTINGS.setText("Settings");
        BtnSETTINGS.setContentAreaFilled(false);
        BtnSETTINGS.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BtnSETTINGS.setIconTextGap(0);
        BtnSETTINGS.setPreferredSize(new java.awt.Dimension(70, 80));
        BtnSETTINGS.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        BtnSETTINGS.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        BtnSETTINGS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSETTINGSActionPerformed(evt);
            }
        });
        SideBarPanel.add(BtnSETTINGS, java.awt.BorderLayout.PAGE_END);

        BorderPanel.add(SideBarPanel, java.awt.BorderLayout.LINE_START);

        MainSlpitPanel.setBackground(new java.awt.Color(255, 234, 218));
        MainSlpitPanel.setDividerLocation(160);
        MainSlpitPanel.setDividerSize(0);
        MainSlpitPanel.setMaximumSize(new java.awt.Dimension(1400, 1000));
        MainSlpitPanel.setMinimumSize(new java.awt.Dimension(85, 1000));
        MainSlpitPanel.setPreferredSize(new java.awt.Dimension(1400, 1000));

        ProjectPanel.setBackground(new java.awt.Color(239, 209, 165));
        ProjectPanel.setMaximumSize(new java.awt.Dimension(160, 1000));
        ProjectPanel.setMinimumSize(new java.awt.Dimension(160, 1000));
        ProjectPanel.setPreferredSize(new java.awt.Dimension(160, 1000));

        jScrollPane1.setBackground(new java.awt.Color(239, 209, 165));
        jScrollPane1.setBorder(null);

        jTree1.setBackground(new java.awt.Color(239, 209, 165));
        jTree1.setFont(new java.awt.Font("Bookman Old Style", 3, 10)); // NOI18N
        jTree1.setForeground(new java.awt.Color(78, 36, 2));
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Project");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("calculator.java");
        treeNode1.add(treeNode2);
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane1.setViewportView(jTree1);

        javax.swing.GroupLayout ProjectPanelLayout = new javax.swing.GroupLayout(ProjectPanel);
        ProjectPanel.setLayout(ProjectPanelLayout);
        ProjectPanelLayout.setHorizontalGroup(
            ProjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
        );
        ProjectPanelLayout.setVerticalGroup(
            ProjectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
        );

        MainSlpitPanel.setLeftComponent(ProjectPanel);

        RightSplit.setBackground(new java.awt.Color(255, 234, 218));
        RightSplit.setDividerLocation(500);
        RightSplit.setDividerSize(0);
        RightSplit.setForeground(new java.awt.Color(78, 36, 2));
        RightSplit.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        RightSplit.setResizeWeight(0.7);
        RightSplit.setMinimumSize(new java.awt.Dimension(85, 1000));
        RightSplit.setPreferredSize(new java.awt.Dimension(253, 1000));

        OutputTabbedPanel.setBackground(new java.awt.Color(141, 115, 78));
        OutputTabbedPanel.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        OutputTabbedPanel.setPreferredSize(new java.awt.Dimension(800, 500));

        OutputTextArea.setEditable(false);
        OutputTextArea.setBackground(new java.awt.Color(188, 166, 134));
        OutputTextArea.setColumns(20);
        OutputTextArea.setFont(new java.awt.Font("Bookman Old Style", 3, 12)); // NOI18N
        OutputTextArea.setRows(5);
        OutputTextPanel.setViewportView(OutputTextArea);
        OutputTextArea.getAccessibleContext().setAccessibleName("OutputTxt");

        OutputTabbedPanel.addTab("Output", OutputTextPanel);

        RightSplit.setRightComponent(OutputTabbedPanel);

        CodeEditorPanel.setBackground(new java.awt.Color(141, 114, 80));
        CodeEditorPanel.setForeground(new java.awt.Color(78, 36, 2));
        CodeEditorPanel.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        CodeEditorScrollPanel.setBackground(new java.awt.Color(255, 234, 218));
        CodeEditorScrollPanel.setBorder(null);

        CodeEditorTextArea.setBackground(new java.awt.Color(255, 234, 218));
        CodeEditorTextArea.setColumns(20);
        CodeEditorTextArea.setFont(new java.awt.Font("Bookman Old Style", 3, 12)); // NOI18N
        CodeEditorTextArea.setForeground(new java.awt.Color(78, 36, 2));
        CodeEditorTextArea.setRows(5);
        CodeEditorTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CodeEditorTextAreaKeyPressed(evt);
            }
        });
        CodeEditorScrollPanel.setViewportView(CodeEditorTextArea);

        CodeEditorPanel.addTab("Sourcecode", CodeEditorScrollPanel);

        RightSplit.setLeftComponent(CodeEditorPanel);
        CodeEditorPanel.getAccessibleContext().setAccessibleName("");

        MainSlpitPanel.setRightComponent(RightSplit);

        BorderPanel.add(MainSlpitPanel, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BorderPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1490, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BorderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 850, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnFILEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnFILEActionPerformed
       PopMenuFILE.show(BtnFILE, 0, BtnFILE.getHeight());
        
    }//GEN-LAST:event_BtnFILEActionPerformed

    private void BtnHELPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHELPActionPerformed
      PopMenuHELP.show(BtnHELP, 0, BtnHELP.getHeight());
    }//GEN-LAST:event_BtnHELPActionPerformed

    private void BtnEDITActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEDITActionPerformed
      PopMenuEDIT.show(BtnEDIT, 0, BtnEDIT.getHeight());
    }//GEN-LAST:event_BtnEDITActionPerformed

    private void BtnSETTINGSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSETTINGSActionPerformed
     PopMenuSETTINGS.show(BtnSETTINGS, 0, BtnSETTINGS.getHeight());
    }//GEN-LAST:event_BtnSETTINGSActionPerformed

    private void BtnRUNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRUNActionPerformed
        try {
        // Clear previous output
        OutputTextArea.setText("");
        
        // Get code from editor
        String code = CodeEditorTextArea.getText();
        
        if (code.trim().isEmpty()) {
            OutputTextArea.setText("Error: No code to execute");
            return;
        }
        
        // 3. Initialize the Interpreter Engine
            InterpreterEngine engine = new InterpreterEngine();
            
            // 4. Run the code and capture the result string
            String result = engine.runCode(code);
            
            // 5. Display the result in the Output Tab
            OutputTextArea.setText(result);
            
            // 6. Switch focus to the Output tab so user sees the result immediately
            OutputTabbedPanel.setSelectedIndex(0);
        
        
        
    } catch (Exception ex) {
        OutputTextArea.setText("Error: " + ex.getMessage());
        ex.printStackTrace();
    }
    }//GEN-LAST:event_BtnRUNActionPerformed

    private void NEWFileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NEWFileMenuActionPerformed
      String name = JOptionPane.showInputDialog(this, "Enter file name:");
        if (name == null || name.trim().isEmpty()) return;
        
        try {
            File newFile = new File(projectDirectory, name);
            if (!newFile.exists()) {
                newFile.createNewFile();
                refreshTree();
            } else {
                JOptionPane.showMessageDialog(this, "File already exists!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_NEWFileMenuActionPerformed
      
    private void SAVEFileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SAVEFileMenuActionPerformed
       try {
            if (currentFile == null) {
                saveFileAs();
                return;
            }
            
            FileWriter writer = new FileWriter(currentFile);
            writer.write(CodeEditorTextArea.getText());
            writer.close();
            JOptionPane.showMessageDialog(this, "Saved!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    
    }//GEN-LAST:event_SAVEFileMenuActionPerformed

    private void SAVEASFileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SAVEASFileMenuActionPerformed
        JFileChooser chooser = new JFileChooser(projectDirectory);
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File newFile = chooser.getSelectedFile();
            
            try {
                if (currentFile != null && !currentFile.equals(newFile)) {
                    currentFile.delete();
                }
                
                FileWriter writer = new FileWriter(newFile);
                writer.write(CodeEditorTextArea.getText());
                writer.close();
                
                currentFile = newFile;
                CodeEditorPanel.setTitleAt(0, currentFile.getName());
                refreshTree();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_SAVEASFileMenuActionPerformed

    private void DELETEMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DELETEMenuActionPerformed
       
        File selected = getSelectedTreeFile();
        if (selected == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Delete '" + selected.getName() + "'?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            deleteRecursively(selected);
            refreshTree();
        }
    }
    
    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursively(child);
            }
        }
        file.delete();
    
    }//GEN-LAST:event_DELETEMenuActionPerformed

    private void CodeEditorTextAreaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CodeEditorTextAreaKeyPressed
        if (evt.isControlDown()) {
        switch (evt.getKeyCode()) {
            case java.awt.event.KeyEvent.VK_Z: // Ctrl+Z
                // Undo
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
                evt.consume();
                break;
                
            case java.awt.event.KeyEvent.VK_Y: // Ctrl+Y
                // Redo
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
                evt.consume();
                break;
                         
        }
    }
    }//GEN-LAST:event_CodeEditorTextAreaKeyPressed

    private void COPYMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_COPYMenuActionPerformed
        CodeEditorTextArea.copy();
    }//GEN-LAST:event_COPYMenuActionPerformed

    private void PASTEMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PASTEMenuActionPerformed
       CodeEditorTextArea.paste();
    }//GEN-LAST:event_PASTEMenuActionPerformed

    private void TextColorMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextColorMenuActionPerformed
     Color color = JColorChooser.showDialog(this, "Choose Text Color", Color.BLACK);
    if (color != null) {
        CodeEditorTextArea.setForeground(color);
    }
    }//GEN-LAST:event_TextColorMenuActionPerformed

    private void BackgroundColorMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackgroundColorMenuActionPerformed
       Color color = JColorChooser.showDialog(this, "Choose Background Color", Color.WHITE);
    if (color != null) {
        CodeEditorTextArea.setBackground(color);
    }
    }//GEN-LAST:event_BackgroundColorMenuActionPerformed

    private void USERGUIDEMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_USERGUIDEMenuActionPerformed
        // TODO add your handling code here:
        // Create the User Guide window
        UserGuideFrame guide = new UserGuideFrame();
        
        // This line is CRITICAL: it ensures closing the guide doesn't close your whole app
        guide.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        // Center the window on screen (optional but looks nice)
        guide.setLocationRelativeTo(null);
        
        // Show it
        guide.setVisible(true);
        
    }//GEN-LAST:event_USERGUIDEMenuActionPerformed

    private void ABOUTMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ABOUTMenuActionPerformed
        // TODO add your handling code here:
        // OPTION 1: If you created a custom AboutFrame (as in Step 1)
        About about = new About();
        about.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        about.setLocationRelativeTo(null);
        about.setVisible(true);

        /* // OPTION 2: If you just want a simple popup without making a new JFrame, 
        // delete the code above and use this one line instead:
        
        JOptionPane.showMessageDialog(this, "Snail Editor v1.0\nCreated by: [Your Name]", "About", JOptionPane.INFORMATION_MESSAGE);
        */
        
    }//GEN-LAST:event_ABOUTMenuActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem ABOUTMenu;
    private javax.swing.JMenuItem BackgroundColorMenu;
    private javax.swing.JPanel BorderPanel;
    private javax.swing.JButton BtnEDIT;
    private javax.swing.JButton BtnFILE;
    private javax.swing.JButton BtnHELP;
    private javax.swing.JButton BtnRUN;
    private javax.swing.JButton BtnSETTINGS;
    private javax.swing.JMenuItem COPYMenu;
    private javax.swing.JTabbedPane CodeEditorPanel;
    private javax.swing.JScrollPane CodeEditorScrollPanel;
    private javax.swing.JTextArea CodeEditorTextArea;
    private javax.swing.JMenuItem DELETEMenu;
    private javax.swing.JButton LOGO;
    private javax.swing.JSplitPane MainSlpitPanel;
    private javax.swing.JMenuItem NEWFileMenu;
    private javax.swing.JTabbedPane OutputTabbedPanel;
    private javax.swing.JTextArea OutputTextArea;
    private javax.swing.JScrollPane OutputTextPanel;
    private javax.swing.JMenuItem PASTEMenu;
    private javax.swing.JPopupMenu PopMenuEDIT;
    private javax.swing.JPopupMenu PopMenuFILE;
    private javax.swing.JPopupMenu PopMenuHELP;
    private javax.swing.JPopupMenu PopMenuSETTINGS;
    private javax.swing.JPanel ProjectPanel;
    private javax.swing.JSplitPane RightSplit;
    private javax.swing.JMenuItem SAVEASFileMenu;
    private javax.swing.JMenuItem SAVEFileMenu;
    private javax.swing.JPanel SideBarPanel;
    private javax.swing.JMenuItem TextColorMenu;
    private javax.swing.JPanel TopPanel;
    private javax.swing.JMenuItem USERGUIDEMenu;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}
