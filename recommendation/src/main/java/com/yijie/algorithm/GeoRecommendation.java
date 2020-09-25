package com.yijie.algorithm;

import java.util.*;
import java.util.Map.Entry;

import com.yijie.entity.FavItem;
import com.yijie.entity.Favourite;
import com.yijie.entity.Item;
import com.yijie.feign.SearchFeign;
import com.yijie.repository.FavItemRepository;
import com.yijie.repository.FavRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
// Recommendation based on geo distance and similar categories.
public class GeoRecommendation {

    @Autowired
    private FavRepository favRepository;
    @Autowired
    private FavItemRepository favItemRepository;
    @Autowired
    private SearchFeign searchFeign;


    public List<Item> recommendItems(String userId, Double lat, Double lon, String stateCode, String city) {
        List<Item> recommendedItems = new ArrayList<>();

        // Step 1 Get all favorite items
        Set<String> favoriteItemIds = getFavoriteItemIds(userId);	// userId --> favoriteItemIds

        int sum = 0;
        // Step 2 Get all categories of favorite items, sort by count
        Map<String, Integer> allCategories = new HashMap<>();
        for (String itemId : favoriteItemIds) {
            Set<String> categories = getCategories(itemId);		// ItemId --> categories
            for (String category : categories) {
                allCategories.put(category, allCategories.getOrDefault(category, 0) + 1);
                sum++;
            }
        }

        List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());


        // Step 3, do search based on category, filter out favorited events, sort by
        // distance
        Set<Item> hashedRes = new HashSet<>();
        for (Entry<String, Integer> category : categoryList) {
            /**
             * Call search service
             */
            double ratio = (category.getValue() * 1.0) / (sum * 1.0);
            int size = (int)(50 * ratio);
            if(size == 0) size = 1;
            System.out.println("!!!!!!!!!!!!!!!");
            System.out.println(size);
            System.out.println("!!!!!!!!!!!!!!!");
            List<Item> items = getItems(lat, lon, category.getKey(), stateCode, city, size);

            for (Item item : items) {
                if(!favoriteItemIds.contains(item.getItemId()))
                    hashedRes.add(item);
            }
        }
        recommendedItems.addAll(hashedRes);

        // If recommend by state/city, then NO need for sorting
        if(lat != 0.0 || lon != 0.0){
            Collections.sort(recommendedItems,
                    (a, b) -> a.getDistance() < b.getDistance() ? -1
                            : (a.getDistance() > b.getDistance() ? 1 : 0));
        }
        return recommendedItems;
    }


    /**
     * Helper function
     * 1. getItems: call search service
     * 2. getFavoriteItemIds: search DB in collection "users"
     * 3. getCategories: search DB in collection "items"
     */
    public List<Item> getItems(Double lat, Double lon, String term, String stateCode, String city, Integer size){
        return searchFeign.getItemsByTerm(lat, lon, term, stateCode, city, size);
    }

    public Set<String> getFavoriteItemIds(String userId){
        Set<String> favoriteItems = new HashSet<>();

        // Search favourite-record in "fav" table
        Optional<Favourite> res = favRepository.findById(userId);
        if(!res.isPresent()) return favoriteItems;

        // Search and insert itemId using [record.getAllIds] method
        Favourite record = res.get();
        record.getAllIds(favoriteItems);    // using method defined in "favourite" class

        return favoriteItems;
    }

    public Set<String> getCategories(String itemId){
        Set<String> categories = new HashSet<>();

        // Search favItem in "fav_item" table
        Optional<FavItem> res = favItemRepository.findById(itemId);
        if(!res.isPresent()) return categories;

        // Get categories in [favItem.info.categories]
        FavItem favItem = res.get();
        categories.addAll(favItem.getInfo().getCategories());
        return categories;
    }
}