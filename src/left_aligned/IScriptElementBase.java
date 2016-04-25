package left_aligned;

public interface IScriptElementBase<T> {
	public String getTextContent();
	public void trimTextContent();
	public void accept(ISceneGroupVisitor visitor);
	public void accept(IElementGroupVisitor visitor);
	public void removeEmpties();
}