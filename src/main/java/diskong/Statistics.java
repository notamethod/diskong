package diskong;

import java.util.ArrayList;
import java.util.List;

public class Statistics {


    private List<AlbumVo> missingImages = new ArrayList<>();

    /** Constructeur privé */
    private Statistics()
    {}

    public void addStats(AlbumVo album) {
        if (null==album.getFolderImagePath())
            missingImages.add(album);
    }

    /** Holder */
    private static class StatisticsHolder
    {
        /** Instance unique non préinitialisée */
        private final static Statistics instance = new Statistics();
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static Statistics getInstance()
    {
        return StatisticsHolder.instance;
    }
}
