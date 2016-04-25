package left_aligned;

import java.util.ArrayList;

public class ScriptElement implements IScriptElement<Text> {
	public String type;	
    private ArrayList<Text> textRuns;
    
    public ScriptElement(Text... textRuns) {
    	type = "Script Element";
        this.textRuns = new ArrayList<Text>();
        
        for (Text tr : textRuns) {
        	this.textRuns.add(tr);
        }
    }
    
    public void addTextRun(Text textRun) {
    	textRun.setParent(this);
    	
        textRuns.add(textRun);
    }
    
    // Attempts to normalize/merge with last extant run; 
    // otherwise appends.
    public void addTextRun(Text textRun, boolean tryMerge) {
        if (!tryMerge || textRuns.size() == 0) {
            this.addTextRun(textRun);
        }
        
        if (textRun.getTextContent().length() == 0) return;
        
        Text lastRun = textRuns.get(textRuns.size() - 1);
        if (areAttrsEqual(lastRun, textRun)) {
            lastRun.setTextContent(lastRun
                .getTextContent() + textRun.getTextContent());
            return;
        }
         
        textRuns.add(textRun);
        return;
    }
    
    public void removeTextRun(Text textRun) {
    	textRuns.remove(textRun);
    }
    
    public void removeTextRun(int idx) {
    	textRuns.remove(idx);
    }
    
    public ArrayList<Text> getTextRuns(){
        return textRuns;
    }
    
    public String getTextContent() {
    	// FIXME - add 'final' bool in which case no \n
        String textContent = "";
        for (Text run : textRuns) {
            textContent += run.getTextContent();
        }
        
        return textContent + "\n";
    }
    
    private boolean areAttrsEqual(Text runA, Text runB) {
        if (runA.getStyle() == runB.getStyle()) {
            return true;
        }
        
        return false;
    }
    
    public void trimTextContent() {
    	if (textRuns.size() != 0) trimTextContent(true);
  	
    	removeEmpties();
    }
    
    private void trimTextContent(boolean atFront) {
    	int idx = atFront ? 0 : textRuns.size() - 1;
    	Text theRun = textRuns.get(idx);  	
    	
    	
    	if (atFront) {
    		if (theRun.getTextContent().trim().length() == 0) {
        		removeTextRun(idx);
        		// Make sure the removed run wasn't the only run:
        		trimTextContent();
        		return;
        	}
    		
    		else {
    			atFront = false;
    			theRun.setTextContent(theRun
        				.getTextContent()
        				.replaceAll("^\\s+", ""));
    			
    			trimTextContent(false);
        		return;
    		} 		  		
    		
    	} else {
    		theRun.setTextContent(theRun
    				.getTextContent()
    				.replaceAll("\\s+$", ""));
    		
    		return;
    	}
    }
    
    public void accept(ISceneGroupVisitor visitor) {
    	visitor.visit(this);
    }
    
    public void accept(IElementGroupVisitor visitor) {
		visitor.visit(this);
	}
    
    public void accept(IElementVisitor visitor) { 	
    	visitor.visit(this);
    }
    
    public void accept(ITextVisitor visitor) {
    	for (Text run : getTextRuns()) {
    		run.accept(visitor);
    	}
    }
    	
	public void removeEmpties() {
		for (Text run : this.getTextRuns()) {
			if (run.getTextContent().length() == 0) {
				removeTextRun(run);
			}
		}
	}
	
	public void normalize() {
		normalize(0);
	}
	
	private void normalize(int idx) {
		if (idx + 1 >= textRuns.size()) return;
		
		if (!areAttrsEqual(textRuns.get(idx), textRuns.get(idx + 1))) {
			normalize(++idx);
			return;
		}
		
		textRuns.get(idx).setTextContent(	textRuns.get(idx)
											.getTextContent() 
											+ textRuns.get(idx + 1)
											.getTextContent());
		
		removeTextRun(idx + 1);
		normalize(idx);
	}
}
