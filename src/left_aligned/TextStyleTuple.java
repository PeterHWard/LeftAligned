package left_aligned;

import java.util.Arrays;

public class TextStyleTuple {
	public String text;
	public String[] styles;
	
	public TextStyleTuple() {}
	
	public TextStyleTuple(String text) {
		this.text = text;
	}
	
	public TextStyleTuple(String text, String[] styles) {
		this.text = text;
		this.styles = styles;
	}
	
	public TextStyleTuple(String... args) {
		this(args[0]);	
		if (args.length <= 1) return;
		if (args.length == 2) setStyles(args[1]);
		else {
			this.styles = Arrays.copyOfRange(args, 1, args.length); 
		}
	} 
	
	private void setStyles(String styles) {
		this.styles = styles.trim().split(" ");
	}
}
