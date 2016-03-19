package diskong.pojos.old;
//package diskong.pojos;
//
//-----------------------------------com.example.Pagination.java-----------------------------------
//
//package com.example;
//
//import java.util.HashMap;
//import java.util.Map;
//import javax.annotation.Generated;
//import com.fasterxml.jackson.annotation.JsonAnyGetter;
//import com.fasterxml.jackson.annotation.JsonAnySetter;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//
//@JsonInclude(JsonInclude.Include.NON_NULL)
//@Generated("com.googlecode.jsonschema2pojo")
//@JsonPropertyOrder({
//"page",
//"pages",
//"items",
//"per_page",
//"urls"
//})
//public class Pagination {
//
//@JsonProperty("page")
//private Integer page;
//@JsonProperty("pages")
//private Integer pages;
//@JsonProperty("items")
//private Integer items;
//@JsonProperty("per_page")
//private Integer per_page;
//@JsonProperty("urls")
//private Urls urls;
//private Map<String, Object> additionalProperties = new HashMap<String, Object>();
//
//@JsonProperty("page")
//public Integer getPage() {
//return page;
//}
//
//@JsonProperty("page")
//public void setPage(Integer page) {
//this.page = page;
//}
//
//@JsonProperty("pages")
//public Integer getPages() {
//return pages;
//}
//
//@JsonProperty("pages")
//public void setPages(Integer pages) {
//this.pages = pages;
//}
//
//@JsonProperty("items")
//public Integer getItems() {
//return items;
//}
//
//@JsonProperty("items")
//public void setItems(Integer items) {
//this.items = items;
//}
//
//@JsonProperty("per_page")
//public Integer getPer_page() {
//return per_page;
//}
//
//@JsonProperty("per_page")
//public void setPer_page(Integer per_page) {
//this.per_page = per_page;
//}
//
//@JsonProperty("urls")
//public Urls getUrls() {
//return urls;
//}
//
//@JsonProperty("urls")
//public void setUrls(Urls urls) {
//this.urls = urls;
//}
//
//@JsonAnyGetter
//public Map<String, Object> getAdditionalProperties() {
//return this.additionalProperties;
//}
//
//@JsonAnySetter
//public void setAdditionalProperties(String name, Object value) {
//this.additionalProperties.put(name, value);
//}
//
//}
//-----------------------------------com.example.Urls.java-----------------------------------
//
//package com.example;
//
//import java.util.HashMap;
//import java.util.Map;
//import javax.annotation.Generated;
//import com.fasterxml.jackson.annotation.JsonAnyGetter;
//import com.fasterxml.jackson.annotation.JsonAnySetter;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//
//@JsonInclude(JsonInclude.Include.NON_NULL)
//@Generated("com.googlecode.jsonschema2pojo")
//@JsonPropertyOrder({
//"first",
//"prev",
//"next",
//"last"
//})
//public class Urls {
//
//@JsonProperty("first")
//private String first;
//@JsonProperty("prev")
//private String prev;
//@JsonProperty("next")
//private String next;
//@JsonProperty("last")
//private String last;
//private Map<String, Object> additionalProperties = new HashMap<String, Object>();
//
//@JsonProperty("first")
//public String getFirst() {
//return first;
//}
//
//@JsonProperty("first")
//public void setFirst(String first) {
//this.first = first;
//}
//
//@JsonProperty("prev")
//public String getPrev() {
//return prev;
//}
//
//@JsonProperty("prev")
//public void setPrev(String prev) {
//this.prev = prev;
//}
//
//@JsonProperty("next")
//public String getNext() {
//return next;
//}
//
//@JsonProperty("next")
//public void setNext(String next) {
//this.next = next;
//}
//
//@JsonProperty("last")
//public String getLast() {
//return last;
//}
//
//@JsonProperty("last")
//public void setLast(String last) {
//this.last = last;
//}
//
//@JsonAnyGetter
//public Map<String, Object> getAdditionalProperties() {
//return this.additionalProperties;
//}
//
//@JsonAnySetter
//public void setAdditionalProperties(String name, Object value) {
//this.additionalProperties.put(name, value);
//}
//
//}