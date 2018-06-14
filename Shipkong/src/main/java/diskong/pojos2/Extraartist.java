
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

package diskong.pojos2;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "tracks",
    "role",
    "anv",
    "join",
    "name",
    "resource_url"
})
public class Extraartist {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("tracks")
    private String tracks;
    @JsonProperty("role")
    private String role;
    @JsonProperty("anv")
    private String anv;
    @JsonProperty("join")
    private String join;
    @JsonProperty("name")
    private String name;
    @JsonProperty("resource_url")
    private String resource_url;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("tracks")
    public String getTracks() {
        return tracks;
    }

    @JsonProperty("tracks")
    public void setTracks(String tracks) {
        this.tracks = tracks;
    }

    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    @JsonProperty("role")
    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("anv")
    public String getAnv() {
        return anv;
    }

    @JsonProperty("anv")
    public void setAnv(String anv) {
        this.anv = anv;
    }

    @JsonProperty("join")
    public String getJoin() {
        return join;
    }

    @JsonProperty("join")
    public void setJoin(String join) {
        this.join = join;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("resource_url")
    public String getResource_url() {
        return resource_url;
    }

    @JsonProperty("resource_url")
    public void setResource_url(String resource_url) {
        this.resource_url = resource_url;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
