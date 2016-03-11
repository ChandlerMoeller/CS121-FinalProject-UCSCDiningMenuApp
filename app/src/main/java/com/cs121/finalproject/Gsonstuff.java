
package com.cs121.finalproject;

import java.util.ArrayList;
import java.util.List;

//import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class Gsonstuff {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("tags")
    @Expose
    public List<String> tags = new ArrayList<String>();
    @SerializedName("ingredients")
    @Expose
    public String ingredients;
    @SerializedName("allergens")
    @Expose
    public String allergens;

}
