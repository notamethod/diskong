package diskong.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class NioDirectoryParser {

	static int cpt=0;
	public static void main(String[] args) {
		NioDirectoryParser dp = new NioDirectoryParser();
		
		Path dir = FileSystems.getDefault().getPath( args[0] );
		long startTime = System.currentTimeMillis();
		
		
		dp.parse(dir);
		long endTime = System.currentTimeMillis();
		System.out.println("file count "+cpt);
	    System.out.println("duration " + (endTime-startTime)/1000);
	}

	private void parse(Path dir) {
		
		
		try (DirectoryStream<Path>  stream = Files.newDirectoryStream( dir ) ){
			for (Path path : stream) {
				if (Files.isDirectory(path)){
					parse(path);
				}else{
					traiterFichier( path );
					cpt++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void traiterFichier(Path path) {
		//System.out.println(path.getFileName());

	}
}
