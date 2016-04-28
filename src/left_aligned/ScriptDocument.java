/* File Name : ElementObjects.java */

package left_aligned;

import java.util.ArrayList;

import com.google.gson.Gson;

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
	
	public ElementGroup createElementGroup(String type) {
		switch (type) {
			case "Character":
			case "Dialogue":
			case "Parenthetical":
				return new DialogueGroup();
			case "Action":
				return new ActionGroup();
			case "General":
				return new GeneralGroup();
			case "Scene Heading":
				return new SceneHeadingGroup();
			case "Transition":
				return new TransitionGroup();
			case "Shot":
				return new ShotGroup();
			default:
				return null;
		
		}
	}
	
	public ScriptElement createElement(String type) {
		switch (type) {
			case "Character":
				return new CharacterName();
			case "Dialogue":
				return new Dialogue();
			case "Parenthetical":
				return new Parenthetical();
			case "Action":
				return new Action();
			case "General":
				return new General();
			case "Scene Heading":
				return new SceneHeading();
			case "Transition":
				return new Transition();
			case "Shot":
				return new Shot();
			default:
				return null;
	
		}
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
	
	public String toJson() {
		class BagOfPrimitives {
			  private int value1 = 1;
			  private String value2 = "abc";
			  private transient int value3 = 3;
			  BagOfPrimitives() {
			    // no-args constructor
			  }
			}

			// Serialization
			BagOfPrimitives obj = new BagOfPrimitives();
			Gson gson = new Gson();
			String json = gson.toJson(obj);  
        System.out.println("Gson: "+json); // FIXME
        
        return json;
    }
}


