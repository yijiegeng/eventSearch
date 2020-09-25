package com.yijie.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document(collection = "fav_items")
public class FavItem {
    @Id
    private String itemId;
    private Item info;
    private Set<String> hitUsers;
    private int hit;

    public void addHit(String userId){
        hitUsers.add(userId);
        hit = hitUsers.size();
    }

    public void recudeHit(String userId){
        hitUsers.remove(userId);
        hit = hitUsers.size();
    }
}
