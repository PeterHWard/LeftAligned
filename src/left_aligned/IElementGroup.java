package left_aligned;

import java.util.ArrayList;

public interface IElementGroup<T> extends IScriptElementBase<T> {
	public void addMember(T elem);
	public ArrayList<T> getMembers();
	public void removeMember(T elem);
	public void removeMember(int idx);
	public void accept(IElementGroupVisitor vistor);
	public void accept(IElementVisitor vistor);
	public void accept(ITextVisitor vistor);
}
