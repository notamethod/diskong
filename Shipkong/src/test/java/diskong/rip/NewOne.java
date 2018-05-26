package diskong.rip;


import org.junit.Test;

import java.net.URISyntaxException;

public class NewOne {


    @Test
    public void test_abcde_conf_loaded() {
        AbcdeHandler ah = null;
        try {
            ah = new AbcdeHandler();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        System.out.println(ah.ripProperties.getProperty("PROPTEST1"));

    }
//    public static void main(String[] args) {
//        NioDirectoryParser dp = new NioDirectoryParser();
//
//        URI uri = null;
//        if (args.length >= 0) {
//            uri = new File("/media/syno/music/Tricky/").toURI();
//        } else {
//            uri = new File(args[0]).toURI();//
//        }
//        // testURL(new File(args[0]));
//        // Path dir = FileSystems.getDefault().getPath(args[0]);
//        long startTime = System.currentTimeMillis();
//
//        dp.parse(uri);
//        long endTime = System.currentTimeMillis();
//        System.out.println("nb fichiers " + cpt);
//        System.out.println("temps " + (endTime - startTime) / 1000);
//
//    }

}
