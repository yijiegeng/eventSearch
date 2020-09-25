package com.yijie.service;

import com.yijie.entity.FavItem;
import com.yijie.entity.Favourite;
import com.yijie.entity.Item;
import com.yijie.entity.User;
import com.yijie.feign.SearchByIdFeign;
import com.yijie.repository.FavItemRepository;
import com.yijie.repository.FavRepository;
import com.yijie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class UserRecord {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FavRepository favRepository;
    @Autowired
    private FavItemRepository favItemRepository;
    @Autowired
    private SearchByIdFeign searchByIdFeign;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findById(String id){
        Optional<User> res = userRepository.findById(id);
        if(!res.isPresent()) return null;
        return res.get();
    }

    public User save(User user){
        user = userRepository.save(user);
        favRepository.save(createFav(user));
        return user;
    }

    public void deleteById(String id){
        userRepository.deleteById(id);
        favRepository.deleteById(id);
    }

    /**
     * @return users_favourite_record
     */
    public Favourite getFav(String userId){
        Optional<Favourite> recordStatus = favRepository.findById(userId);
        if(!recordStatus.isPresent()) return null;
        else return recordStatus.get();
    }

    /**
     * @return users_favourite_record
     */
    public Favourite setFav(String userId, String itemId){
        Optional<Favourite> recordStatus = favRepository.findById(userId);
        if(!recordStatus.isPresent()) return null;

        /**
         * Call feign to search event by ID
         */
        Item item;
        List<Item> searchRes = searchByIdFeign.getItemsById(itemId);
        if(searchRes == null || searchRes.size() == 0) item = null;
        else item = searchRes.get(0);


        // Save favourite record in "fav" table
        Favourite record = recordStatus.get();
        record.addItem(itemId, item);
        favRepository.save(record);

        // Save favourite item in "fav_item" table
        if(item != null){
            FavItem favItem;
            Optional<FavItem> favItemStatus = favItemRepository.findById(itemId);
            if(!favItemStatus.isPresent()) favItem = createFavItem(item);
            else favItem = favItemStatus.get();

            favItem.addHit(userId);
            favItemRepository.save(favItem);
        }
        return record;
    }


    /**
     * @return users_favourite_record
     */
    public Favourite unsetFav(String userId, String itemId) {
        Optional<Favourite> recordStatus = favRepository.findById(userId);
        if (!recordStatus.isPresent()) return null;

        // Delete favourite record in "fav" table
        Favourite record = recordStatus.get();
        record.removeItem(itemId);
        favRepository.save(record);


        // Delete/modify favourite item in "fav_item" table
        Optional<FavItem> favItemStatus = favItemRepository.findById(itemId);
        if(favItemStatus.isPresent()){
            FavItem favItem = favItemStatus.get();
            favItem.recudeHit(userId);
            if(favItem.getHit() == 0) favItemRepository.delete(favItem);
            else favItemRepository.save(favItem);
        }
        return record;
    }


    /***************************************
     *  Helper function -- for "save" method
     */
    private Favourite createFav(User user){
        Favourite fav = new Favourite();
        fav.setUserId(user.getUserId());
        fav.setFirstName(user.getFirstName());
        fav.setItems(new HashMap<>());
        return fav;
    }

    /*****************************************
     *  Helper function -- for "setFav" method
     */
    private FavItem createFavItem(Item item){
        FavItem favItem = new FavItem();
        favItem.setItemId(item.getItemId());
        favItem.setInfo(item);
        favItem.setHitUsers(new HashSet<>());
        return favItem;
    }
}
