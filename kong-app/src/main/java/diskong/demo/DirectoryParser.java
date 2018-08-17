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

import java.io.File;
import java.io.IOException;

class DirectoryParser {

	private static int cpt=0;
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
