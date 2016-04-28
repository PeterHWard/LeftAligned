package left_aligned;

import java.io.File;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

final class TrelyPres {
	public static String SCENE_HEADING = "\\";
	public static String ACTION = ".";
	public static String CHARACTER = "_";
	public static String PARENTHETICAL = "(";
	public static String DIALOGUE = ":";
	public static String SHOT = "=";
	public static String TRANSITION = "/";
	public static String NOTE = "%";
}

public final class TrelbyWriter implements IDocumentWriter {
	private String tDocument;
	private ScriptDocument sDocument;
	
	@Override
	public boolean write(String filePath) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean write(File file) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void buildTrelbDocument() {
		// START ->
	}

}
