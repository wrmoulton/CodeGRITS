package utils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Utility class for XML validation, parsing, and formatting operations.
 */
public class XMLValidator {

    /**
     * Validate an XML file for well-formedness.
     *
     * @param filePath The path to the XML file.
     * @return true if the file is valid XML, false otherwise.
     */
    public static boolean validateXMLFile(String filePath) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.parse(new File(filePath));
            return true;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return false;
        }
    }

    /**
     * Parse an XML file and return a Document object.
     *
     * @param filePath The path to the XML file.
     * @return The parsed Document, or null if parsing fails.
     */
    public static Document parseXMLFile(String filePath) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(new File(filePath));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return null;
        }
    }

    /**
     * Parse an XML string and return a Document object.
     *
     * @param xmlString The XML string to parse.
     * @return The parsed Document, or null if parsing fails.
     */
    public static Document parseXMLString(String xmlString) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(new java.io.ByteArrayInputStream(xmlString.getBytes()));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return null;
        }
    }

    /**
     * Convert a Document object to a formatted XML string with indentation.
     *
     * @param document The XML document.
     * @return Formatted XML string.
     * @throws TransformerException if transformation fails.
     */
    public static String documentToFormattedString(Document document) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(sw));
        return sw.toString();
    }

    /**
     * Convert a Document object to an unformatted XML string.
     *
     * @param document The XML document.
     * @return XML string without formatting.
     * @throws TransformerException if transformation fails.
     */
    public static String documentToString(Document document) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        
        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(sw));
        return sw.toString();
    }

    /**
     * Get validation error message for an XML file.
     *
     * @param filePath The path to the XML file.
     * @return Error message, or empty string if valid.
     */
    public static String getValidationErrorMessage(String filePath) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.parse(new File(filePath));
            return "";
        } catch (ParserConfigurationException e) {
            return "Parser configuration error: " + e.getMessage();
        } catch (SAXException e) {
            return "XML parsing error: " + e.getMessage();
        } catch (IOException e) {
            return "File IO error: " + e.getMessage();
        }
    }

    /**
     * Check if a string is valid XML.
     *
     * @param xmlString The XML string to validate.
     * @return true if valid, false otherwise.
     */
    public static boolean isValidXML(String xmlString) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.parse(new java.io.ByteArrayInputStream(xmlString.getBytes()));
            return true;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return false;
        }
    }

    /**
     * Get line count of formatted XML.
     *
     * @param document The XML document.
     * @return Number of lines.
     */
    public static int getLineCount(Document document) {
        try {
            String formatted = documentToFormattedString(document);
            return formatted.split("\n").length;
        } catch (TransformerException e) {
            return 0;
        }
    }
}
