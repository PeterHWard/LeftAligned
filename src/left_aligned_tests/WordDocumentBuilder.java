package left_aligned_tests;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import left_aligned.TextStyleTuple;

public class WordDocumentBuilder {
	public static XWPFDocument makeWordDocument(TextStyleTuple[] textsStyles) {
		XWPFDocument wDocument = new XWPFDocument();
		XWPFParagraph para = wDocument.createParagraph();
		XWPFRun wordRun;
		
		for (TextStyleTuple tup : textsStyles) {
			// text, style
			wordRun = para.createRun();	
			wordRun.setText(tup.text);
			
			if (tup.styles == null) continue;	
	
			for (String style : tup.styles) {
				
				switch (style) {
				case "Bold":
					wordRun.setBold(true);
					break;
				case"Italic":
					wordRun.setItalic(true);
					break;
				case "AllCaps":
					wordRun.setCapitalized(true);
					break;
				case "Underline":
					wordRun.setUnderline(UnderlinePatterns.SINGLE);
					break;	
				}
			}		
		}
	
		return wDocument;
	}
}
