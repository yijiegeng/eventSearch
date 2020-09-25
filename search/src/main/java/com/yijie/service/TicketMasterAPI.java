package com.yijie.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.mapper.Mapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yijie.entity.Item;
import org.springframework.stereotype.Service;


@Service
public class TicketMasterAPI {
    // ROOTAPI - from ticketMaster
    private static final String URL = "http://app.ticketmaster.com/discovery/v2/events.json";
    // default keyword
    private static final String DEFAULT_VALUE = "";

    // account keyword
    private static final String API_KEY = "oCjfH7tovu3ikA0oJFozQ0b3CGPESVc8";


    /**
     * Helper methods
     */

    //  {
    //    "name": "laioffer",
    //    "id": "12345",
    //    "url": "www.laioffer.com",
    //    ...
    //    "_embedded": {
    //	    "venues": [
    //	        {
    //		        "address": {
    //		           "line1": "101 First St.",
    //		           "line2": "Suite 101",
    //		           "line3": "...",
    //		        },
    //		        "city": {
    //		        	"name": "San Francisco"
    //		        }
    //		        ...
    //	        },
    //	        ...
    //	    ]
    //    }
    //    ...
    //  }

    //////////// helper function
    private String getAddress(JSONObject event) throws JSONException {
        if (!event.isNull("_embedded")) {
            JSONObject embedded = event.getJSONObject("_embedded");

            if (!embedded.isNull("venues")) {
                JSONArray venues = embedded.getJSONArray("venues");

                // Traverse every venue
                for (int i = 0; i < venues.length(); ++i) {
                    JSONObject venue = venues.getJSONObject(i);

                    StringBuilder sb = new StringBuilder();

                    if (!venue.isNull("address")) {
                        JSONObject address = venue.getJSONObject("address");

                        if (!address.isNull("line1")) {
                            sb.append(address.getString("line1"));
                        }
                        if (!address.isNull("line2")) {
                            sb.append(" ");
                            sb.append(address.getString("line2"));
                        }
                        if (!address.isNull("line3")) {
                            sb.append(" ");
                            sb.append(address.getString("line3"));
                        }
                    }

                    if (!venue.isNull("city")) {
                        JSONObject city = venue.getJSONObject("city");

                        if (!city.isNull("name")) {
                            sb.append(" ");
                            sb.append(city.getString("name"));
                        }
                    }

                    if (!sb.toString().equals("")) {
                        return sb.toString();
                    }
                }
            }
        }

        return "";
    }


    // {"images": [{"url": "www.example.com/my_image.jpg"}, ...]}
    private String getImageUrl(JSONObject event) throws JSONException {
        if (!event.isNull("images")) {
            JSONArray images = event.getJSONArray("images");

            for (int i = 0; i < images.length(); ++i) {
                JSONObject image = images.getJSONObject(i);

                if (!image.isNull("url")) {
                    return image.getString("url");
                }
            }
        }

        return "";
    }

    // {"classifications" : [{"segment": {"name": "music"}}, ...]}
    private Set<String> getCategories(JSONObject event) throws JSONException {
        Set<String> categories = new HashSet<>();
        if (!event.isNull("classifications")) {
            JSONArray classifications = event.getJSONArray("classifications");
            for (int i = 0; i < classifications.length(); i++) {
                JSONObject classification = classifications.getJSONObject(i);
                if (!classification.isNull("segment")) {
                    JSONObject segment = classification.getJSONObject("segment");
                    if (!segment.isNull("name")) {
                        String name = segment.getString("name");
                        categories.add(name);
                    }
                }
            }
        }

        return categories;
    }

    //////////////// Convert JSONArray to a list of item objects.
    private List<Item> getItemList(JSONArray events) throws JSONException {
        List<Item> itemList = new ArrayList<>();

        for (int i = 0; i < events.length(); ++i) {
            JSONObject event = events.getJSONObject(i);

            Item.ItemBuilder builder = new Item.ItemBuilder();

            if (!event.isNull("name")) {
                builder.setName(event.getString("name"));
            }

            if (!event.isNull("id")) {
                builder.setItemId(event.getString("id"));
            }

            if (!event.isNull("url")) {
                builder.setUrl(event.getString("url"));
            }

            if (!event.isNull("rating")) {
                builder.setRating(event.getDouble("rating"));
            }

            if (!event.isNull("distance")) {
                builder.setDistance(event.getDouble("distance"));
            }

            // those Three field are stored in deeper level
            // so create helper function to parse
            builder.setCategories(getCategories(event));
            builder.setAddress(getAddress(event));
            builder.setImageUrl(getImageUrl(event));

            itemList.add(builder.build());
        }

        return itemList;
    }


    private String check(String content){
        // keyword HAS TO exist
        if(content == null) {
            content = DEFAULT_VALUE;
        }
        // transform the uniform of KEYWORD
        try {
            content= java.net.URLEncoder.encode(content, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }


    ///////////////// lat + lon  -->   JSONArray
    public List<Item> search(Double lat, Double lon, String keyword, String stateCode, String city) {


        keyword = check(keyword);
        stateCode = check(stateCode);
        city = check(city);

        // call GeoHash function -> get hashCode
        String geoHash = "";
        if(stateCode == "" && city == "")
            geoHash = GeoHash.encodeGeohash(lat, lon, 8);

        int size = 50;
        if(keyword != "") size = 30;

        // finish query in that format
        String query = String.format("apikey=%s&geoPoint=%s&classificationName=%s&stateCode=%s&city=%s&radius=%s&size=%s", API_KEY, geoHash, keyword, stateCode, city, 50, size);

        // create connection
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query).openConnection();

            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            System.out.println("\nSending 'Get' request to URL :" + URL + "?" + query);
            System.out.println("Response code: " + responseCode);

            if(responseCode != 200) {
                //
            }

            // create a buffer to contain input
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputline;
            StringBuilder response = new StringBuilder();

            // read input line-by-line
            while((inputline = in.readLine()) != null) {
                response.append(inputline);
            }
            in.close();

            // now all the input-data in stored in JSON
            JSONObject obj = new JSONObject(response.toString());
            // if no "_embedded" --> empty
            if (obj.isNull("_embedded")) {
                return new ArrayList<>();
            }

            JSONObject embedded = obj.getJSONObject("_embedded");
            JSONArray events = embedded.getJSONArray("events");

            ////// get FINAL result ////
            return getItemList(events);

        }catch(Exception e){
            e.printStackTrace();
        }

        return new ArrayList<>();
    }


    /**
     *  use for debug
     */
    private void queryAPI(double lat, double lon) {
        List<Item> events = search(lat, lon, null, null, null);
        try {
//		    for (int i = 0; i < events.size(); i++) {
//		        Item event = events.get(i);
//		        System.out.println(event);
//		    }
            // traverse every event
            for(Item event : events) {
                System.out.println(event.toJSONObject()); // transform to JSON
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main entry for sample TicketMaster API requests.
     * Use for Debug
     */
    public static void main(String[] args) {
        TicketMasterAPI tmApi = new TicketMasterAPI();
        // Mountain View, CA
        // tmApi.queryAPI(37.38, -122.08);
        // London, UK
        // tmApi.queryAPI(51.503364, -0.12);
        // Houston, TX
        tmApi.queryAPI(29.682684, -95.295410);
    }
}

