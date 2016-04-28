package left_aligned;

import java.util.Arrays;

public class Text {
    /*
        Supported styles:
        - "AllCaps"
        - "Bold"
        - "Italic"
        - "Underline"
        
        Since unsupported styles will be ignored by FB there is no 
        need to validate. 
    */
	public ScriptElement parent;
	private String parentType; // Used if an orphan and type manually set;
	private boolean isOrphan = true;
    private String style;
    private String textContent;
    
    public int _startIndex;
    public int _endIndex;
    
    public Text() {
        setTextContent(""); 
    }
    
    public Text(String text, String style, String type) {
    	setTextContent(text);
    	setStyle(style);
    	parentType = type;
    }
    
    public Text(String[] textStyleTuple) {
    	setTextContent(textStyleTuple[0]);
    	textStyleTuple[0] = "";
    	setStyle(textStyleTuple);
    }
    
    public void setParent(ScriptElement parent) {
    	this.parent = parent;
    	this.isOrphan = false;
    }
    
    public void removeParent() {
    	this.parent = null;
    	this.isOrphan = true;
    }
    
    public void setStyle(String styles) {
    	if (styles == null) return;
        String[] stylesArr = styles.split(" ");
        this.setStyle(stylesArr);
    }
    
    public void setStyle(String[] styles) {
    	Arrays.sort(styles);
        String styleStr = String.join(" ", styles).trim();
        if (styleStr.length() == 0) {
            this.style = null;
            return;
        }
        
        this.style = styleStr;  
    }
    
    public String getStyle() {
        return style;
    }
    
    public String getTextContent() {
        return textContent;
    }
    
    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
    
    public String getParentType() {
    	if (isOrphan) return parentType;
    	return parent.type;
    }
    
    public Text clone() {
    	Text clone = new Text();
    	clone.setTextContent(getTextContent());
    	clone.setStyle(getStyle());
    	
    	return clone;
    }
    
    public void removeNewline() {
    	textContent = textContent.replaceAll("/n|/n/r", " ");
    }
    
    public void accept(ITextVisitor visitor) {
    	visitor.visit(this);
    }
}
