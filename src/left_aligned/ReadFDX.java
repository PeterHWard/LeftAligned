package left_aligned;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import left_aligned.ElementGroup;
import left_aligned.GlobalConstants;
import left_aligned.SceneGroup;
import left_aligned.ScriptDocument;
import left_aligned.ScriptElement;
import left_aligned.Text;

public class ReadFDX {
	private Document fDocument;
	private ScriptDocument sDocument;
	
	public ReadFDX(File fdxFile) throws IOException {
        try {
	        
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        fDocument = builder.parse(fdxFile);
	  
		} catch (Exception exp) {
			exp.printStackTrace();
		}
        
        exec();
        
    }

	public ReadFDX(Document fDocument) {
		this.fDocument = fDocument;
    	exec();
    }
	
	public ScriptDocument getScriptDocument() {
		return sDocument;
	}
	
	private void exec() {
		sDocument = new ScriptDocument();
		SceneGroup sGroup = new SceneGroup();
		sDocument.addMember(sGroup);	
		
		Element fPara;
		ElementGroup eGroup = null;
		ScriptElement sElem;
		String theType;
		String priorType = null;
		
		Element content = (Element) fDocument .getElementsByTagName("Content").item(0);
		NodeList paras = content.getElementsByTagName("Paragraph");
		
		int i = 0;
		while (true) {
			fPara = (Element)paras.item(i++);
			if (fPara == null) break;
			
			theType = fPara.getAttribute("Type");
			if (theType.trim() == "") System.out.println(fPara.getTextContent());
			if (theType == "Scene Heading") {
				sGroup = new SceneGroup();
				sDocument.addMember(sGroup);
			}
			
			if (eGroup == null || isNewGroupType(priorType, theType)) {
				eGroup = sDocument.createElementGroup(theType);
				sGroup.addMember(eGroup);
			}
			
			sElem = sDocument.createElement(theType);
			sElem.addAllTextRuns(getTextRuns(fPara));
			sElem.setAlignment(fPara.getAttribute("Alignment"));
			eGroup.addMember(sElem);
			priorType = theType;
		}
		
		sDocument.trimTextContent();
		sDocument.normalize();
	}
	
	private boolean isNewGroupType(String priorType, String theType) {	
		String[] dialogueTypes = {
				"Character", "Dialogue", "Parenthetical"
		};
		
		if (theType == "Character") return true;
		
		if (Arrays.asList(dialogueTypes).contains(priorType) 
				&& Arrays.asList(dialogueTypes).contains(theType)) {
			return false; 
		}
		
		return true;
	}
	
	private ArrayList<Text> getTextRuns(Element fPara) {
		ArrayList<Text> retVal = new ArrayList<Text>();
		NodeList xmlRuns = fPara.getElementsByTagName("Text");
		
		Element xmlRun;
		Text scriptRun;
		int i = 0;
		while (true) {
			xmlRun = (Element)xmlRuns.item(i++);
			if (xmlRun == null) break;
			scriptRun = new Text();
			retVal.add(scriptRun);
			scriptRun.setTextContent(xmlRun.getTextContent());
			scriptRun.setStyle(xmlRun.getAttribute("Style"));
		}
		
		return retVal;
	}
}
