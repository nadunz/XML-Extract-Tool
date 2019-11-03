
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class XMLExtractTool extends javax.swing.JFrame {

    // file chooser from machine
    private JFileChooser fc;
    // jlist model for view the xml file list
    private DefaultListModel dlm;
    // list for save selected tags
    private ArrayList<String> selectedTags;
    // to store the xml files with errors
    private ArrayList<String> errorsFound;
    // to store unknown elements
    private ArrayList<Unknown> unknowns;
    // the output file
    private File extractionFile;
    private boolean isSelectedOnly;
    private boolean removeQuot;
    
    public static ExtractFormat extractFormat;
    
    /**
     * Creates new form XMLExtractTool
     */
    public XMLExtractTool() {
        // initiate all the ui components
        initComponents();
        // set the location of showing Jframe
        this.setLocationRelativeTo(null);
        // initialize the file chooser
        this.fc = new JFileChooser();
        // add 3 extention filters for the file chooser
        this.fc.addChoosableFileFilter(new FileNameExtensionFilter("Extensible Markup Language file (.xml)", "xml"));
        this.fc.addChoosableFileFilter(new FileNameExtensionFilter("Text Document (.txt)", "txt"));
        this.fc.addChoosableFileFilter(new FileNameExtensionFilter("Rust File (.rs)", "rs"));
        // initialize the list model
        this.dlm = new DefaultListModel();
        // initialize the selected tags list
        this.selectedTags = new ArrayList<>();
        // initialize the errors found list
        this.errorsFound = new ArrayList<>();
        // initialize the list of unknowns
        this.unknowns = new ArrayList<>();
        // add the created list model to the jlist
        xmlFilesList.setModel(dlm);
        // initialize the variable isSelectedOnly to false
        this.isSelectedOnly = false;
        // initialize the variable removeQuot to false
        this.removeQuot = false;
        progressLabel.setText("");
        //  Check all the XML Tags to Extract and both of the Extract Options by default
        setDefaultRadioBtnConfigurations();
        
    }
    
    /**
     * To check all the XML Tags to Extract and both of the Extract Options by default
     */
    private void setDefaultRadioBtnConfigurations(){
        idRadioBtn.setSelected(true);
        dataLocatorRadioBtn.setSelected(true);
        xPathRadioBtn.setSelected(true);
        nameRadioBtn.setSelected(true);
        cssRadioBtn.setSelected(true);
        textRadioBtn.setSelected(true);
        isSelectedRadioBtn.setSelected(true);
        removeQuotRadioBtn.setSelected(true);
    }
    
    /**
     * To add the files which are selected.
     * @return file list
     */
    private File[] addFiles(){
        // set title for the file chooser
        fc.setDialogTitle("Choose a Input file");
        // selection mode
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // set enable the multi selection
        fc.setMultiSelectionEnabled(true);
        // take the clicked option of the file chooser
        int option = fc.showOpenDialog(this);    
        if(option == JFileChooser.APPROVE_OPTION) {
            // get the selected file list
            return fc.getSelectedFiles();
        }
        return null;
    }
    
    /**
     * To add all the files in the selected folder.
     * @return a file list
     */
    private File[] addAllInFolder(){
        // set title for the file chooser
        fc.setDialogTitle("Choose a folder to add all Input files");
        // selection mode
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        // set disable the multi selection
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // get all the files in the selected folder
            return fc.getSelectedFile().listFiles();
        }
        return null;
    }
    
    /**
     * To add extraction file output
     * @return the selected output file
     */
    private String selectAOutputFile(){
        // set title for the file chooser
        fc.setDialogTitle("Choose a output TXT file");
        // set the selection mode
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // set disable the multi selection
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // get the selected file
            return fc.getSelectedFile().getPath();
        }
        return null;
    }
    
    /**
     * 
     * @param filePath - path of the given file
     * @param ex - expected extentions separated by comma
     * @return whether the file is in expected extentions
     */
    private boolean isFileWithExtensions(String filePath, String exts){
        int i = filePath.lastIndexOf('.');
        // get all the given extensions
        String[] extensions = exts.split(",");
        if (i > 0 &&  i < filePath.length() - 1) {
            // take the extension
            String extension = filePath.substring(i + 1).toLowerCase();
            for (String ex : extensions) {
                // and check whether the extention is similar to the expected one 
                if (ex.equals(extension)) 
                    return true;
            }
        }
        return false;
    }
    
    /**
     * To extract given XML file
     * @param file - input file path
     * @return list of web element properties 
     */
    private ArrayList<WebElementProperties> extractXMLFile(String file){
        // crate an empty array list of web element properties 
        ArrayList<WebElementProperties> webelements = new ArrayList<>();
        try {
            // open the given xml file
            File fXmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            // create a document builder from the factory class relevent to that
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            // create an document from the opened xml file
            Document doc = dBuilder.parse(fXmlFile);
            // normalize the created document as a xml file
            doc.getDocumentElement().normalize();
            // read the web element name
            String webElementName = doc.getDocumentElement().getElementsByTagName("name").item(0).getTextContent();
            // take all the web element properties nodes as a node list
            NodeList nList = doc.getElementsByTagName("webElementProperties");
            
            // iterate through the node list
            for (int i = 0; i < nList.getLength(); i++) {
                // take the node
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    // create an element from the node
                    Element eElement = (Element) nNode;
                    // create an instance of webElementProperties
                    WebElementProperties wep = new WebElementProperties(webElementName, this.removeQuot);
                    // reading all the properties
                    wep.setIsSelected(Boolean.valueOf(eElement.getElementsByTagName("isSelected").item(0).getTextContent()));
                    wep.setMatchCondition(eElement.getElementsByTagName("matchCondition").item(0).getTextContent());
                    wep.setName(eElement.getElementsByTagName("name").item(0).getTextContent());
                    wep.setType(eElement.getElementsByTagName("type").item(0).getTextContent());
                    wep.setValue(eElement.getElementsByTagName("value").item(0).getTextContent());
                    // add the created webelement properties instance to the list
                    webelements.add(wep);
                }
            }
            
        } catch (Exception e) {
            this.errorsFound.add(Paths.get(file).getFileName().toString());
        }
        return webelements;
    }
    
    /**
     * To generate the comments for the extraction file
     * @return all the comments
     */
    private String generateComments(){
        String comment = "/*\n" +
        "* WebElements are identified by " + extractFormat.name + " annotation\n" +
        "*/\n";
        StringBuilder fileList = new StringBuilder();
        for (int i = 0; i < dlm.size(); i++) {
            fileList.append("\n// ").append(dlm.get(i));
        }
        fileList.append("\n\n");
       
        return comment + fileList; 
    }
    
    /**
     * To add all the selected tags to the list
     * @return the number of selected tags by the user
     */
    private int getAllSelectedTags(){
        this.selectedTags.clear();
        
        if (idRadioBtn.isSelected()) {
             selectedTags.add(idRadioBtn.getText().toLowerCase());
        }
        
        if (dataLocatorRadioBtn.isSelected()) {
             selectedTags.add(dataLocatorRadioBtn.getText().toLowerCase());
        }
        
        if (xPathRadioBtn.isSelected()) {
             selectedTags.add(xPathRadioBtn.getText().toLowerCase());
        }
        
        if (nameRadioBtn.isSelected()) {
             selectedTags.add(nameRadioBtn.getText().toLowerCase());
        }
        
        if (cssRadioBtn.isSelected()) {
             selectedTags.add(cssRadioBtn.getText().toLowerCase());
        }
        
        if (textRadioBtn.isSelected()) {
             selectedTags.add(textRadioBtn.getText().toLowerCase());
        }
        
        if (_dataRadioBtn.isSelected()) {
             selectedTags.add(_dataRadioBtn.getText().toLowerCase());
             selectedTags.add(dataLocatorRadioBtn.getText().toLowerCase());
        }
        return this.selectedTags.size();
    }
    
    /**
     * To filter the list according to the XML tags and extract 
     * option which are given by the user.
     * @param in - the array list of web element properties.
     * @return filtered list according to the user given configurations.
     */
    private ArrayList<WebElementProperties> filterWebElementProperties(ArrayList<WebElementProperties> in, String file){
        // create temporary array list of web element properties
        ArrayList<WebElementProperties> filtered = new ArrayList<>();
        // create temporary array list of no true elements
        NoTrue notrue = new NoTrue(file);
        int noTrueCount = 0;
        int knownCount = 0;
        for (WebElementProperties wep : in) {
            // iterate through the array list
            String name = (wep.startsWithData_() && !wep.isDataLocator()) ? "data-" : wep.name;
            if(this.selectedTags.contains(name.toLowerCase().trim())){
                knownCount++;
                if(!wep.isIsSelected()){
                    noTrueCount++;
                    notrue.addElement(wep);
                }
                // check the condition given by the user and filter them
                if(!this.isSelectedOnly || wep.isIsSelected())
                    filtered.add(wep);
            }else{
                // unknowns
                if(wep.isIsSelected())
                    unknowns.add(new Unknown(file, wep));
            }
        }
        //
        if((noTrueCount == knownCount) && noTrueCount != 0){
            // append to noTrue file.
            writeNoTrues(notrue);
        }
        return filtered;
    }
    
    private void writeUnknowns(){
        String filePath = Paths.get(this.extractionFile.getPath()).getParent().toString() + "\\Unknown.txt";
        File unknownFile = new File(filePath);
        FileWriter fr = null;
        BufferedWriter br = null;
        try {
            fr = new FileWriter(unknownFile, true);
            br = new BufferedWriter(fr);
            
            for (Unknown u : unknowns) {
                br.write((u.getWebElementProp().name + " - " + u.getFile() + "\n"));
            }

        } catch (IOException ex) {
            System.out.println(ex);
            return;
        }
        try {
            // close the file
            br.close();
            fr.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    private void writeNoTrues(NoTrue notrue){
        String filePath = Paths.get(this.extractionFile.getPath()).getParent().toString() + "\\NoTrue.txt";
        File unknownFile = new File(filePath);
        FileWriter fr = null;
        BufferedWriter br = null;
        try {
            fr = new FileWriter(unknownFile, true);
            br = new BufferedWriter(fr);
            // write comments
            br.write(("// " + notrue.getFile() + "\n\n")); 
            for (WebElementProperties wep : notrue.getElementList()) {
                br.write((wep.toString()));
            }

        } catch (IOException ex) {
            System.out.println(ex);
            return;
        }
        try {
            // close the file
            br.close();
            fr.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        xmlFilesList = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        addXMLBtn = new javax.swing.JButton();
        addALLBtn = new javax.swing.JButton();
        clearListBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        outputTxt = new javax.swing.JTextField();
        browseBtn = new javax.swing.JButton();
        startExtractionBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        idRadioBtn = new javax.swing.JRadioButton();
        xPathRadioBtn = new javax.swing.JRadioButton();
        nameRadioBtn = new javax.swing.JRadioButton();
        cssRadioBtn = new javax.swing.JRadioButton();
        textRadioBtn = new javax.swing.JRadioButton();
        dataLocatorRadioBtn = new javax.swing.JRadioButton();
        _dataRadioBtn = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        isSelectedRadioBtn = new javax.swing.JRadioButton();
        removeQuotRadioBtn = new javax.swing.JRadioButton();
        progressLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        extractFormatComboBox = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("XML Extract Tool");
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("XML Extract Tool");

        jScrollPane1.setViewportView(xmlFilesList);

        jLabel2.setText("Input Files");

        addXMLBtn.setText("Add Input Files");
        addXMLBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addXMLBtnActionPerformed(evt);
            }
        });

        addALLBtn.setText("Add All In Folder");
        addALLBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addALLBtnActionPerformed(evt);
            }
        });

        clearListBtn.setText("Clear List");
        clearListBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearListBtnActionPerformed(evt);
            }
        });

        jLabel3.setText("Extraction File");

        browseBtn.setText("Browse");
        browseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseBtnActionPerformed(evt);
            }
        });

        startExtractionBtn.setText("Start Extracting");
        startExtractionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startExtractionBtnActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("XML Tags To Extract"));

        idRadioBtn.setText("ID");

        xPathRadioBtn.setText("XPath");

        nameRadioBtn.setText("Name");

        cssRadioBtn.setText("CSS");

        textRadioBtn.setText("Text");

        dataLocatorRadioBtn.setText("Data-Locator");
        dataLocatorRadioBtn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dataLocatorRadioBtnItemStateChanged(evt);
            }
        });

        _dataRadioBtn.setText("Data-");
        _dataRadioBtn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                _dataRadioBtnItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(idRadioBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(xPathRadioBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                        .addComponent(nameRadioBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cssRadioBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(textRadioBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(dataLocatorRadioBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(66, 66, 66)
                        .addComponent(_dataRadioBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(idRadioBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dataLocatorRadioBtn)
                    .addComponent(_dataRadioBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xPathRadioBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameRadioBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cssRadioBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textRadioBtn)
                .addGap(12, 12, 12))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Extract Options"));

        isSelectedRadioBtn.setText("IsSelected Only");

        removeQuotRadioBtn.setText("Remove &quot;");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(isSelectedRadioBtn)
                    .addComponent(removeQuotRadioBtn))
                .addContainerGap(137, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(isSelectedRadioBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeQuotRadioBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        progressLabel.setText("Extracting...");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Extract Format"));

        extractFormatComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "---Select---", "@FindBy", "By" }));
        extractFormatComboBox.setFocusable(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(extractFormatComboBox, 0, 224, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(extractFormatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 773, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 563, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addXMLBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addALLBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(clearListBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(10, 10, 10)
                        .addComponent(outputTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(browseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(startExtractionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(progressLabel)))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel1)
                .addGap(12, 12, 12)
                .addComponent(jLabel2)
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(addXMLBtn)
                        .addGap(18, 18, 18)
                        .addComponent(addALLBtn)
                        .addGap(18, 18, 18)
                        .addComponent(clearListBtn)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(outputTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(browseBtn))
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(startExtractionBtn)
                        .addGap(6, 6, 6)
                        .addComponent(progressLabel)))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addXMLBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addXMLBtnActionPerformed
        // choose all the files in the selected folder
        File[] allFiles = addFiles();
        if (allFiles != null) {
            // add to the list 
            for (File file : allFiles) {
                // take the path of the file
                String filePath = file.getPath();
                // check whether it is a xml,rs,ts file
                if (isFileWithExtensions(filePath, "xml,txt,rs")) {
                    dlm.addElement(file);
                }
            }
        }
    }//GEN-LAST:event_addXMLBtnActionPerformed

    private void startExtractionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startExtractionBtnActionPerformed
        progressLabel.setText("");
        if(dlm.isEmpty()){ // check whether the file list is empty
            JOptionPane.showMessageDialog(null, "No XML files added.");
        } else { // otherwise
            String outFile = outputTxt.getText(); // take the output file path
            if(outFile.isEmpty()){ // check whether the input field of output file is empty
                JOptionPane.showMessageDialog(null, "Please, add a TXT file for output.");
            }else{
                // check wether the user select at least one tag to extract
                if (getAllSelectedTags() == 0) {
                    JOptionPane.showMessageDialog(null, "Please, select atleast one XML tag to extract.");
                }else if(extractFormatComboBox.getSelectedIndex() == 0){
                    JOptionPane.showMessageDialog(null, "Please, select an extract format.");
                }else{
                    
                    int selectedFormatIndex = extractFormatComboBox.getSelectedIndex();
                    extractFormat = selectedFormatIndex == 1 ? ExtractFormat.FIND_BY : ExtractFormat.BY;
                    this.isSelectedOnly = isSelectedRadioBtn.isSelected();
                    this.removeQuot = removeQuotRadioBtn.isSelected();
                    
                    // extraction process
                    boolean append = false;
                    this.extractionFile = new File(outputTxt.getText().trim());
                    if(this.extractionFile.exists()){
                        Object[] options = { "Append", "Overwrite", "Cancel" };
                        int result = JOptionPane.showOptionDialog(null, "Extraction file already exists. What do you need to do?", 
                        "Confirmation",JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
                        switch(result){
                            case 0:
                                append = true;
                                break;
                            case 1:
                                append = false;
                                break;
                            default:
                                return;
                        }
                    }else{
                        append = false;
                    }
                    
                    FileWriter fr = null;
                    BufferedWriter br = null;
                    try {
                        fr = new FileWriter(this.extractionFile, append);
                        br = new BufferedWriter(fr);
                        
                        progressLabel.setText("Extracting...");
                        for (int i = 0; i < dlm.size(); i++) {
                            // get one-by-one from the file list
                            String file = dlm.get(i).toString();
                            // extract the tags and write them to the output file
                            // open the output file
                            if(i == 0)
                                br.write(generateComments());
                            // filter the web elements array list according the given configurations 
                            ArrayList<WebElementProperties> filteredWeps = filterWebElementProperties(extractXMLFile(file), file);
                            for (WebElementProperties webelement : filteredWeps) {
                                // write the extracted details to the file
                                br.write(webelement.toString());
                            }
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Cannot find the output file or file is opened by another program.");
                        return;
                    }
                    try {
                        // close the file
                        br.close();
                        fr.close();
                    } catch (IOException ex) {
                        System.out.println(ex);
                        return;
                    }
                    progressLabel.setText("Completed.");
                    
                    // Attach the file names with extraction errors to the popup message 
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html>");
                    sb.append("<ul>");
                    for (String error : this.errorsFound) {
                        sb.append("<li>").append(error).append("</li>");
                    }
                    sb.append("</ul>");
                    sb.append("</html>");
                    
                    // write unknowns
                    if(unknowns.size() > 0)
                        this.writeUnknowns();
                    
                    JOptionPane.showMessageDialog(null, "<html><b>Extraction Process " 
                            + (errorsFound.isEmpty()? "Completed":"Warning")
                            + ".</b></html>"
                            + (errorsFound.isEmpty()? "" : ("\nThere are some issues with the following XML files." 
                            + " Please check them.\n" + sb.toString())));
                    progressLabel.setText("");
                    //  Check all the XML Tags to Extract and both of the Extract Options by default
                    // setDefaultRadioBtnConfigurations();
                    // clear the errorsFound list
                    this.errorsFound.clear();
                    this.unknowns.clear();
                }
            }
        }
    }//GEN-LAST:event_startExtractionBtnActionPerformed

    private void addALLBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addALLBtnActionPerformed
        // choose all the files in the selected folder
        File[] allFiles = addAllInFolder();
        if (allFiles != null) {
            // add to the list 
            for (File file : allFiles) {
                // iterate through the selected files
                String filePath = file.getPath();
                // check whether it is a xml,txt,rs file
                if(file.isFile() && !file.isHidden() && isFileWithExtensions(filePath, "xml,rs,ts"))
                    dlm.addElement(filePath);
            }
        }
    }//GEN-LAST:event_addALLBtnActionPerformed

    private void clearListBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearListBtnActionPerformed
        dlm.clear();
        progressLabel.setText("");
    }//GEN-LAST:event_clearListBtnActionPerformed

    private void browseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseBtnActionPerformed
        // choose an output file
        String file = selectAOutputFile();
        if (file != null) {
            // check whether it is a txt file
            // add to txt field
            if (isFileWithExtensions(file, "txt")) {
                outputTxt.setText(file);
            }
        }
    }//GEN-LAST:event_browseBtnActionPerformed

    private void dataLocatorRadioBtnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dataLocatorRadioBtnItemStateChanged
        if(dataLocatorRadioBtn.isSelected())
            _dataRadioBtn.setSelected(false);
    }//GEN-LAST:event_dataLocatorRadioBtnItemStateChanged

    private void _dataRadioBtnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event__dataRadioBtnItemStateChanged
        if(_dataRadioBtn.isSelected())
            dataLocatorRadioBtn.setSelected(false);
    }//GEN-LAST:event__dataRadioBtnItemStateChanged

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(XMLExtractTool.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(XMLExtractTool.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(XMLExtractTool.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(XMLExtractTool.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new XMLExtractTool().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton _dataRadioBtn;
    private javax.swing.JButton addALLBtn;
    private javax.swing.JButton addXMLBtn;
    private javax.swing.JButton browseBtn;
    private javax.swing.JButton clearListBtn;
    private javax.swing.JRadioButton cssRadioBtn;
    private javax.swing.JRadioButton dataLocatorRadioBtn;
    private javax.swing.JComboBox extractFormatComboBox;
    private javax.swing.JRadioButton idRadioBtn;
    private javax.swing.JRadioButton isSelectedRadioBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton nameRadioBtn;
    private javax.swing.JTextField outputTxt;
    private javax.swing.JLabel progressLabel;
    private javax.swing.JRadioButton removeQuotRadioBtn;
    private javax.swing.JButton startExtractionBtn;
    private javax.swing.JRadioButton textRadioBtn;
    private javax.swing.JRadioButton xPathRadioBtn;
    private javax.swing.JList xmlFilesList;
    // End of variables declaration//GEN-END:variables
}
