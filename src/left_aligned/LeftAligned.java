/* File Name : LeftAligned.java */

package left_aligned;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.*;
import javax.xml.transform.stream.StreamResult;

public class LeftAligned {
	private Document fdxDocument;
    public boolean makeFDX(String wordPath) {
    	return makeFDX(new File(wordPath));
    }
    
    public boolean makeFDX(File wordFile) {
    	ReadWord rw;
    	try {
    		rw = new ReadWord(wordFile);
    	} catch (Exception exp) {
    		exp.printStackTrace();
    		return false;
    	}
    	
        FDXModel fdm = new FDXModel();
        ArrayList<SceneGroup> shotGroups = rw.document.getMembers();
        
        for (SceneGroup sGroup : shotGroups) {    
            for (ElementGroup eGroup : sGroup.getMembers()) {                
                for (ScriptElement sElement : eGroup.getMembers())
                fdm.appendElement(sElement);
            }
        }
        
        fdxDocument = fdm.document;
        
        return true;
    }
    
    public boolean writeFDX(String filePath) throws IOException {
    	return writeFDX(new File(filePath));
    }
    
    public boolean writeFDX(File file) throws IOException {
    	try {
    		TransformerFactory txf = TransformerFactory.newInstance();
    		Transformer tx = txf.newTransformer();
    		DOMSource src = new DOMSource(fdxDocument); 
            StreamResult res = new StreamResult(file);
            tx.transform(src, res);
        
    	} catch (Exception exp) {
            throw new IOException("LeftAligned: Bad output file", exp);
        }      
        
        return true;
    }
    
    public boolean fileExists(File file) {
        return file.isFile();
    }
    
    public boolean fileExists(String filePath) {
        return fileExists(new File(filePath));
    }
    
    public boolean isValidFile(String filename, String ext) {
    	return isValidFile(new File(filename), ext);
    }
    
    public boolean isValidFile(File file, String ext) {
        if (!fileExists(file)) return false;
       	 // FIXME test content header
  
    	return true;
    }
    
    public String newFileExension(String filePath, String newExt) {	
    	newExt = newExt.replace("\\.", "");
    	String[] parts = filePath.split("\\.");
    	parts[(parts.length - 1)] = newExt;
    	
    	return String.join(".", parts);
    }
}
