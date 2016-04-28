package left_aligned;

import java.io.File;

public interface IDocumentWriter {
	public boolean write(String filePath);
	public boolean write(File file);
}
