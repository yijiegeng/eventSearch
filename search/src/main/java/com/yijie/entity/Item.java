package com.yijie.entity;

import lombok.Data;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document(collection = "items")
public class Item {
    @Id
    private String itemId;
    private String name;
    private String description;
    private String address;
    private Set<String> categories;
    private String imageUrl;
    private String url;
    private double distance;

    // Getter
    public String getItemId() {
        return itemId;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getAddress() {
        return address;
    }
    public Set<String> getCategories() {
        return categories;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getUrl() {
        return url;
    }
    public double getDistance() {
        return distance;
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("item_id", itemId);
            obj.put("name", name);
            obj.put("description", description);
            obj.put("address", address);
            obj.put("categories", new JSONArray(categories));
            obj.put("image_url", imageUrl);
            obj.put("url", url);
            obj.put("distance", distance);
        }catch(JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }


    //////// INNER class ////////////
    public static class ItemBuilder{
        // Setter
        public void setItemId(String itemId) {
            this.itemId = itemId;
        }
        public void setName(String name) {
            this.name = name;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public void setAddress(String address) {
            this.address = address;
        }
        public void setCategories(Set<String> categories) {
            this.categories = categories;
        }
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public void setDistance(double distance) {
            this.distance = distance;
        }

        private String itemId;
        private String name;
        private String description;
        private String address;
        private Set<String> categories;
        private String imageUrl;
        private String url;
        private double distance;

        public Item build() {
            return new Item(this);
        }
    }

    /**
     * This is a !!!builder pattern!!! in Java.
     */
    // constructor for outer-class "Item"
    private Item(ItemBuilder builder) {
        this.itemId = builder.itemId;
        this.name = builder.name;
        this.description = builder.description;
        this.address = builder.address;
        this.categories = builder.categories;
        this.imageUrl = builder.imageUrl;
        this.url = builder.url;
        this.distance = builder.distance;
    }
}
