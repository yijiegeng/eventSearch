package com.yijie.controller;

import com.yijie.entity.Item;
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
    private TicketMasterAPI ticketMasterAPI;


    /**
     *  Search based on geo-point
     */
    @GetMapping("/{lat}/{lon}")
    public List<Item> getItems(@PathVariable("lat") double lat, @PathVariable("lon") double lon) {
        return ticketMasterAPI.search(
                lat, lon, null, null,null, null, null);
    }


    /**
     *  Search based on state
     */
    @GetMapping("/state/{state_code}")
    public List<Item> getItemsByState(@PathVariable("state_code") String stateCode){
        return ticketMasterAPI.search(
                null, null, null, stateCode,null, null, null);
    }

    /**
     *  Search based on city
     */
    @GetMapping("/city/{city_name}")
    public List<Item> getItemsByCity(@PathVariable("city_name") String city){
        return ticketMasterAPI.search(
                null, null, null, null, city, null, null);
    }


    /********************************************************
     *
     *      --This API is for Recommendation--
     *  Search based on category and geo-point/state/city
     */
    @GetMapping("/term/{lat}/{lon}/{term}/{state_code}/{city}/{size}")
    public List<Item> getItemsByTerm(@PathVariable("lat") Double lat,
                                     @PathVariable("lon") Double lon,
                                     @PathVariable("term") String term,
                                     @PathVariable("state_code") String stateCode,
                                     @PathVariable("city") String city,
                                     @PathVariable("size") Integer size){

        if(lat == 0.0) lat = null;
        if(lon == 0.0) lon = null;
        if(stateCode.equals("0")) stateCode = null;
        if(city.equals("0")) city = null;
        List<Item> items = ticketMasterAPI.search(lat, lon, term, stateCode, city, null, size);
        return items;
    }

    /********************************************************
     *
     *  --This API is for "user" service--
     *  Search based on event-id
     */
    @GetMapping("/id/{id}")
    public List<Item> getItemsByid(@PathVariable("id") String id){
        return ticketMasterAPI.search(
                null, null, null, null, null, id, null);
    }
}
