package left_aligned;

public class CharacterName extends ScriptElement {
	public CharacterName(Text... textRuns) {
		super(textRuns);
		type = "Character";
	}
	
	public void accept(IElementGroupVisitor visitor) {
		visitor.visit(this);
	}
}
