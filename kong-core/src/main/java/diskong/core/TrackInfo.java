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

package diskong.core;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.jetbrains.annotations.NotNull;


public class TrackInfo implements Comparable<TrackInfo> {
	public Metadata getMetadata() {
		return metadata;
	}

	public FilePath getfPath() {
		return fPath;
	}


	private final Metadata metadata;
	private FilePath fPath;

	public TrackInfo(@NotNull Metadata metadata) {
		this.metadata=metadata;
	}

	public TrackInfo(FilePath fPath, @NotNull Metadata metadata) {
		this.metadata=metadata;
		this.fPath=fPath;
		
	}

	public String getTitle() {
		return metadata.get(TikaCoreProperties.TITLE);
	}

    public String getNumber() {
        return metadata.get(XMPDM.TRACK_NUMBER);
    }

    public Object getArtist() {
        return metadata.get(XMPDM.ARTIST);
    }

    @Override
    public int compareTo(@NotNull TrackInfo o) {


	    if (this.getNumber() !=null) {
            String thisNumber = this.getNumber().replaceAll("\\.", "");
            if (o.getNumber() != null) {
                String otherNumber = o.getNumber().replaceAll("\\.", "");
                return (Integer.valueOf(thisNumber) - Integer.valueOf(otherNumber));
            }
        }
        return 0;
    }
}
