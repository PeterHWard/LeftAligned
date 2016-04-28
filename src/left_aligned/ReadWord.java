/* File Name : ReadWord.java */

package left_aligned;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class ReadWord {
    private ScriptDocument sDocument;
    private XWPFDocument wDocument;
    private String[] elementsGroupTypes = {
                                        "Scene Heading Group",
                                        "Action Group", 
                                        "Dialogue Group",
                                        "Shot Group",
                                        "Transition Group",
                                        "General Group" 
                                                            };
    private String[] elementTypes = {   "Scene Heading",
                                        "Action",
                                        "Character",
                                        "Parenthetical",
                                        "Dialogue",
                                        "Shot",
                                        "Transition",
                                        "General"          };
     
    public ReadWord(File file) throws IOException {
    	FileInputStream wordFile;
        try {
            wordFile = new FileInputStream(file.getAbsolutePath());            
            
        } catch (Exception exp) {
        	throw new IOException("ReadWord: Input file unreadable", exp);
        }
        
        exec(new XWPFDocument(wordFile));
        wordFile.close();
    }

	public ReadWord(XWPFDocument wDocument) {
    	exec(wDocument);
    }
	
	public ScriptDocument getScriptDocument() {
		return sDocument;
	}
    
    private void exec(XWPFDocument wDocument) {
    	this.wDocument = wDocument;
    	walkDocument();
        sDocument.normalize();
        sDocument.trimTextContent();
    }
 
    private void walkDocument() {
    	sDocument = new ScriptDocument();
        ArrayList<ElementGroup> elementGroups = batchMakeElementGroups();
        SceneGroup sceneGroup = new SceneGroup();
        
        for (ElementGroup eGroup : elementGroups) {  
            if (eGroup.type == "Scene Heading Group") {
            	sDocument.addMember(sceneGroup);
            	//System.out.println(sceneGroup.getTextContent()); // FIXME
                sceneGroup = new SceneGroup();
            }
            
            sceneGroup.addMember(eGroup);  
            //System.out.println(sceneGroup.getTextContent()); // FIXME
        }
        
        sDocument.addMember(sceneGroup);
    }
    
    private ArrayList<ElementGroup> batchMakeElementGroups() {
    	List<XWPFParagraph> paragraphs = wDocument.getParagraphs();
    	SharedState sState = new SharedState();
    	int idx = 0;
    	
    	for (XWPFParagraph para : paragraphs) {
        	if (para.getText().trim().length() == 0) continue;
        	
        	sState.stack.add(null);
        	MakeElementGroup wkr = new MakeElementGroup(para, sState, idx++);
        	
        	try {
        		wkr.start();
        		wkr.join();
        	} catch (Exception exp) {
        		exp.printStackTrace();
        		return null;
        	}
    	}
    	
    	return sState.stack;
    }
    
    
    class MakeElementGroup extends Thread {
    	private SharedState sharedState;
    	private XWPFParagraph sourceParagraph;
    	private int idx;
    		
    	public MakeElementGroup(XWPFParagraph sourceParagraph, 
    			SharedState sharedState,
    			int idx) {
    		
    		this.sharedState = sharedState;
    		this.sourceParagraph = sourceParagraph; 
    		this.idx = idx;
    	}
    	
    	public void run() {
    		ScriptElement trElem = makeTextRuns(sourceParagraph);
            if (sourceParagraph.getAlignment() == ParagraphAlignment.CENTER) {
            	trElem.setAlignment("Center");
            }
            String elemType = getElementType(trElem);
            
            sharedState.stack.set(idx, makeElementGroup(elemType, trElem)); 
    	}
    	
    	private ScriptElement makeTextRuns(XWPFParagraph para) {
            List<XWPFRun> wordRuns = para.getRuns();
            ScriptElement textRuns = new ScriptElement();
            
            int startIndex = 0;
            for (XWPFRun wordRun : wordRuns) {
	           	 if (wordRun.getText(0) == null) continue;
	           	 
	                Text textRun = new Text();
	                String textContent = wordRun.getText(0);        
	                if (wordRun.isCapitalized()) textContent = textContent.toUpperCase();
	                textRun.setTextContent(textContent);
	                textRun._startIndex = startIndex;
	                textRun._endIndex = startIndex + textContent.length();             
	                
	                ArrayList<String> styles = new ArrayList<String>();
	                if (wordRun.isBold()) styles.add("Bold");
	                if (wordRun.isItalic()) styles.add("Italic");
	                if (wordRun.getUnderline() 
	                   != UnderlinePatterns.NONE) styles.add("Underline");
	                
	                if (styles.size() > 0) textRun.setStyle(styles.toArray(new String[styles.size()]));
	                textRuns.addTextRun(textRun);
	                startIndex = textRun._endIndex;
            }
            
            return textRuns;
       }
       
       private String getElementType(ScriptElement trPara) {
           /* 
               NB: These are actually Element Groups not simple elements.
               This is because one dialog paragraph in Word contains
               three FDX elements.
               
               Element Group Types:
                   - Scene Heading Group
                   - Transition Group
                   - Shot Group
                   - Dialogue Group
                   - Action Group
                   - General Group
           */
           ParagraphState pState = new ParagraphState();
           String paraText = trPara.getTextContent();
           
           if (isFilterAllCaps(paraText, pState)) {
               if (isSceneHeading(paraText, pState)) {
                   return "Scene Heading Group";
               } else if (isTransition(paraText, pState)) {
                   return "Transition Group";
               } else {
                   return "Shot Group";
               }
               
           } else {
               if (isDialog(paraText, pState)) {
                   return "Dialogue Group";
               } else if (isAction(paraText, pState)){
                   return "Action Group";
               } 
               
               return "General Group";
           }
       }
       
       private boolean isFilterAllCaps(String text, ParagraphState pState) {
           if (text.toUpperCase() != text) {
               String[] args = { 	"Scene Heading", 
                       				"Shot", 
                       				"Transition"	};
           	
               pState.eliminateTypes(args);
                                       
               return false;
           } else {
               //pState.eliminateTypes({})
               return true;
           }
       }
       
       private boolean isSceneHeading(String text, ParagraphState pState) {
       	// FIXME pState not currently used
           text = text.trim();
           text = text.replace("—", "--"); 

           if (hasPattern(text, "^(INT|EXT)\\. ")) return true;
           if (hasPattern(text, "\\- ?(DAY|NIGHT|CONTINUOUS|NEXT)$")) return true;        
           if (hasPattern(text, "^\\S+\\. .+\\- \\S+$")) return true;
                 
           return false;
       }
       
       private boolean isTransition(String text, ParagraphState pState) {
    	   text = text.trim();
           if (hasPattern(text, "(^FADE |TO:$)")) return true;
           
           return false;
       }
       
       private boolean isDialog(String text, ParagraphState pState) {
           text = text.trim();
           text = text.replaceAll("\\s+", " ");
           if (hasPattern(text, "^[^a-z]{2,30}?: \\S+")) return true;
           if (hasPattern(text, "^.{2,30}? \\(.+\\): \\S+")) return true;
           
           return false;
       }
       
       private boolean isAction(String text, ParagraphState pState) {
           // For now whatever's left assumed to be action
           return true;
       }
       
       private boolean hasPattern(String testStr, String pattern) {
       	if (Pattern.compile(pattern).matcher(testStr).find()) return true;
           
           return false;
       }
       
       private ElementGroup makeElementGroup(String type, ScriptElement genericE) {
       	ElementGroup typedEG;
           ScriptElement typedE;
           // FIXME - consistent solution
           
           switch (type) {
               case "Dialogue Group":
                   return makeDialogGroup(genericE);
               case "Scene Heading Group":       
            	   return makeSceneHeadingGroup(genericE);
               case "Transition Group":
               	typedE = new Transition();
                   typedEG = new TransitionGroup();
                   break;
               case "Shot Group":
               	typedE = new Shot();
                   typedEG = new ShotGroup();
                   break;
               case "Action Group":
               	typedE = new Action();
                   typedEG = new ActionGroup();
                   break;
               default:
               	typedE = new General();
                   typedEG = new GeneralGroup();
                   break;
           }
           
           for (Text tr : genericE.getTextRuns()) {       	
        	   typedE.addTextRun(tr);
           }
           
           typedE.setAlignment(genericE.getAlignment());
           typedEG.addMember(typedE);
           return typedEG;
       }
       
       private SceneHeadingGroup makeSceneHeadingGroup(ScriptElement genericE) {
    	   SceneHeading sHeading = new SceneHeading();
    	   SceneHeadingGroup sHeadingGroup = new SceneHeadingGroup(); 
    	   
    	   // FIXME - for single element groups, drop elem straight in 
    	   // when init eGroup
    	   for (Text tr : genericE.getTextRuns()) {       	
    		   sHeading.addTextRun(tr);
           }
    	   
    	   sHeading.accept(new CharFilterVisitor());
    	   
    	   sHeading.setAlignment(genericE.getAlignment());
    	   sHeadingGroup.addMember(sHeading);
    	   return sHeadingGroup;
       }
       
       private DialogueGroup makeDialogGroup(ScriptElement genericE) { 
           class DGState {
               private ArrayList<String> tokens;
               private ArrayList<Text> sourceRuns;
               private String paragraphText;
               
               Iterator<String> itrTokens;
               Iterator<Text> itrSourceRuns;
               
               private final int SOURCE_RUN = 1;
               private final int TOKEN = 2;
               
               public String token;
               public String tokenType, nextTokenType;
               public Text sourceRun;
               
               private int tokenStart;
               private int tokenEnd;
               public int intersectStart;
               public int intersectEnd;
               
               public DGState( ArrayList<String> tokens, 
                               ArrayList<Text> sourceRuns,
                               String paragraphText ) {
                   
	                   this.tokens = tokens;
	                   this.sourceRuns = sourceRuns;
	                   itrTokens = tokens.iterator();
	                   itrSourceRuns = sourceRuns.iterator();
	                   this.token = itrTokens.next();
	                   this.sourceRun = itrSourceRuns.next();
	                   this.paragraphText = paragraphText;
	                   
	                   tokenType = "Character";
	                   nextTokenType = (tokens.get(0).charAt(0) == '(')
	                       ? "Parenthetical" 
	                       : "Dialogue";
	                   tokenStart = 0;
	                   tokenEnd = token.length();
	                   
	                   getIntersection();
               }
               
               public boolean hasNext() {
                   if (itrTokens.hasNext() || itrSourceRuns.hasNext()) {
                       return true;
                   }
                   
                   return false;
               }
               
               public void next() {
                   switch (getCallOnNext()) {
                       case (SOURCE_RUN | TOKEN):
                           advanceRun();
                           advanceToken();
                           break;
                       case SOURCE_RUN:
                           advanceRun();
                           break;
                       case TOKEN:
                           advanceToken();
                           break;
                       default:
                           break;
                   }
               }
               
               public Text makeRun() {
                   Text run = new Text();
                   
                   if (sourceRun.getStyle() != null) {
                	   run.setStyle(sourceRun.getStyle()); 
                   }
                   
                   run.setTextContent(paragraphText
                		   				.substring(intersectStart, 
                		   					intersectEnd));
                   
                   return run;
               }
               
               private void advanceRun() {
                   sourceRun = itrSourceRuns.next();  
                   getIntersection();
               }
               
               private void advanceToken() {   
                   token = itrTokens.next();
                   tokenStart = tokenEnd;    
                   tokenEnd = tokenStart + token.length();
                   tokenType = nextTokenType;
                   nextTokenType = (tokenType == "Parenthetical") 
                       ? "Dialogue" : "Parenthetical";
                   getIntersection();
               }
               
               private void getIntersection() {           	
                   intersectStart = (sourceRun._startIndex > tokenStart) 
                       ? sourceRun._startIndex : tokenStart;
                   intersectEnd = (sourceRun._endIndex < tokenEnd) 
                       ? sourceRun._endIndex : tokenEnd;  
               }
               
               private int getCallOnNext() {
                   if (sourceRun._endIndex == tokenEnd) {
                       return TOKEN | SOURCE_RUN;
                   } else if (sourceRun._endIndex > tokenEnd) {
                       return TOKEN;
                   } else {
                       return SOURCE_RUN;
                   }
               }
           }   
           
           DialogueGroup dGroup = new DialogueGroup();
           String paragraphText = genericE.getTextContent();
           ArrayList<String> tokens = tokenizeDialogueParagraph(paragraphText);
           
           DGState dgs = new DGState(	tokens, 
                               			genericE.getTextRuns(), 
                               			genericE.getTextContent() );
                               
           ScriptElement elemWrapper = new CharacterName();
           dGroup.addMember(elemWrapper);
           String lastTokenType = "Character";
           
           while (true) {
               if (dgs.tokenType != lastTokenType) {
            	   lastTokenType = dgs.tokenType;
           			
                   elemWrapper = (dgs.tokenType == "Parenthetical")
           				? new Parenthetical() 
           				: new Dialogue();
           			dGroup.addMember(elemWrapper);
               }
               
               elemWrapper.addTextRun(dgs.makeRun()); 
               
               if (!dgs.hasNext()) {
               		break;
               }
               
               dgs.next();
           }
           
           cleanupDGroup(dGroup);
           return dGroup; 
       }
       
       private void cleanupDGroup(DialogueGroup dGroup) {
    	   dGroup.accept(new ElementGroupVisitor() {
    		   @Override
    		   public void visit(CharacterName chrName) {
    			   chrName.trimTextContent();
	   			
	   			   Text endRun = chrName
	   						.getTextRuns()
	   						.get(chrName
	   							.getTextRuns()
	   							.size() - 1);
	   				
	   					endRun
		       				.setTextContent(endRun
		       					.getTextContent()
		       					.replace(":", ""));	
	   			}
       		});
       }
    }
    
    public ArrayList<String> tokenizeDialogueParagraph (String paragraphText) {
    	 // N.B.: After split we need to put split chars back in str
    	ArrayList<String> tokens = new ArrayList<String>();
        String[] parts = paragraphText.split(": ");
        tokens.add(parts[0] + ": ");
        parts = Arrays.copyOfRange(parts, 1, parts.length);
        String dialogBlock = String.join(": ", parts);
        boolean paranFirst = dialogBlock.startsWith("(") // FIXME - currently dialogBlock will start with whitespace
            ? true
            : false;
        
        // Let's separate out the parentheticals. 
        // A case of "(....<EOL>" results in "(....)" post normalization.
        char expect = '(';
        String token = "";
        for (int i = 0; i < dialogBlock.length(); i++){
     	    char c = dialogBlock.charAt(i);        
     	    if (c == expect) {
     	    	if (expect == '(') {
     	    		expect = ')';
     	    		tokens.add(token);
     	    		token = "(";
     	    	} else {
     	    		expect = '(';
     	    		tokens.add(token + c);
     	    		token = "";
     	    	}
     	    	
     	    } else {
     	    	token += c;
     	    }
        }
        if (expect == ')') {
     	   tokens.set(tokens.size() - 1, tokens.get(tokens.size() - 1) + token);
        } else {
     	   tokens.add(token);
        }             
        
        return tokens;       
    }
    
    
    // Nested classes/interfaces
    class SharedState {
    	public ArrayList<ElementGroup> stack;
    	
    	public SharedState() {
    		stack = new ArrayList<ElementGroup>();
    	}
    	
    	public SharedState(int stackSize) {
    		stack = new ArrayList<ElementGroup>(stackSize);
    	}
    }
    
    class ParagraphState {
        public String[] possibleTypes;
        public int typesRemain;
        public boolean complete;
        
        public ParagraphState() {
            complete = false;
            possibleTypes = elementsGroupTypes.clone();
            typesRemain = possibleTypes.length;
        }
        
        public void eliminateTypes(String[] toVoid) {
            for (String type : toVoid) {
            	eliminateTypes(type);
            }
        }
        
        public void eliminateTypes(String toVoid) {
            for (int i = 0; i < possibleTypes.length; i++) {
                if (this.possibleTypes[i].equals(toVoid)) {      
                    this.possibleTypes[i] = null;                      
                    if (--this.typesRemain == 1) complete = true;
                    return;
                }
            }
        }
    } 
}
 