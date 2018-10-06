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

package diskong.tag.metatag;

import org.apache.tika.metadata.Property;

import java.util.ArrayList;
import java.util.List;

public class Arguments {

	private StringBuffer buf = new StringBuffer();
	private List<String> liste = new ArrayList<>();
	
	public void add(ArgAction actionTag, Property prop) {
		StringBuilder buf = new StringBuilder();
		buf.append(actionTag.getString()).append(prop.getName().contains(":")?prop.getName().split(":")[1]:prop.getName());//.append(" ");
		liste.add(buf.toString());
		
	}

	public void add(ArgAction actionTag, String prop) {
		StringBuilder buf = new StringBuilder();
		buf.append(actionTag.getString()).append(prop.contains(":")?prop.split(":")[1]:prop);//.append(" ");
		liste.add(buf.toString());

	}
	public void add(ArgAction actionTag, Property prop, String value) {
		StringBuilder buf = new StringBuilder();
		
		buf.append(actionTag.getString()).append(prop.getName().contains(":")?prop.getName().split(":")[1]:prop.getName()).append("=").append(value);
		liste.add(buf.toString());
		
	}

	public void add(ArgAction actionTag, String key, String value) {
		StringBuilder buf = new StringBuilder();

		buf.append(actionTag.getString()).append(key.contains(":")?key.split(":")[1]:key).append("=").append(value);
		liste.add(buf.toString());

	}
	public String flatten() {
		// TODO Auto-generated method stub
		return buf.toString();
	}
	
	public List<String> getList() {
		// TODO Auto-generated method stub
		return liste;
	}
	
}
