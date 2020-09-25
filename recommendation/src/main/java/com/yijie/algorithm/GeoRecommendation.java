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

        // Step 2 Get all categories of favorite items, sort by count
        Map<String, Integer> allCategories = new HashMap<>();
        for (String itemId : favoriteItemIds) {
            Set<String> categories = getCategories(itemId);		// ItemId --> categories
            for (String category : categories) {
                // if category exist --> ++
                // if not exist --> create (category, 0) --> ++
                allCategories.put(category, allCategories.getOrDefault(category, 0) + 1);
            }
        }

        List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());

        // Rewrite comparator
        Collections.sort(categoryList, (o1, o2)
                -> Integer.compare(o2.getValue(), o1.getValue()));


        // Step 3, do search based on category, filter out favorited events, sort by
        // distance
        Set<Item> visitedItems = new HashSet<>();


        for (Entry<String, Integer> category : categoryList) {
            /**
             * Call search service
             */
            List<Item> items = getItems(lat, lon, category.getKey(), stateCode, city);


            List<Item> filteredItems = new ArrayList<>();
            Set<Item> filteredItems1 = new HashSet<>();
            for (Item item : items) {
                if (!favoriteItemIds.contains(item.getItemId()) && !visitedItems.contains(item)) {
                    if(lat != null && lon != null) filteredItems.add(item);
                    else filteredItems1.add(item);
                }
            }

            if(lat != null && lon != null){
                // Sort filteredItems by distance
                Collections.sort(filteredItems, new Comparator<Item>() {
                    @Override
                    public int compare(Item item1, Item item2) {
                        return Double.compare(item1.getDistance(), item2.getDistance());
                    }
                });
            }

            visitedItems.addAll(items);
            recommendedItems.addAll(filteredItems);
            recommendedItems.addAll(filteredItems1);
        }
        return recommendedItems;
    }


    /**
     * Helper function
     * 1. getItems: call search service
     * 2. getFavoriteItemIds: search DB in collection "users"
     * 3. getCategories: search DB in collection "items"
     */
    public List<Item> getItems(Double lat, Double lon, String term, String stateCode, String city){
        return searchFeign.getItemsByTerm(lat, lon, term, stateCode, city);
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