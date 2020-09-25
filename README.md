# eventSearch
* This is a real-time event search web app.

* This app is based on **Spring cloud** and **spring boot**.

* The event data source is **`ticketMaster API`**: [Discovery API](https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/).

  

## Version 2.0	--	modified the database table

### Recomendation algorithm

* Using the user's favorite records and find out all the categories that the user likes. According to **their preference**, search for similar events and recommend them to the user. 
* Sort the recomendation events by according to **how much users like the category**.
* If call recommendation based on **geo-location**, then sort the recomendation events in same category by **distance**.

### Data Base : MongoDB

* `users`: Stored  all user's persional information

  ```json
  {
      "userId": "5f6bdea318d55516a3871a72",
      "firstName": "xxxx",
      "lastName": "xxxx",
      "password": "123"
  }
  ```

* `fav_items`: Stored all events that **favourited** by the user

  ```json
  {
  	"itemId": "vv1AaZAqAGkdPXfSW",
      "info": {
          "itemId": "vv1AaZAqAGkdPXfSW",
                  "name": "Eagles",
                  "description": null,
                  "address": "3900 W Manchester Blvd. Inglewood",
                  "categories": ["Music"],
                  "imageUrl": "https://s1.ticketm.net/dam/a/c40/e0f4dedd-b435-4b8b-8fd0-e73e47e93c40_851341_CUSTOM.jpg",
                  "url": "https://www.ticketmaster.com/eagles-inglewood-california-10-16-2021/event/09005745E5F94CFD",
                  "distance": 0.0
      },
      "hitUsers": ["5f6bdea318d55516a3871a72", "5f6bdec418d55516a3871a73"],
      "hit": 2
  }
  ```

  

* `fav` : Stored all favourite record of each user :

  ```json
  {
      "userId": "5f6bdea318d55516a3871a72",
      "firstName": "yijie",
      "items": {
          "vv1AaZAqAGkdPXfSW": {
              "description": "Active!",
              "item": {
                  "itemId": "vv1AaZAqAGkdPXfSW",
                  "name": "Eagles",
                  "description": null,
                  "address": "3900 W Manchester Blvd. Inglewood",
                  "categories": ["Music"],
                  "imageUrl": "https://s1.ticketm.net/dam/a/c40/e0f4dedd-b435-4b8b-8fd0-e73e47e93c40_851341_CUSTOM.jpg",
                  "url": "https://www.ticketmaster.com/eagles-inglewood-california-10-16-2021/event/09005745E5F94CFD",
                  "distance": 0.0
              }
        }
  }
  ```

  

### API

* **`user`** : 

  * `GET - user/findAll` ---- Find all users

  * `GET - user/findById/{user_id}` ---- Find user by id

  * `POST - user/save` (with JSON body) :	

    1. Save a new user OR update the user's information
    2. Create a `favourite` record in `fav` table

  * `DELETE - user/deleteById/{user_id}`:

    1. Delete user by id
    2. Delete the `favourite` record in `fav` table

    ---

  * `Get - /user/getFav/{user_id}`---- Get user's favourite record in `fav` table

  * `Get - /user/setFav/{user_id}/{item_id}` :

    1. Call **`search`** service，using `ticketMaster API` to get the information of the item
    2. Add the item into an user's favourite list in `fav` table
    3. Add the item into `fav_items` table
       * If this item exist (i.e. favourited by other user), then count- number + 1
       * Otherwise create a new data

  * `Get - /user/unsetFav/{user_id}/{item_id}` :

    1. Remove the item from an user's favourite list in `fav` table
    2. Remove the item from `fav_items` table
       * If this item count-num > 1 (i.e. favourited by other user), then count-number - 1
       * Otherwise delete this dat

---

* **`search`**:

  * `Get - search/{lat}/{lon}` ---- Call **`ticketMaster API`**,  search event based on **geo-location**

  * `Get - search/state/{state_code}` ---- Call **`ticketMaster API`**,  search event based on **state-code**

  * `Get - search/state/{city}` ---- Call **`ticketMaster API`**,  search event based on **city**

    ---

  * `Get - search/term/{lat}/{lon}/{state_code}/{city}/{size}`: 

    1. *This is designed for **`recomendation`** service*
    2. Call **`ticketMaster API`**,  search event based on **category** and **size** and [lat+lon]() OR [state-code]() OR [city]()

  * `Get - search/id/{id}`: 

    1. *This is designed for **`user`** service*
    2. Call **`ticketMaster API`**,  search event based on **event-id**

---

* **`recommendation`**:
  * `Get - recom/{lat}/{lon}/{user_id}` ---- Call **`search`**service,  recommend event based on **geo-location**
  * `Get - recom/state/{state_code}/{user_id}` ---- Call  **`search`**service,  recommend event based on **state-code**
  * `Get - recom/state/{city}/{user_id}` ---- Call  **`search`**service,  recommend event based on **city**



