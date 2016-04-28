package left_aligned;

import java.util.ArrayList;

public class SceneGroup implements IElementGroup<ElementGroup> {
	public String type;
    public SceneProperties sceneProperties;
    public ArrayList<ElementGroup> members;
    
    public SceneGroup() {
    	type = "Scene Group";
		members = new ArrayList<ElementGroup>();
	}
    
    public ElementGroup quickAddElement(String type) {
    	return makeElementGroup(type);
    }
    
    public ElementGroup makeElementGroup(String type) {
		ElementGroup eGroup;
		ScriptElement sElement = null;
		
		switch (type) {
			case "Scene Heading Group":
				eGroup = new SceneHeadingGroup();
				sElement = new SceneHeading();
				break;
			case "Action Group":
				eGroup = new ActionGroup();
				sElement = new Action();
				break;
			case "Dialogue Group":
				eGroup = new DialogueGroup();
				break;
			case "Character":
				eGroup = new DialogueGroup();
				sElement = new CharacterName();
				break;
			case "Dialogue":
				eGroup = new DialogueGroup();
				sElement = new Dialogue();
				break;
			case "Parenthetical":
				eGroup = new DialogueGroup();
				sElement = new Parenthetical();
				break;
			case "Shot Group":
				eGroup = new ShotGroup();
				sElement = new Shot();
				break;
			case "Transition Group":
				eGroup = new TransitionGroup();
				sElement = new Transition();
				break;
			case "General Group":
				eGroup = new GeneralGroup();
				sElement = new General();
				break;
			default:
				eGroup = new GeneralGroup();
				sElement = new General();
				break;
		}
		
		if (sElement != null) eGroup.addMember(sElement);
		addMember(eGroup);
		
		return eGroup;
	}
	
	public void addMember(ElementGroup newMember) {
		members.add(newMember);
	}
	
	public void removeMember(int idx) {
    	members.remove(idx);
    }
    
    public void removeMember(ElementGroup eGroup) {
    	members.remove(eGroup);
    }
	
	public ArrayList<ElementGroup> getMembers() {
		return members;
	}
	
	public String getTextContent() {
		String textContent = "";
		
		for (ElementGroup member : members) {
			textContent += member.getTextContent();
		}
		
		return textContent;
	}
	
	public void accept(ISceneGroupVisitor visitor) {
		visitor.visit(this);
		for (ElementGroup eGroup: members) {
			eGroup.accept(visitor);
			visitor.visit(eGroup);
		}
	}
	
	public void accept(IElementGroupVisitor visitor) {
		for (ElementGroup eGroup: members) {
			eGroup.accept(visitor);
		}
	}
	
	public void accept(IElementVisitor visitor) {
		for (ElementGroup eGroup: members) {
			eGroup.accept(visitor);
		}
	}
	
	public void accept(ITextVisitor visitor) {
		for (ElementGroup eGroup: members) {
			eGroup.accept(visitor);
		}
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
			public void visit(ElementGroup eGroup) {
				eGroup.removeEmpties();
			}
		});
		
		ArrayList<ElementGroup> removables = new ArrayList<ElementGroup>();
		for (ElementGroup eGroup : this.getMembers()) {
			if (eGroup.getTextContent() == null 
					|| eGroup.getTextContent().length() == 0) {
				removables.add(eGroup);
			}
		}
		
		for (ElementGroup removable : removables) {
			removeMember(removable);
		}
	}
	
	public void normalize() {
		accept(new ElementGroupVisitor() {
			public void visit(ElementGroup eGroup) {
				eGroup.normalize();
			}	
		});
	}
}
