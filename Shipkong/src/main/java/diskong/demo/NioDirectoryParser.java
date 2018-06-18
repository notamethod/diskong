/*
 * Copyright 2018 org.dpr & croger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package diskong.demo;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

class NioDirectoryParser {

	private static int cpt=0;
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
