# XML Preview Quick Start Guide

## Overview
The XML Preview feature allows you to view and analyze CodeGRITS XML output directly in the IDE. This guide will get you started in 5 minutes.

## Installation
The XML Preview feature is included in CodeGRITS. No additional installation needed.

## Quick Start

### Step 1: Open the Preview Window
1. In IntelliJ IDEA (or any JetBrains IDE), go to **Tools** menu
2. Click **XML Preview**
3. A preview window will open with three tabs: IDE Tracking, Eye Tracking, and Combined View

### Step 2: Browse and Load XML Files
1. Click the **Browse XML File** button
2. Navigate to your CodeGRITS tracking session folder
3. Select an XML file (e.g., `ide_tracking.xml` or `eye_tracking.xml`)
4. The XML will be automatically loaded and displayed

### Step 3: View the Data
- The XML will be formatted with proper indentation
- View line count in the status bar
- Scroll through the content to explore the data

## Common Tasks

### Viewing IDE Tracking Data
```
File → Browse → [Session Folder] → ide_tracking.xml
```
Shows all IDE interactions: mouse clicks, keystrokes, file changes, etc.

### Viewing Eye Tracking Data
```
File → Browse → [Session Folder] → eye_tracking.xml
```
Shows all eye gaze events and fixations.

### Copying XML to Clipboard
1. Load the XML file
2. Click the **Copy** button
3. XML is copied to clipboard for external analysis

### Formatting Messy XML
1. Load the XML file
2. Click **Auto-Format**
3. XML will be reformatted with consistent indentation

### Finding Your Session Data
CodeGRITS saves tracking data in:
```
[Project Root] / [Timestamp] / ide_tracking.xml
[Project Root] / [Timestamp] / eye_tracking.xml
```

Example:
```
MyProject/1701518400123/ide_tracking.xml
MyProject/1701518400123/eye_tracking.xml
```

## Features

### Real-Time Status Display
When tracking is active:
- Status bar shows current element count
- Click status widget for quick preview
- Updates automatically as data is recorded

### Tabbed Interface
- **IDE Tracking Tab** - View IDE interaction data
- **Eye Tracking Tab** - View eye gaze data
- **Combined View** - View mixed content or other XML

### Syntax Highlighting
- Properly formatted XML with indentation
- Easy to read hierarchical structure
- Fixed-width font for alignment

### Copy and Export
- Copy button for quick clipboard access
- Export formatted XML for external tools
- Maintain formatting when copying

## Keyboard Shortcuts
(Can be customized in Settings)

| Action | Shortcut |
|--------|----------|
| Open Preview | `Alt+T` then `X` (Tools → XML Preview) |
| Focus on Tab | `Ctrl+Tab` |
| Scroll Down | `Page Down` |
| Scroll Up | `Page Up` |
| Copy | `Ctrl+C` or button click |

## Tips & Tricks

### Tip 1: Quick Preview Popup
During tracking, click the status bar widget to see a quick preview of current data without opening the full window.

### Tip 2: Batch Loading
Open multiple XML files from the same session to compare data:
1. Open preview window
2. Click "Browse XML File"
3. Select first file
4. Use file browser or tabs to switch files

### Tip 3: External Analysis
Copy XML content and paste into your favorite XML editor for advanced analysis:
1. Click "Copy" button
2. Open your XML editor (VS Code, XMLSpy, etc.)
3. Paste content for analysis

### Tip 4: Session Organization
Keep session folders organized for easy access:
```
Project/
├── 2024-01-15-session1/
│   ├── ide_tracking.xml
│   ├── eye_tracking.xml
│   └── screen_recording.mp4
└── 2024-01-15-session2/
    ├── ide_tracking.xml
    ├── eye_tracking.xml
    └── screen_recording.mp4
```

### Tip 5: Validating Data
Before using XML for analysis:
1. Load the file in preview
2. Check for error messages
3. Verify data is complete and well-formed

## Troubleshooting

### Preview Window Won't Open
**Solution:**
1. Ensure a project is open in the IDE
2. Check that the plugin is loaded (Settings → Plugins → CodeGRITS)
3. Restart IDE if needed

### XML File Not Loading
**Solution:**
1. Verify file exists and is readable
2. Check XML file format is correct
3. Look for error message in status bar
4. Try opening file in system file browser to confirm access

### Data Appears Incomplete
**Solution:**
1. Ensure tracking session was completed (click "Stop Tracking")
2. Check that XML file was written to disk
3. Try closing and reopening the preview window

### Large Files Load Slowly
**Solution:**
1. Files larger than 100MB may take time to parse
2. Consider splitting large sessions into multiple sessions
3. Use external XML tools for very large files (> 1GB)

### Status Widget Not Showing
**Solution:**
1. Check if tracking is active (status bar shows "Recording")
2. Verify widget is enabled in status bar settings
3. Try scrolling status bar to find widget

## Common XML Structure

### IDE Tracking XML
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<ide_tracking>
    <environment>
        <!-- System and IDE information -->
    </environment>
    <actions>
        <!-- Action events -->
    </actions>
    <mouses>
        <!-- Mouse events -->
    </mouses>
    <typings>
        <!-- Keyboard events -->
    </typings>
    <carets>
        <!-- Cursor position changes -->
    </carets>
    <selections>
        <!-- Text selection events -->
    </selections>
    <files>
        <!-- File operations -->
    </files>
</ide_tracking>
```

### Eye Tracking XML
```xml
<?xml version="1.0" encoding="UTF-8"?>
<eye_tracking>
    <environment>
        <!-- System and device information -->
    </environment>
    <gazes>
        <!-- Gaze data points -->
    </gazes>
    <fixations>
        <!-- Eye fixation events -->
    </fixations>
    <saccades>
        <!-- Rapid eye movements -->
    </saccades>
</eye_tracking>
```

## Data Analysis Workflow

### 1. Start Tracking
- Configure CodeGRITS settings
- Click "Start Tracking"
- Perform your programming task

### 2. Stop Tracking
- Click "Stop Tracking"
- XML files are written to disk
- Session folder is created with timestamp

### 3. Open Preview
- Go to Tools → XML Preview
- Browse to session folder
- Load XML files

### 4. Analyze Data
- Review structure and content
- Copy data for external analysis
- Export to analysis tools (Python, R, etc.)

### 5. Export and Process
- Use data for research analysis
- Generate visualizations
- Create reports

## Advanced Usage

### Programmatic Access
For plugin developers integrating with CodeGRITS:

```java
// Get the preview handler
XMLPreviewDataHandler handler = XMLPreviewDataHandler.getInstance();

// Get current tracking documents
Document ideDoc = handler.getCurrentIDEDocument();
Document eyeDoc = handler.getCurrentEyeDocument();
```

### Custom Analysis
Extend the preview functionality:

```java
// Parse XML file
Document doc = XMLValidator.parseXMLFile(filePath);

// Validate
if (XMLValidator.validateXMLFile(filePath)) {
    // Process...
}

// Convert to string
String xml = XMLValidator.documentToFormattedString(doc);
```

## Integration with Other Tools

### Python Analysis
```python
import xml.etree.ElementTree as ET

# Load XML from CodeGRITS preview
root = ET.parse('ide_tracking.xml').getroot()

# Analyze data
for action in root.findall('.//action'):
    print(action.get('timestamp'), action.get('type'))
```

### R Analysis
```r
# Load XML using xml2 package
library(xml2)
doc <- read_xml("ide_tracking.xml")

# Extract and analyze
actions <- xml_find_all(doc, ".//action")
```

## Performance Guidelines

| Action | Expected Time |
|--------|----------------|
| Open Preview Window | < 1 second |
| Browse Files | < 2 seconds |
| Load Small XML (<1MB) | < 1 second |
| Load Medium XML (1-10MB) | 1-5 seconds |
| Load Large XML (10-100MB) | 5-30 seconds |
| Format XML | < 5 seconds |
| Copy to Clipboard | < 1 second |

## Getting Help

### Online Resources
- CodeGRITS Website: https://codegrits.github.io/CodeGRITS/
- GitHub Repository: https://github.com/codegrits/CodeGRITS
- JavaDoc Documentation: https://codegrits.github.io/CodeGRITS/docs/

### Reporting Issues
Found a bug? Please report it on GitHub:
1. Go to Issues tab
2. Click "New Issue"
3. Describe the problem with details
4. Include XML file if possible (anonymized)

### Community Support
- Ask questions on GitHub Discussions
- Join our research community
- Share your analysis workflows

## What's Next?

After mastering the basics:
1. **Learn Advanced Filtering** - Filter data by time, action type
2. **Explore Analysis Tools** - Use Python/R for statistical analysis
3. **Generate Visualizations** - Create heatmaps, charts
4. **Contribute to CodeGRITS** - Help improve the project

---

**Last Updated:** December 2024  
**Version:** 1.0  
**Compatibility:** CodeGRITS 0.3.2+
