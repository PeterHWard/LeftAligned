package left_aligned_tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import left_aligned.IElementVisitor;
import left_aligned.ReadWord;
import left_aligned.ScriptDocument;
import left_aligned.ScriptElement;
import left_aligned.Text;

import static left_aligned_tests.WordDocumentBuilder.makeWordDocument;
import static left_aligned_tests.TextStyleTupleArray.textStyleTupleArray;


public class LeftAlignedTests {
	
	private void testStyleRuns() {
		testByRuns(
				"Test Styles",
				tsa("Some normal text."),
				tsa("Some bold text.", "Bold"),
				tsa("Some italics text.", "Italic"),
				tsa("Some underline text.", "Underline"),
				
				new Text("Some normal text.", null, "Action"),
				new Text("Some bold text.", "Bold", "Action"),
				new Text("Some italics text.", "Italic", "Action"),
				new Text("Some underline text.", "Underline", "Action")
		);
	}
	
	private void testSceneHeadingGroup() {
		testByRuns(
				"Test Scene Heading",
				tsa("INT. SOME LOCATION - DAY"),
				
				new Text("INT. SOME LOCATION - DAY", null, "Scene Heading")
		);
	}
	
	// textActionGroup - FIXME
	// testShotGroup
	// testTransitionGroup
	
	private void testDialogueGroup() {
		testByRuns(
				"Test Dialogue - simple",
				
				tsa("CHARACTER NAME: Some dialogue. (a parenthetical) More dialogue."),
				
				new Text("CHARACTER NAME", null, "Character"),
				new Text("Some dialogue.", null, "Dialogue"),
				new Text("(a parenthetical)", null, "Parenthetical"),
				new Text("More dialogue.", null, "Dialogue")
		);
		
		// Style change at sub-element level
		testByRuns(
				"Test Dialogue - style shifts",
				
				tsa("CHARACTER NAME: ", "Bold"),
				tsa("Some dialogue."),
				tsa("(a parenthetical) ", "Bold"),
				tsa("More dialogue."),
				
				new Text("CHARACTER NAME", "Bold", "Character"),
				new Text("Some dialogue.", null, "Dialogue"),
				new Text("(a parenthetical)", "Bold", "Parenthetical"),
				new Text("More dialogue.", null, "Dialogue")
		);
		
		// Style within text
		testByRuns(
				"Test Dialogue - mid-element style shifts",
				tsa("CHARACTER NAME: "),
				tsa("Some", "Bold"),
				tsa("dialogue. (a parenthetical) More dialogue."),
				
				new Text("CHARACTER NAME", null, "Character"),
				new Text("Some", "Bold", "Dialogue"),
				new Text("dialogue.", null, "Dialogue"),
				new Text("(a parenthetical)", null, "Parenthetical"),
				new Text("More dialogue.", null, "Dialogue")
		);
		
		// Style within text
				testByRuns(
						"Test Dialogue - parenthetical first",
						tsa("CHARACTER NAME: "),
						tsa("(a parenthetical)"),
						tsa("Some dialogue."),
						
						new Text("CHARACTER NAME", null, "Character"),
						new Text("(a parenthetical)", null, "Parenthetical"),
						new Text("Some dialogue.", null, "Dialogue")
				);
		
		// An irrational case 
		testByRuns(	
				"Test Dialogue - 'worse case'",
				tsa("CHARACTER NAME: So"),
				tsa("me dialogue. (a ", "Bold"),
				tsa("parenthetica"),
				tsa("l) More dialogue.", "Bold"),
				
				new Text("CHARACTER NAME", null, "Character"),
				new Text( "So", null, "Dialogue"),
				new Text( "me dialogue.", "Bold", "Dialogue"),
				new Text( "(a ", "Bold", "Parenthetical"),
				new Text( "parenthetica", null, "Parenthetical"),
				new Text( "l)", "Bold", "Parenthetical"),
				new Text( "More dialogue.", "Bold", "Dialogue")
			);
		
	}
	
	private void testByRuns(Object ... args) {
		String annotation = "";
		ArrayList<String[]> sources = new ArrayList<String[]>();
		ArrayList<Text> expecteds = new ArrayList<Text>();
		
		for (Object arg : args) {
			if (arg instanceof String) annotation += arg;
			if (arg instanceof String[]) sources.add((String[])arg);
			else if (arg instanceof Text) expecteds.add((Text)arg);
		}
		
		testByRuns(	sources.toArray(new String[sources.size()][2]), 
					expecteds.toArray(new Text[expecteds.size()]),
					annotation);
	}
	
	private void testByRuns(String[][] sources, Text[] expecteds, String annotation) {
		System.out.println(":> LA Tests: " + annotation);
		class TestingVisitor implements IElementVisitor {
			public ArrayList<Text> testRuns = new ArrayList<Text>();
			public ArrayList<String> types = new ArrayList<String>();
			
			public void visit(ScriptElement elem) {
				// We should be testing one element. But in case of error,
				// it is good to see all runs generated.
				testRuns.addAll(elem.getTextRuns());
				types.add(elem.type);
			}
		}
		
		ScriptDocument sd = new ReadWord(makeWordDocument(textStyleTupleArray(sources))).getScriptDocument();
		TestingVisitor tv = new TestingVisitor();
		sd.accept(tv);
		
		//assertEquals(expecteds.length, tv.testRuns.size());
		
		Text testRun, refRun;
		for (int i = 0; i < tv.testRuns.size(); i++) {
			testRun = tv.testRuns.get(i);
			refRun = expecteds[i];
					
			System.out.println("|>" + refRun.getTextContent() + "<| |>" + testRun.getTextContent() + "<|");
			assertEquals(refRun.getTextContent(), testRun.getTextContent());
			System.out.println("|>" + testRun.getStyle() + "<| |>" + refRun.getStyle() + "<|");
			assertEquals(refRun.getStyle(), testRun.getStyle());
			System.out.println("|>" + testRun.getParentType() + "<| |>" + refRun.getParentType() + "<|");
			assertEquals(refRun.getParentType(), testRun.getParentType());
		}
	}
	
	private String[] tsa(String ... strings) {
		return strings;
	}
	
	
	public void doTest() {
		/*
		 * Element Group Types:
		 *	- Scene Heading Group
	     *  - Action Group
	     *	- Dialogue Group
	     * 	- Shot Group
	     * 	- Transition Group
		 * 	- General Group [not tested as is default]
		 * 
		 */
		
		testStyleRuns();
		testSceneHeadingGroup();
		testDialogueGroup();
		System.out.println("LeftAlignedTests: All tests passed!");
	}
	
	public static void main(String[] args) {
		LeftAlignedTests lat = new LeftAlignedTests();
		lat.doTest();
	}
}
