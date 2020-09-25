package com.yijie.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;
import java.util.Set;


@Data
@Document(collection = "fav")
public class Favourite {
    @Data
    private class ItemInfo{
        String STATUS;
        Item item;
    }
    @Id
    private String userId;
    @Field(value = "first_name")
    private String firstName;
    @Field(value = "items")
    private Map<String, ItemInfo> items;


    /**
     *  Used in "user-service"
     */
    public void addItem(String itemId, Item item){
        if(items.containsKey(itemId)) return;

        ItemInfo itemInfo = new ItemInfo();
        itemInfo.item = item;
        itemInfo.STATUS = item == null ? "Expired" : "Active";
        items.put(itemId, itemInfo);
    }

    public void removeItem(String itemId){
        if(!items.containsKey(itemId)) return;
        items.remove(itemId);
    }

    /**
     *  Used in "recom-service"
     */
    public Set<String> getAllIds(Set<String> set){
        for(Map.Entry<String, ItemInfo> entry : items.entrySet()){
            if(entry.getValue() != null) set.add(entry.getKey());
        }
        return set;
    }
}
