package left_aligned;

public class Dialogue extends ScriptElement {
	public Dialogue(Text... textRuns) {
		super(textRuns);
		type = "Dialogue";
	}
	
	public void accept(IElementVisitor visitor) { 	
    	visitor.visit(this);
    }
}

