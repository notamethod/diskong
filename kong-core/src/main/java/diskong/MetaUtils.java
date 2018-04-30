package diskong;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.XMPDM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MetaUtils {

	public static final Property STYLE = Property.externalText("style");

	public static List<String> getGenre(Metadata metadata) {
		List<String> genres = new ArrayList<>();
		genres.addAll(Arrays.asList(metadata.getValues(XMPDM.GENRE)));
		return genres;

	}

	public static List<String> getStyle(Metadata metadata) {
		List<String> styles = new ArrayList<>();
		Collections.addAll(styles, metadata.getValues("style"));
		return styles;
	}

	//refactoring: setTag
	public static void setGenre(IAlbumVo source, Metadata metadata) {

		metadata.set(XMPDM.GENRE, source.getGenres().toArray(new String[source.getGenres().size()]));

	}

	public static void setStyle(IAlbumVo source, Metadata metadata) {

		metadata.set(STYLE, source.getStyles().toArray(new String[source.getStyles().size()]));

	}

}
