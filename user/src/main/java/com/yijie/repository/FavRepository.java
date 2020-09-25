package com.yijie.repository;

import com.yijie.entity.Favourite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavRepository extends MongoRepository<Favourite, String> {
//    public void update(String userId, String itemId);
}
