package com.yijie.repository;

import com.yijie.entity.FavItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavItemRepository extends MongoRepository<FavItem, String> {
}
