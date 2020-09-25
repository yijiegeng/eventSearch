package com.yijie.controller;

import com.yijie.algorithm.GeoRecommendation;
import com.yijie.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/recom")
public class RecomHandler {
    @Autowired
    private GeoRecommendation geoRecommendation;

    /**
     *  recommend based on geo-point
     */
    @GetMapping("/{lat}/{lon}/{user_id}")
    public List<Item> recommendByGeo(@PathVariable("lat") double lat,
                                     @PathVariable("lon") double lon,
                                     @PathVariable("user_id") String userId) {
        return geoRecommendation.recommendItems(userId, lat, lon, "0", "0");
        // CANNOT pass "null" because this type cannot be passed to another service
    }

    /**
     *  recommend by state
     */
    @GetMapping("/state/{state_code}/{user_id}")
    public List<Item> recommendByState(@PathVariable("state_code") String stateCode,
                                     @PathVariable("user_id") String userId) {
        return geoRecommendation.recommendItems(userId, 0.0, 0.0, stateCode, "0");
        // CANNOT pass "null" because this type cannot be passed to another service
    }


    /**
     *  recommend by city
     */
    @GetMapping("/city/{city}/{user_id}")
    public List<Item> recommendByCity(@PathVariable("city") String city,
                                     @PathVariable("user_id") String userId) {
        return geoRecommendation.recommendItems(userId, 0.0, 0.0, "0", city);
        // CANNOT pass "null" because this type cannot be passed to another service
    }




    /*******************************
     *  Use for debug
     */
    @GetMapping("/search/{lat}/{lon}")
    public List<Item> getItems(@PathVariable("lat") double lat,
                               @PathVariable("lon") double lon,
                               @PathVariable("term") String term){


        List<Item> items = geoRecommendation.getItems(lat, lon, null, null,null);
        return items;
    }

    @GetMapping("get/{item_id}")
    public Set<String> getCategories(@PathVariable("item_id") String itemId){
        return geoRecommendation.getCategories(itemId);
    }

    @GetMapping("gf/{user_id}")
    public Set<String> getFavoriteItemIds(@PathVariable("user_id")String userId){
        return geoRecommendation.getFavoriteItemIds(userId);
    }
}
