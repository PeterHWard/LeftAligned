package left_aligned_tests;

import left_aligned.TextStyleTuple;

public final class TextStyleTupleArray {
	private TextStyleTupleArray() {}
	
	public static TextStyleTuple[] textStyleTupleArray(String[][] args) {
		int len = args.length;
		TextStyleTuple[] retVal = new TextStyleTuple[len];
		
		String[] pair;
		for (int i = 0; i < len; i++) {
			pair = args[i];
			retVal[i] = new TextStyleTuple(pair);
		}
		
		return retVal;
	}
	
	public static TextStyleTuple[] textStyleTupleArray(String[] pair) {
		TextStyleTuple[] a = {new TextStyleTuple(pair)};
		return a;
		
	}
}	
