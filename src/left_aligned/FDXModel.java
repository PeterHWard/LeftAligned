/* File Name : FDXModel.java */

package left_aligned;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class FDXModel {
    public Document document;
    private Element content;
    
    public FDXModel() {
        this(GlobalConstants.FDX_TEMPLATE_PATH);
    }
    
    public FDXModel(String fdxFilePath) {
    	File fdxFile;
        try {
        	 fdxFile = new File(fdxFilePath);
        
             DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
             DocumentBuilder builder = factory.newDocumentBuilder();
        
             document = builder.parse(fdxFile);
             
        } catch (Exception e) {
            e.printStackTrace(); // FIXME - deal with malformed FDX file
        }
        
        content = (Element) document.getElementsByTagName("Content").item(0);        
    }
    /*
    http://www.tutorialspoint.com/java_xml/java_dom_create_document.htm
    
    <Content>
        <Paragraph Type=<elementType>>
            <Text Style=<style>>
            </Text>
        </Paragraph>
    </Content>
    */
    
    public void appendElement(ScriptElement scrptElem) {
    	assert scrptElem.type != null;// FIXME
        Element paraElem = document.createElement("Paragraph");
        Attr typeAttr = document.createAttribute("Type");
        typeAttr.setValue(scrptElem.type);
        paraElem.setAttributeNode(typeAttr);
        
        for (Text textRun : scrptElem.getTextRuns()) {
            Element textElem = document.createElement("Text");
            
            if (textRun.getStyle() != null && textRun.getStyle().length() > 0) {
            	Attr styleAttr = document.createAttribute("Style");
            	styleAttr.setValue(textRun.getStyle());
            	textElem.setAttributeNode(styleAttr);
            }
            
            textElem.setTextContent(textRun.getTextContent());
            
            paraElem.appendChild(textElem);
        }
        
        content.appendChild(paraElem);
    }
}