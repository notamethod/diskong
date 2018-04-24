package diskong.demo;

import java.io.File;
import java.io.IOException;

public class DirectoryParser {

	static int cpt=0;
	public static void main(String[] args) {
		DirectoryParser dp = new DirectoryParser();
		
		String dir =  args[0];
		long startTime = System.currentTimeMillis();
		
		
		dp.parse(dir);
		long endTime = System.currentTimeMillis();
		System.out.println("nb fichiers "+cpt);
	    System.out.println("temps " + (endTime-startTime)/1000);
	}

	private void parse(String path) {
		File root = new File(path);
		File[] childs = root.listFiles();
		for (File f : childs) {
			if (f.isDirectory()) {
				try {
					parse(f.getCanonicalPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (f.isFile()) {
				traiterFichier(f);
				cpt++;
			}
		}

	}

	private void traiterFichier(File f) {
		//System.out.println(f.getPath());

	}
}
