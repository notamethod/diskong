package diskong.pojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class Result {

private List<String> style = new ArrayList<String>();
private String thumb;
private List<String> format = new ArrayList<String>();
    private List<String> image = new ArrayList<String>();
private String country;
private List<String> barcode = new ArrayList<String>();
private String uri;
private List<String> label = new ArrayList<String>();
private String catno;
private String year;
private List<String> genre = new ArrayList<String>();
private String title;
private String resource_url;
private String type;
private Integer id;
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

public List<String> getStyle() {
return style;
}

public void setStyle(List<String> style) {
this.style = style;
}

public String getThumb() {
return thumb;
}

public void setThumb(String thumb) {
this.thumb = thumb;
}

public List<String> getFormat() {
return format;
}

public void setFormat(List<String> format) {
this.format = format;
}

public String getCountry() {
return country;
}

public void setCountry(String country) {
this.country = country;
}

public List<String> getBarcode() {
return barcode;
}

public void setBarcode(List<String> barcode) {
this.barcode = barcode;
}

public String getUri() {
return uri;
}

public void setUri(String uri) {
this.uri = uri;
}

public List<String> getLabel() {
return label;
}

public void setLabel(List<String> label) {
this.label = label;
}

public String getCatno() {
return catno;
}

public void setCatno(String catno) {
this.catno = catno;
}

public String getYear() {
return year;
}

public void setYear(String year) {
this.year = year;
}

public List<String> getGenre() {
return genre;
}

public void setGenre(List<String> genre) {
this.genre = genre;
}

public String getTitle() {
return title;
}

public void setTitle(String title) {
this.title = title;
}

public String getResource_url() {
return resource_url;
}

public void setResource_url(String resource_url) {
this.resource_url = resource_url;
}

public String getType() {
return type;
}

public void setType(String type) {
this.type = type;
}

public Integer getId() {
return id;
}
    public List<String> getImage() {
        return image;
    }
    public void setImage(List<String> image) {
        this.image = image;
    }

public void setId(Integer id) {
this.id = id;
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