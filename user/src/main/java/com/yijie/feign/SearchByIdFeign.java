package com.yijie.feign;

import com.yijie.entity.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
@FeignClient(value = "search")
public interface SearchByIdFeign {
    @GetMapping("search/id/{id}")
    public List<Item> getItemsById(@PathVariable("id") String id);
}
