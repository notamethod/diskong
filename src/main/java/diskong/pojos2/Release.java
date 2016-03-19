
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
    "id",
    "title",
    "resource_url",
    "uri",
    "status",
    "data_quality",
    "master_id",
    "master_url",
    "country",
    "year",
    "released",
    "released_formatted",
    "notes",
    "styles",
    "genres",
    "estimated_weight",
    "format_quantity",
    "community",
    "labels",
    "companies",
    "extraartists",
    "videos",
    "artists",
    "formats",
    "images",
    "identifiers",
    "tracklist"
})
public class Release {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("resource_url")
    private String resource_url;
    @JsonProperty("uri")
    private String uri;
    @JsonProperty("status")
    private String status;
    @JsonProperty("data_quality")
    private String data_quality;
    @JsonProperty("master_id")
    private Integer master_id;
    @JsonProperty("master_url")
    private String master_url;
    @JsonProperty("country")
    private String country;
    @JsonProperty("year")
    private Integer year;
    @JsonProperty("released")
    private String released;
    @JsonProperty("released_formatted")
    private String released_formatted;
    @JsonProperty("notes")
    private String notes;
    @JsonProperty("styles")
    private List<String> styles = new ArrayList<String>();
    @JsonProperty("genres")
    private List<String> genres = new ArrayList<String>();
    @JsonProperty("estimated_weight")
    private Integer estimated_weight;
    @JsonProperty("format_quantity")
    private Integer format_quantity;
    @JsonProperty("community")
    private Community community;
    @JsonProperty("labels")
    private List<Label> labels = new ArrayList<Label>();
    @JsonProperty("companies")
    private List<Company> companies = new ArrayList<Company>();
    @JsonProperty("extraartists")
    private List<Extraartist> extraartists = new ArrayList<Extraartist>();
    @JsonProperty("videos")
    private List<Video> videos = new ArrayList<Video>();
    @JsonProperty("artists")
    private List<Artist> artists = new ArrayList<Artist>();
    @JsonProperty("formats")
    private List<Format> formats = new ArrayList<Format>();
    @JsonProperty("images")
    private List<Image> images = new ArrayList<Image>();
    @JsonProperty("identifiers")
    private List<Identifier> identifiers = new ArrayList<Identifier>();
    @JsonProperty("tracklist")
    private List<Tracklist> tracklist = new ArrayList<Tracklist>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("resource_url")
    public String getResource_url() {
        return resource_url;
    }

    @JsonProperty("resource_url")
    public void setResource_url(String resource_url) {
        this.resource_url = resource_url;
    }

    @JsonProperty("uri")
    public String getUri() {
        return uri;
    }

    @JsonProperty("uri")
    public void setUri(String uri) {
        this.uri = uri;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("data_quality")
    public String getData_quality() {
        return data_quality;
    }

    @JsonProperty("data_quality")
    public void setData_quality(String data_quality) {
        this.data_quality = data_quality;
    }

    @JsonProperty("master_id")
    public Integer getMaster_id() {
        return master_id;
    }

    @JsonProperty("master_id")
    public void setMaster_id(Integer master_id) {
        this.master_id = master_id;
    }

    @JsonProperty("master_url")
    public String getMaster_url() {
        return master_url;
    }

    @JsonProperty("master_url")
    public void setMaster_url(String master_url) {
        this.master_url = master_url;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("year")
    public Integer getYear() {
        return year;
    }

    @JsonProperty("year")
    public void setYear(Integer year) {
        this.year = year;
    }

    @JsonProperty("released")
    public String getReleased() {
        return released;
    }

    @JsonProperty("released")
    public void setReleased(String released) {
        this.released = released;
    }

    @JsonProperty("released_formatted")
    public String getReleased_formatted() {
        return released_formatted;
    }

    @JsonProperty("released_formatted")
    public void setReleased_formatted(String released_formatted) {
        this.released_formatted = released_formatted;
    }

    @JsonProperty("notes")
    public String getNotes() {
        return notes;
    }

    @JsonProperty("notes")
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("styles")
    public List<String> getStyles() {
        return styles;
    }

    @JsonProperty("styles")
    public void setStyles(List<String> styles) {
        this.styles = styles;
    }

    @JsonProperty("genres")
    public List<String> getGenres() {
        return genres;
    }

    @JsonProperty("genres")
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    @JsonProperty("estimated_weight")
    public Integer getEstimated_weight() {
        return estimated_weight;
    }

    @JsonProperty("estimated_weight")
    public void setEstimated_weight(Integer estimated_weight) {
        this.estimated_weight = estimated_weight;
    }

    @JsonProperty("format_quantity")
    public Integer getFormat_quantity() {
        return format_quantity;
    }

    @JsonProperty("format_quantity")
    public void setFormat_quantity(Integer format_quantity) {
        this.format_quantity = format_quantity;
    }

    @JsonProperty("community")
    public Community getCommunity() {
        return community;
    }

    @JsonProperty("community")
    public void setCommunity(Community community) {
        this.community = community;
    }

    @JsonProperty("labels")
    public List<Label> getLabels() {
        return labels;
    }

    @JsonProperty("labels")
    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    @JsonProperty("companies")
    public List<Company> getCompanies() {
        return companies;
    }

    @JsonProperty("companies")
    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    @JsonProperty("extraartists")
    public List<Extraartist> getExtraartists() {
        return extraartists;
    }

    @JsonProperty("extraartists")
    public void setExtraartists(List<Extraartist> extraartists) {
        this.extraartists = extraartists;
    }

    @JsonProperty("videos")
    public List<Video> getVideos() {
        return videos;
    }

    @JsonProperty("videos")
    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    @JsonProperty("artists")
    public List<Artist> getArtists() {
        return artists;
    }

    @JsonProperty("artists")
    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    @JsonProperty("formats")
    public List<Format> getFormats() {
        return formats;
    }

    @JsonProperty("formats")
    public void setFormats(List<Format> formats) {
        this.formats = formats;
    }

    @JsonProperty("images")
    public List<Image> getImages() {
        return images;
    }

    @JsonProperty("images")
    public void setImages(List<Image> images) {
        this.images = images;
    }

    @JsonProperty("identifiers")
    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    @JsonProperty("identifiers")
    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

    @JsonProperty("tracklist")
    public List<Tracklist> getTracklist() {
        return tracklist;
    }

    @JsonProperty("tracklist")
    public void setTracklist(List<Tracklist> tracklist) {
        this.tracklist = tracklist;
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
