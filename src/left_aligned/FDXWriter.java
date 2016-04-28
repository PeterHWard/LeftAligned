/* File Name : FDXModel.java */

package left_aligned;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.util.ArrayList;

public final class FDXWriter implements IDocumentWriter {
	private ScriptDocument sDocument;
	private Document fDocument;
    
    public FDXWriter(ScriptDocument sDocument) {
    	this.sDocument = sDocument;
    	
		try {
			File fdxTemplateFile = new File(GlobalConstants.FDX_TEMPLATE_PATH);
	        
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        fDocument = builder.parse(fdxTemplateFile);
	  
		} catch (Exception exp) {
			exp.printStackTrace();
		}
    }
    
    @Override
    public boolean write(String fdxFilePath) {
    	return write(new File(fdxFilePath));
    }
    
    @Override
    public boolean write(File fdxFile) {
    	build();
    	try {
    		TransformerFactory txf = TransformerFactory.newInstance();
    		Transformer tx = txf.newTransformer();
    		DOMSource src = new DOMSource(fDocument); 
    		// FIXME - deal with bad fdxFilePath
            StreamResult res = new StreamResult(fdxFile);
            tx.transform(src, res);
    	} catch (Exception exp) {
			exp.printStackTrace();
			return false;
		} 	
    	
        return true;
    }

	private void build() {
		ArrayList<SceneGroup> shotGroups = sDocument.getMembers();
        
        for (SceneGroup sGroup : shotGroups) {    
            for (ElementGroup eGroup : sGroup.getMembers()) {                
                for (ScriptElement sElement : eGroup.getMembers())
                appendElement(sElement);
            }
        }
	}
    
    private void appendElement(ScriptElement scrptElem) {
    	/*
         *	
	     *  <Content>
	     *       <Paragraph Alignment=<alignment> Type=<elementType>>
	     *           <Text Style=<style>>
	     *           </Text>
	     *       </Paragraph>
	     *   </Content>
	     *   
         */
    	Element content = (Element) fDocument.getElementsByTagName("Content").item(0); 
        Element paraElem = fDocument.createElement("Paragraph");
        Attr typeAttr = fDocument.createAttribute("Type");
        typeAttr.setValue(scrptElem.type);
        paraElem.setAttributeNode(typeAttr);
        
        Attr alignAttr = fDocument.createAttribute("Alignment");
        alignAttr.setValue(scrptElem.alignment);
        paraElem.setAttributeNode(alignAttr);
        
        for (Text textRun : scrptElem.getTextRuns()) {
            Element textElem = fDocument.createElement("Text");
            
            if (textRun.getStyle() != null && textRun.getStyle().length() > 0) {
            	Attr styleAttr = fDocument.createAttribute("Style");
            	styleAttr.setValue(textRun.getStyle());
            	textElem.setAttributeNode(styleAttr);
            }
            
            textElem.setTextContent(textRun.getTextContent());
            
            paraElem.appendChild(textElem);
        }
        
        content.appendChild(paraElem);
    }
}