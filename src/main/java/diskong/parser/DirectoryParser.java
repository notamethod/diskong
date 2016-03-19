package diskong.parser;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import diskong.parser.fileutils.FilePath;

public interface DirectoryParser {

	Map<Path, List<FilePath>> parse(String absolutePath);

}