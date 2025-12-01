package components;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.JBUI;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Extended XML preview panel with file browsing capabilities.
 * Allows users to browse and select XML files to preview.
 */
public class XMLFilePreviewPanel extends JPanel {

    private final XMLPreviewPanel previewPanel;
    private final JLabel filePathLabel;
    private final Project project;

    public XMLFilePreviewPanel(Project project) {
        this.project = project;
        setLayout(new BorderLayout());
        setBorder(JBUI.Borders.empty(8));

        // Top panel with file selection controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton browseButton = new JButton("Browse XML File");
        browseButton.addActionListener(e -> browseXMLFile());
        topPanel.add(browseButton);

        filePathLabel = new JLabel("No file selected");
        topPanel.add(filePathLabel);

        add(topPanel, BorderLayout.NORTH);

        // Preview panel
        previewPanel = new XMLPreviewPanel();
        add(previewPanel, BorderLayout.CENTER);
    }

    /**
     * Open a file browser dialog to select an XML file.
     */
    private void browseXMLFile() {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false) {
            @Override
            public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                return file.isDirectory() || file.getName().endsWith(".xml");
            }
        };
        descriptor.setTitle("Select XML File");
        descriptor.setDescription("Choose an XML file to preview");

        VirtualFile selected = FileChooser.chooseFile(descriptor, project, null);
        if (selected != null && !selected.isDirectory()) {
            loadXMLFile(selected.getPath());
        }
    }

    /**
     * Load and display an XML file.
     *
     * @param filePath The path to the XML file.
     */
    public void loadXMLFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            previewPanel.displayXMLString("Error: File not found: " + filePath);
            filePathLabel.setText("File not found");
            return;
        }

        try {
            javax.xml.parsers.DocumentBuilderFactory dbf = 
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            
            previewPanel.displayDocument(doc);
            filePathLabel.setText("Loaded: " + filePath);
        } catch (Exception e) {
            previewPanel.displayXMLString("Error loading file:\n" + e.getMessage());
            filePathLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Get the preview panel.
     *
     * @return The preview panel.
     */
    public XMLPreviewPanel getPreviewPanel() {
        return previewPanel;
    }
}
