package heatmap.parser;

import heatmap.model.MouseEvent;
import heatmap.model.SessionMetadata;
import heatmap.validator.SessionDataException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses XML data from ide_tracking.xml files.
 * Extracts mouse events and session metadata.
 */
public class XMLParser {
    private static final Pattern SCREEN_SIZE_PATTERN = Pattern.compile("\\((\\d+),(\\d+)\\)");

    /**
     * Parses mouse events from the IDE tracking XML file.
     *
     * @param xmlFilePath Path to ide_tracking.xml
     * @return List of mouse events
     * @throws SessionDataException if parsing fails
     */
    public List<MouseEvent> parseMouseEvents(String xmlFilePath) throws SessionDataException {
        List<MouseEvent> mouseEvents = new ArrayList<>();

        try {
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // Get all <mouse> elements
            NodeList mouseNodes = document.getElementsByTagName("mouse");

            for (int i = 0; i < mouseNodes.getLength(); i++) {
                Element mouseElement = (Element) mouseNodes.item(i);

                try {
                    String id = mouseElement.getAttribute("id");
                    long timestamp = Long.parseLong(mouseElement.getAttribute("timestamp"));
                    String path = mouseElement.getAttribute("path");
                    int x = Integer.parseInt(mouseElement.getAttribute("x"));
                    int y = Integer.parseInt(mouseElement.getAttribute("y"));

                    MouseEvent event = new MouseEvent(id, timestamp, path, x, y);
                    mouseEvents.add(event);
                } catch (NumberFormatException e) {
                    // Skip malformed mouse events
                    System.err.println("Warning: Skipping malformed mouse event at index " + i);
                }
            }

        } catch (Exception e) {
            throw new SessionDataException("Failed to parse mouse events from XML: " + e.getMessage(), e);
        }

        return mouseEvents;
    }

    /**
     * Parses session metadata from the environment section of the XML file.
     *
     * @param xmlFilePath Path to ide_tracking.xml
     * @return Session metadata
     * @throws SessionDataException if parsing fails
     */
    public SessionMetadata parseEnvironmentData(String xmlFilePath) throws SessionDataException {
        try {
            File xmlFile = new File(xmlFilePath);
            String sessionPath = xmlFile.getParentFile().getAbsolutePath();
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // Get the <environment> element
            NodeList envNodes = document.getElementsByTagName("environment");
            if (envNodes.getLength() == 0) {
                throw new SessionDataException("No <environment> element found in XML");
            }

            Element envElement = (Element) envNodes.item(0);

            // Parse screen size
            String screenSizeStr = envElement.getAttribute("screen_size");
            Dimension screenSize = parseScreenSize(screenSizeStr);

            // Parse scaling factors
            double scaleX = Double.parseDouble(envElement.getAttribute("scale_x"));
            double scaleY = Double.parseDouble(envElement.getAttribute("scale_y"));

            // Frame rate is typically 4 (from ScreenRecorder), but we'll default to 4
            // This could be made configurable later
            int frameRate = 4;

            SessionMetadata metadata = new SessionMetadata(sessionPath, screenSize, scaleX, scaleY, frameRate);

            // Set additional metadata
            metadata.setIdeName(envElement.getAttribute("ide_name"));
            metadata.setIdeVersion(envElement.getAttribute("ide_version"));
            metadata.setOsName(envElement.getAttribute("os_name"));
            metadata.setProjectName(envElement.getAttribute("project_name"));

            return metadata;

        } catch (Exception e) {
            throw new SessionDataException("Failed to parse environment data from XML: " + e.getMessage(), e);
        }
    }

    /**
     * Parses screen size string in format "(width,height)".
     *
     * @param screenSizeStr Screen size string
     * @return Dimension object
     * @throws SessionDataException if format is invalid
     */
    private Dimension parseScreenSize(String screenSizeStr) throws SessionDataException {
        Matcher matcher = SCREEN_SIZE_PATTERN.matcher(screenSizeStr);
        if (matcher.matches()) {
            int width = Integer.parseInt(matcher.group(1));
            int height = Integer.parseInt(matcher.group(2));
            return new Dimension(width, height);
        } else {
            throw new SessionDataException("Invalid screen size format: " + screenSizeStr);
        }
    }
}