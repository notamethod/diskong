package diskong.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.XMPDM;

import diskong.AlbumVo;
import diskong.IAlbumVo;

public class MetaUtils {

	public static final Property STYLE = Property.externalText("style");

	public static List<String> getGenre(Metadata metadata) {
		List<String> genres = new ArrayList<>();
		for (String genre : metadata.getValues(XMPDM.GENRE)) {
			genres.add(genre);
		}
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
