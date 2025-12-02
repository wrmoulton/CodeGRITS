package components;

import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.language.CodeLanguageConsole;
import com.intellij.util.ui.JBUI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.StringWriter;

/**
 * This panel displays XML content with syntax highlighting and formatting.
 * It provides a read-only view of XML documents with pretty-printing.
 */
public class XMLPreviewPanel extends JPanel {

    private final JTextArea xmlTextArea;
    private final JLabel statusLabel;

    public XMLPreviewPanel() {
        setLayout(new BorderLayout());
        setBorder(JBUI.Borders.empty(8));

        // Header with status
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("No XML loaded");
        headerPanel.add(statusLabel);
        add(headerPanel, BorderLayout.NORTH);

        // XML display area
        xmlTextArea = new JTextArea();
        xmlTextArea.setEditable(false);
        xmlTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        xmlTextArea.setLineWrap(false);
        xmlTextArea.setWrapStyleWord(true);

        JBScrollPane scrollPane = new JBScrollPane(xmlTextArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton copyButton = new JButton("Copy");
        copyButton.addActionListener(e -> copyToClipboard());
        controlPanel.add(copyButton);

        JButton formatButton = new JButton("Auto-Format");
        formatButton.addActionListener(e -> formatXML());
        controlPanel.add(formatButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    /**
     * Display a Document object in the preview panel.
     *
     * @param document The XML document to display.
     */
    public void displayDocument(Document document) {
        if (document == null) {
            xmlTextArea.setText("No document to display");
            statusLabel.setText("Error: Document is null");
            return;
        }

        try {
            String xmlString = formatXMLDocument(document);
            xmlTextArea.setText(xmlString);
            xmlTextArea.setCaretPosition(0);
            int lineCount = xmlTextArea.getLineCount();
            statusLabel.setText(String.format("XML loaded: %d lines", lineCount));
        } catch (Exception e) {
            xmlTextArea.setText("Error formatting XML: " + e.getMessage());
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Display raw XML string in the preview panel.
     *
     * @param xmlString The XML string to display.
     */
    public void displayXMLString(String xmlString) {
        if (xmlString == null || xmlString.isEmpty()) {
            xmlTextArea.setText("No XML content to display");
            statusLabel.setText("Empty content");
            return;
        }

        try {
            xmlTextArea.setText(xmlString);
            xmlTextArea.setCaretPosition(0);
            int lineCount = xmlTextArea.getLineCount();
            statusLabel.setText(String.format("XML loaded: %d lines", lineCount));
        } catch (Exception e) {
            xmlTextArea.setText("Error: " + e.getMessage());
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Format a Document object into a pretty-printed string.
     *
     * @param document The XML document to format.
     * @return Formatted XML string.
     */
    private String formatXMLDocument(Document document) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        DOMSource source = new DOMSource(document);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        transformer.transform(source, result);
        
        return stringWriter.toString();
    }

    /**
     * Format the current XML content for better readability.
     */
    private void formatXML() {
        String currentXML = xmlTextArea.getText();
        if (currentXML.isEmpty()) {
            statusLabel.setText("No content to format");
            return;
        }

        try {
            // Parse and reformat
            javax.xml.parsers.DocumentBuilderFactory dbf = 
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new java.io.ByteArrayInputStream(currentXML.getBytes()));
            
            String formatted = formatXMLDocument(doc);
            xmlTextArea.setText(formatted);
            xmlTextArea.setCaretPosition(0);
            statusLabel.setText("XML formatted successfully");
        } catch (Exception e) {
            statusLabel.setText("Format error: " + e.getMessage());
        }
    }

    /**
     * Copy the XML content to clipboard.
     */
    private void copyToClipboard() {
        String text = xmlTextArea.getText();
        if (text.isEmpty()) {
            statusLabel.setText("No content to copy");
            return;
        }

        StringSelection stringSelection = new StringSelection(text);
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        statusLabel.setText("Copied to clipboard");
    }

    /**
     * Get the text content currently displayed.
     *
     * @return The XML text content.
     */
    public String getXMLText() {
        return xmlTextArea.getText();
    }

    /**
     * Clear the displayed content.
     */
    public void clear() {
        xmlTextArea.setText("");
        statusLabel.setText("Cleared");
    }
}
