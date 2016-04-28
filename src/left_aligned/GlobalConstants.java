/* File Name : global_constants.java */

package left_aligned;

import java.util.Dictionary;
import java.util.Hashtable;

public class GlobalConstants {
	// Meta
	public static final String version = " v1.1.0-beta";

    // Alert/prompt strings
    public static final String INVALID_FILE_ERR = "Failed to open: Invalid or corrupted file";
    public static final String OVERWRITE_ASK_OK = "Overwrite existing file?";
    public static final String ASK_VALID_WORD_FILE = "Please select a valid Word file";

    // File paths
    public static final String FDX_TEMPLATE_PATH = "assets/template.xml";
    
    // File type related details
    private static Dictionary<FileType, FileTypeTup> extDict;
    static {
    	extDict = new Hashtable<FileType, FileTypeTup>();  	
    	FileTypeTup ftt;
    	
    	ftt = new FileTypeTup();
    	ftt.extension = "docx";
    	ftt.name = "Word 2007";
    	extDict.put(FileType.DOCX, ftt);
    	
    	ftt = new FileTypeTup();
    	ftt.extension = "fdx";
    	ftt.name = "Final Draft";
    	extDict.put(FileType.FDX, ftt);
    	
    	ftt = new FileTypeTup();
    	ftt.extension = "txt";
    	ftt.name = "Text";
    	extDict.put(FileType.TXT, ftt);
    	
    	ftt = new FileTypeTup();
    	ftt.extension = "*";
    	ftt.name = "All Files";
    	extDict.put(FileType.ALL_FILES, ftt);
    }
    
    
    
    public static FileTypeTup getFileExtTup(FileType ft) {
    	return extDict.get(ft);
    }  
}

