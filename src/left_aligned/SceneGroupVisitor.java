package left_aligned;

public abstract class SceneGroupVisitor implements ISceneGroupVisitor {
	public void visit(ScriptDocument document) {}
	public void visit(SceneGroup sGroup) {}
	public void visit(ElementGroup eGroup) {}
	public void visit(ScriptElement elem) {}	
}
