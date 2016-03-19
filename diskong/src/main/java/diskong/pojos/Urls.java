package diskong.pojos;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class Urls {

private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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