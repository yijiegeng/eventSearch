package com.yijie.controller;

import com.yijie.entity.FavItem;
import com.yijie.entity.Favourite;
import com.yijie.entity.Item;
import com.yijie.entity.User;
import com.yijie.repository.FavItemRepository;
import com.yijie.service.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
public class UserHandler {

    @Autowired
    private UserRecord userRecord;

    /**
     *  Search in "users" collection
     */
    @GetMapping("/findAll")
    public List<User> findAll(){
        return userRecord.findAll();
    }

    @GetMapping("/findById/{id}")
    public User findById(@PathVariable("id") String id){
        return userRecord.findById(id);
    }

    @PostMapping("/save")
    public User save(@RequestBody User user){
        return userRecord.save(user);
    }

    @PutMapping("/update")
    public User update(@RequestBody User user){
        return userRecord.save(user);
    }

    @DeleteMapping("/deleteById/{id}")
    public void deleteById(@PathVariable("id") String id){
        userRecord.deleteById(id);
    }


    /**
     *  Search in "fav" collection
     */
    @GetMapping("/getFav/{id}")
    public Favourite getFav(@PathVariable("id") String userId){
        return userRecord.getFav(userId);
    }

    @GetMapping("/setFav/{user_id}/{item_id}")
    public Favourite setFav(@PathVariable("user_id") String userId, @PathVariable("item_id") String itemId){
        return userRecord.setFav(userId, itemId);
    }
    @GetMapping("/unsetFav/{user_id}/{item_id}")
    public Favourite unsetFav(@PathVariable("user_id") String userId, @PathVariable("item_id") String itemId){
        return userRecord.unsetFav(userId, itemId);
    }
}
