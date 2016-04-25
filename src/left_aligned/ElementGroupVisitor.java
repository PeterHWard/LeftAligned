package left_aligned;

public abstract class ElementGroupVisitor implements IElementGroupVisitor {
	public void visit(ScriptElement sElem) {}
	public void visit(ElementGroup sceneHeading) {}
	public void visit(SceneHeading sceneHeading) {}
	public void visit(Action action) {}
	public void visit(CharacterName character) {}
	public void visit(Dialogue dialogue) {}
	public void visit(Parenthetical parenthetical) {}
	public void visit(Transition tranistion) {}
	public void visit(Shot shot) {}
	public void visit(General general) {}
}
