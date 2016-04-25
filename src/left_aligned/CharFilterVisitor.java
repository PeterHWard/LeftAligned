package left_aligned;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CharFilterVisitor implements ITextVisitor {
	static final Map<String, String[]> REPLACEMENTS;
	static {
		HashMap<String, String[]> tmp = new HashMap<String, String[]>();
		tmp.put("--", new String[] {"—"});
        REPLACEMENTS = Collections.unmodifiableMap(tmp);
	}
	
	@SuppressWarnings("unchecked")
	public void visit(Text run) {
		String textContent = run.getTextContent();
		
		Set set = REPLACEMENTS.entrySet();
		Iterator reps = set.iterator();
		while(reps.hasNext()) {		
			Map.Entry<String, String[]> repEntry = (Map.Entry<String, String[]>)reps.next();
			for (String bad : repEntry.getValue()) {
				textContent = textContent.replaceAll(bad, repEntry.getKey());
			}
		}
		
		run.setTextContent(textContent);
	}
}
