package diskong.parser.fileutils;

import java.io.File;
import java.nio.file.Path;

public class FilePath {
	File file;
	Path path;
	public FilePath(File file, Path path) {
		super();
		this.file = file;
		this.path = path;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public Path getPath() {
		return path;
	}
	public void setPath(Path path) {
		this.path = path;
	}

}
