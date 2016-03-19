
package diskong.pojos2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    "have",
    "want",
    "rating",
    "submitter",
    "contributors"
})
public class Community {

    @JsonProperty("have")
    private Integer have;
    @JsonProperty("want")
    private Integer want;
    @JsonProperty("rating")
    private Rating rating;
    @JsonProperty("submitter")
    private Submitter submitter;
    @JsonProperty("contributors")
    private List<Contributor> contributors = new ArrayList<Contributor>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("have")
    public Integer getHave() {
        return have;
    }

    @JsonProperty("have")
    public void setHave(Integer have) {
        this.have = have;
    }

    @JsonProperty("want")
    public Integer getWant() {
        return want;
    }

    @JsonProperty("want")
    public void setWant(Integer want) {
        this.want = want;
    }

    @JsonProperty("rating")
    public Rating getRating() {
        return rating;
    }

    @JsonProperty("rating")
    public void setRating(Rating rating) {
        this.rating = rating;
    }

    @JsonProperty("submitter")
    public Submitter getSubmitter() {
        return submitter;
    }

    @JsonProperty("submitter")
    public void setSubmitter(Submitter submitter) {
        this.submitter = submitter;
    }

    @JsonProperty("contributors")
    public List<Contributor> getContributors() {
        return contributors;
    }

    @JsonProperty("contributors")
    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
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
