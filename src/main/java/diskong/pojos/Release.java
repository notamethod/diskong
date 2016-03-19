package diskong.pojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class Release {

private Pagination pagination;
private List<Result> results = new ArrayList<Result>();
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

public Pagination getPagination() {
return pagination;
}

public void setPagination(Pagination pagination) {
this.pagination = pagination;
}

public List<Result> getResults() {
return results;
}

public void setResults(List<Result> results) {
this.results = results;
}

@Override
public String toString() {
return ToStringBuilder.reflectionToString(this);
}

public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

public void setAdditionalProperties(String name, Object value) {
this.additionalProperties.put(name, value);
}

}