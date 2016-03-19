
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
    "entity_type",
    "catno",
    "id",
    "entity_type_name",
    "name",
    "resource_url"
})
public class Label {

    @JsonProperty("entity_type")
    private String entity_type;
    @JsonProperty("catno")
    private String catno;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("entity_type_name")
    private String entity_type_name;
    @JsonProperty("name")
    private String name;
    @JsonProperty("resource_url")
    private String resource_url;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("entity_type")
    public String getEntity_type() {
        return entity_type;
    }

    @JsonProperty("entity_type")
    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }

    @JsonProperty("catno")
    public String getCatno() {
        return catno;
    }

    @JsonProperty("catno")
    public void setCatno(String catno) {
        this.catno = catno;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("entity_type_name")
    public String getEntity_type_name() {
        return entity_type_name;
    }

    @JsonProperty("entity_type_name")
    public void setEntity_type_name(String entity_type_name) {
        this.entity_type_name = entity_type_name;
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
