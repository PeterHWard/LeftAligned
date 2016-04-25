package left_aligned;

public interface IScriptElement<T> extends IScriptElementBase<Text> {
	public void accept(IElementVisitor visitor);
}
