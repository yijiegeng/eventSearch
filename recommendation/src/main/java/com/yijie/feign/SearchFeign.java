package com.yijie.feign;

import com.yijie.entity.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "search")
public interface SearchFeign {
    @GetMapping("/search/term/{lat}/{lon}/{term}/{state_code}/{city}/{size}")
    public List<Item> getItemsByTerm(@PathVariable("lat") Double lat,
                                     @PathVariable("lon") Double lon,
                                     @PathVariable("term") String term,
                                     @PathVariable("state_code") String stateCode,
                                     @PathVariable("city") String city,
                                     @PathVariable("size") Integer size);
}