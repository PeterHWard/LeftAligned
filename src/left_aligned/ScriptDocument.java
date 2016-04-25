/* File Name : ElementObjects.java */

package left_aligned;

import java.util.ArrayList;


public class ScriptDocument implements IElementGroup<SceneGroup> {
	public String type;
	public ArrayList<SceneGroup> members;
	
	public ScriptDocument() {
		members = new ArrayList<SceneGroup>();
		type = "Script Document";
		
	}
	
	public ElementGroup quickAddElement(String type) {
		SceneGroup sGroup = new SceneGroup();
		addMember(sGroup);
		
		return sGroup.quickAddElement(type);
	}
	
	public void addMember(SceneGroup newMember) {
		members.add(newMember);
	}
	
	public void removeMember(int idx) {
    	members.remove(idx);
    }
    
    public void removeMember(SceneGroup sGroup) {
    	members.remove(sGroup);
    }
	
	public ArrayList<SceneGroup> getMembers() {
		return members;
	}
	
	public String getTextContent() {
		String textContent = "";
		
		for (SceneGroup member : members) {
			textContent += member.getTextContent();
		}
		
		return textContent;
	}
	
	public void accept(ISceneGroupVisitor visitor) {
		visitor.visit(this);
		for (SceneGroup sGroup : members) {
			sGroup.accept(visitor);
			visitor.visit(sGroup);
		}
	}	
	
	public void accept(IElementGroupVisitor visitor) {
		for (SceneGroup sGroup : members) {
			sGroup.accept(visitor);
		}
	}	
	
	public void accept(IElementVisitor visitor) {
		for (SceneGroup sGroup : members) {
			sGroup.accept(visitor);
		}
	}
	
	public void accept(ITextVisitor visitor) {
		accept(new IElementVisitor() {
			public void visit(ScriptElement elem) {
				for (Text run : elem.getTextRuns()) {
					run.accept(visitor);
				}
			}
		});
	}
	
	public void trimTextContent() {
		accept(new SceneGroupVisitor() {
			public void visit(ScriptElement elem) {
				elem.trimTextContent();
			}		
		});
		
		removeEmpties();
	}
	
	public void removeEmpties() {
		accept(new SceneGroupVisitor() {
			public void visit(SceneGroup sGroup) {
				sGroup.removeEmpties();
			}	
		});
		
		for (SceneGroup sGroup : this.getMembers()) {
			if (sGroup.getTextContent().length() == 0) {
				removeMember(sGroup);
			}
		}
	}
	
	public void normalize() {
		accept(new SceneGroupVisitor() {
			public void visit(SceneGroup sGroup) {
				sGroup.normalize();
			}	
		});
	}
}


