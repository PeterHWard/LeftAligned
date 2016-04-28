package left_aligned;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public final class WordWriter implements IDocumentWriter {
	private XWPFDocument wDocument;
	private ScriptDocument sDocument;
	
	public WordWriter(ScriptDocument sDocument) {
		this.sDocument = sDocument;			
	}
	
	@Override
	public boolean write(String filePath) {
		return write(new File(filePath));
	}
	
	@Override
	public boolean write(File wordFile) {
		buildWordDocument();
		try {
			FileOutputStream out = new FileOutputStream(wordFile);
			wDocument.write(out);
			out.close();
		} catch (Exception exp) {
			exp.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private void buildWordDocument() {
		wDocument = new XWPFDocument();
		
		for (SceneGroup sGroup : sDocument.getMembers()) {
			for (ElementGroup eGroup : sGroup.getMembers()) {
				handleElementGroup(eGroup);
			}
		}
	}
	
	private void handleElementGroup(ElementGroup eGroup) {
		if (eGroup.type == "Dialogue Group") {
			handleDialogueGroup(eGroup);
			return;
		}
		
		for (ScriptElement sElem : eGroup.getMembers()) {
			makeParagraph(sElem.getTextRuns(), sElem.getAlignment());
		}
	}
	
	private void handleDialogueGroup(ElementGroup eGroup) {
		ArrayList<Text> normalizedRuns = new ArrayList<Text>();
		for (ScriptElement sElem : eGroup.getMembers()) {
			ArrayList<Text> elementRuns = new ArrayList<Text>();
			sElem.accept(new ITextVisitor() {
				public void visit(Text textRun) {
					elementRuns.add(textRun.clone());			
				}
			});
			
			int len = elementRuns.size();
			if (len == 0) continue;
			Text theRun;
			if (sElem.type == "Character") {
				theRun = elementRuns.get(len -1);
				theRun.setTextContent(theRun.getTextContent() + ":");
			} else {
				theRun = elementRuns.get(0);
				theRun.setTextContent(" "+ theRun.getTextContent());
			}
			
			normalizedRuns.addAll(elementRuns);
		}
		
		makeParagraph(normalizedRuns);
	}
	
	private void makeParagraph(ArrayList<Text> scriptRuns) {
		makeParagraph(scriptRuns, null);
	}
	
	private void makeParagraph(ArrayList<Text> scriptRuns, String align) {
		XWPFParagraph wPara = wDocument.createParagraph();
		wPara.setAlignment(getWordAlign(align)); 
		
		for (Text sRun : scriptRuns) {	
			makeWordRun(wPara.createRun(), sRun);
		}
		
		// Insert blank line.
		wDocument.createParagraph();
	}
	
	private void makeWordRun(XWPFRun wordRun, Text scriptRun) {
		wordRun.setText(scriptRun.getTextContent());
		wordRun.setFontFamily("Courier New");
		wordRun.setFontSize(12);
		if (scriptRun.getStyle() == null) return;
		
		for (String style : scriptRun.getStyle().split(" ")) {
			switch (style) {
			case "Bold":
				wordRun.setBold(true);
				break;
			case "Italic":
				wordRun.setItalic(true);
				break;
			case "Underline":
				wordRun.setUnderline(UnderlinePatterns.SINGLE );
				break;
			default:
				break;
			}
		}
	}
	
	private ParagraphAlignment getWordAlign(String align) {
		if (align == null) return ParagraphAlignment.LEFT;
		
		switch (align) {
			case "Center":
				return ParagraphAlignment.CENTER;
			case "Left":
				return ParagraphAlignment.LEFT;
			case "Right":
				return ParagraphAlignment.RIGHT;
			default:
				return ParagraphAlignment.LEFT;
		}
	}
}
