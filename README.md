# eventSearch
* This is a real-time event search web app.

* This app is based on **Spring cloud** and **spring boot**.

* The event data source is **`ticketMaster API`**: [Discovery API](https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/).

  

## Version 1.0

### Recomendation algorithm

* Using the user's favorite records and find out all the categories that the user likes. According to **their preference**, search for similar events and recommend them to the user. 
* Sort the recomendation events by according to **how much users like the category**.
* If call recommendation based on **geo-location**, then sort the recomendation events in same category by **distance**

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

* `items`: Stored all events that **searched** OR **recommended** to the user

  ```json
  {
      "itemId": "vv1AaZAqAGkdPXfSW",
      "name": "Eagles",
      "description": null,
      "address": "3900 W Manchester Blvd. Inglewood",
      "categories": ["Music"],
      "imageUrl": "https://s1.ticketm.net/dam/a/c40/e0f4dedd-b435-4b8b-8fd0-e73e47e93c40_851341_CUSTOM.jpg",
      "url": "https://www.ticketmaster.com/eagles-inglewood-california-10-16-2021/event/09005745E5F94CFD",
      "distance": 8.59
  }
  ```

* `fav` : Stored all favourite record of each user

  ```json
  {
      "userId": "5f6bdea318d55516a3871a72",
      "items": ["vv1AaZAqAGkdPXfSW", "vvG1iZp1kKuKcL", "vvG1iZp1kKCNcN"
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

  * `Get - /user/getFav/{user_id}`:

    1. Get user's favourite record in `fav` table
    2. Search item information in `items`table

  * `Get - /user/setFav/{user_id}/{item_id}` ---- Add item_id into user's favourite list in `fav` table

  * `Get - /user/unsetFav/{user_id}/{item_id}` ---- Remove item_id from user's favourite list in `fav` table

---

* **`search`**:

  * `Get - search/{lat}/{lon}` ---- Call **`ticketMaster API`**,  search event based on **geo-location**

  * `Get - search/state/{state_code}` ---- Call **`ticketMaster API`**,  search event based on **state-code**

  * `Get - search/state/{city}` ---- Call **`ticketMaster API`**,  search event based on **city**

    ---

  * `Get - search/term/{lat}/{lon}/{state_code}/{city}`: 

    1. *This is designed for **`recomendation`** service*
    2. Call **`ticketMaster API`**,  search event based on **category** and [lat+lon]() OR [state-code]() OR [city]()


---

* **`recommendation`**:
  * `Get - recom/{lat}/{lon}/{user_id}` ---- Call **`search`**service,  recommend event based on **geo-location**
  * `Get - recom/state/{state_code}/{user_id}` ---- Call  **`search`**service,  recommend event based on **state-code**
  * `Get - recom/state/{city}/{user_id}` ---- Call  **`search`**service,  recommend event based on **city**



