package diskong.pojos;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class Pagination {

private Integer per_page;
private Integer items;
private Integer page;
private Urls urls;
private Integer pages;
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

public Integer getPer_page() {
return per_page;
}

public void setPer_page(Integer per_page) {
this.per_page = per_page;
}

public Integer getItems() {
return items;
}

public void setItems(Integer items) {
this.items = items;
}

public Integer getPage() {
return page;
}

public void setPage(Integer page) {
this.page = page;
}

public Urls getUrls() {
return urls;
}

public void setUrls(Urls urls) {
this.urls = urls;
}

public Integer getPages() {
return pages;
}

public void setPages(Integer pages) {
this.pages = pages;
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