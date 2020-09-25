package com.yijie.controller;

import com.yijie.entity.Item;
import com.yijie.repository.ItemRepository;
import com.yijie.service.TicketMasterAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchHandler {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private TicketMasterAPI ticketMasterAPI;


    /**
     *  Search based on geo-point
     */
    @GetMapping("/{lat}/{lon}")
    public List<Item> getItems(@PathVariable("lat") double lat, @PathVariable("lon") double lon) {
        List<Item> items = ticketMasterAPI.search(lat, lon, null, null,null);
        saveItems(items);
        return items;
    }


    /**
     *  Search based on state
     */
    @GetMapping("/state/{state_code}")
    public List<Item> getItemsByState(@PathVariable("state_code") String stateCode){
        List<Item> items = ticketMasterAPI.search(null, null, null, stateCode,null);
        saveItems(items);
        return items;
    }

    /**
     *  Search based on city
     */
    @GetMapping("/city/{city_name}")
    public List<Item> getItemsByCity(@PathVariable("city_name") String city){
        List<Item> items = ticketMasterAPI.search(null, null, null, null,city);
        saveItems(items);
        return items;
    }


    /**
     *      --This API is for Recommendation--
     *  Search based on category and geo-point/state/city and
     */
    @GetMapping("/term/{lat}/{lon}/{term}/{state_code}/{city}")
    public List<Item> getItemsByTerm(@PathVariable("lat") Double lat,
                                     @PathVariable("lon") Double lon,
                                     @PathVariable("term") String term,
                                     @PathVariable("state_code") String stateCode,
                                     @PathVariable("city") String city){

        if(lat == 0.0) lat = null;
        if(lon == 0.0) lon = null;
        if(stateCode.equals("0")) stateCode = null;
        if(city.equals("0")) city = null;
        List<Item> items = ticketMasterAPI.search(lat, lon, term, stateCode,city);
        return items;
    }

    /**
     *  Helper function
     */
    private void saveItems(List<Item> items){
        itemRepository.saveAll(items);
    }
}
