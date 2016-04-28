/* File Name : LeftAligned.java */

package left_aligned;

import java.io.File;
import java.lang.reflect.Method;

public class LeftAligned {
	private ScriptDocument sDocument;
	
	 public boolean make(FileType fileType, File file) {
	   	 switch (fileType) {
	   	 	case FDX:
	   	 		return makeFromFDX(file);
	   	 	case DOCX:
	   	 		return makeFromWord(file);
	   	 	default:
	   	 		return false;
	   	 }		 
	   }
	
    public boolean makeFromWord(String wordPath) {
    	return makeFromWord(new File(wordPath));
    }
    
    public boolean makeFromWord(File wordFile) {
    	try {
    		ReadWord rw = new ReadWord(wordFile);
    		sDocument = rw.getScriptDocument();
    	} catch (Exception exp) {
    		exp.printStackTrace();
    		return false;
    	}
        
        return true;
    }
    
    public boolean makeFromFDX(String fdxPath) {
    	return makeFromFDX(new File(fdxPath));
    }
    
    public boolean makeFromFDX(File fdxFile) {
    	try {
    		 ReadFDX rf = new ReadFDX(fdxFile);
    		 sDocument = rf.getScriptDocument();
    	} catch (Exception exp) {
    		exp.printStackTrace();
    		return false;
    	}
        
        return true;
    }
    
    public boolean write(FileType fileType, File file) {
   	 switch (fileType) {
   	 	case FDX:
   	 		return writeFDX(file);
   	 	case DOCX:
   	 		return writeWord(file);
   	 	default:
   	 		return false;
   	 }		 
   }
    
    public boolean writeFDX(String fdxFileName) {
    	return writeFDX(new File(fdxFileName));
    }
    
    public boolean writeFDX(File fdxFile) {
    	FDXWriter fw = new FDXWriter(sDocument);
    	
    	return fw.write(fdxFile);
    }
    
    public boolean writeWord(String fdxFileName) {
    	return writeWord(new File(fdxFileName));
    }
    
    public boolean writeWord(File wordFile) {
    	try {
    		WordWriter ww = new WordWriter(sDocument);
    		ww.write(wordFile);
	   	} catch (Exception exp) {
	   		exp.printStackTrace();
	   		return false;
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
