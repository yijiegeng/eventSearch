package db.mongodb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.eq;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;


public class MongoDBConnection implements DBConnection {
	private MongoClient mongoClient;
	private MongoDatabase db;

	public MongoDBConnection() {
		// Connects to local mongoDB server.
		mongoClient = new MongoClient();
		db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
	}

	@Override
	public void close() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}
	
	
	//************* Insert "userId" and "itemIds" to COLLECTION <History>
	// NO MORE TABLE <History> 
	// ,just insert new field <favorite> in COLLECTION <users>
	/*
		{
      		“user_id”: “1111” 
		},
   		{ 
     		$push: {
       			“favorite”: {
         				$each: [“abcd”, “efgh”]
   				}
     		}
   		}
	*/
	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		db.getCollection("users")
		.updateOne( new Document("user_id", userId), 
					new Document("$push", 	// no $pushALL in this version
						new Document("favorite", 
							new Document("$each", itemIds)))); 
	}
	
	
	//************* Delete "userId" and "itemIds" to COLLECTION <History>
	/*
		{
      		“user_id”: “1111” 
   		},
   		{ 
     		$pullAll:  {
        		“favorite”: [“abcd”, “efgh”],
     		}
   		}
	 */
	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		db.getCollection("users")
		.updateOne( new Document("user_id", userId), 
					new Document("$pullAll", 
						new Document("favorite", itemIds)));
	}

	
	//************* Get List of itemId in COLLECTION <History>Use (by userId) 
	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		Set<String> favoriteItems = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));
		
		if (iterable.first() != null && iterable.first().containsKey("favorite")) {
			@SuppressWarnings("unchecked")	// NO MORE warning here
											// Knowing return Object is List<String>
			List<String> list = (List<String>) iterable.first().get("favorite");
			favoriteItems.addAll(list);
		}

		return favoriteItems;
	}

	
	//************* Get every element in COLLECTION <Items> (by itemId) 	
	@Override
	public Set<Item> getFavoriteItems(String userId) {
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> itemIds = getFavoriteItemIds(userId);
		for (String itemId : itemIds) {
			// Check for Duplicates iteratively
			FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", itemId));
			if (iterable.first() != null) {
				Document doc = iterable.first();
				
				ItemBuilder builder = new ItemBuilder();
				builder.setItemId(doc.getString("item_id"));
				builder.setName(doc.getString("name"));
				builder.setAddress(doc.getString("address"));
				builder.setUrl(doc.getString("url"));
				builder.setImageUrl(doc.getString("image_url"));
				builder.setRating(doc.getDouble("rating"));
				builder.setDistance(doc.getDouble("distance"));
				builder.setCategories(getCategories(itemId));
				
				favoriteItems.add(builder.build());
			}
			
		}
		return favoriteItems;
	}

	//************* Get list of categories in TABLE <categories> (by itemId) 
	@Override
	public Set<String> getCategories(String itemId) {
		Set<String> categories = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", itemId));
		
		if (iterable.first() != null && iterable.first().containsKey("categories")) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get("categories");
			categories.addAll(list);
		}
		return categories;
	}
	
	
	//************* Get List<Items> and iteratively call "saveItem".
	// Exactly same as SQLConnection
	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		List<Item> items = tmAPI.search(lat, lon, term);
		for (Item item : items) {
			saveItem(item);
		}
		return items;
	}
	
	
	//************* Insert item's every element to Collection <Items>.
	@Override
	public void saveItem(Item item) {
		// Check for Duplicates (same as "INSERT IGNORE"):
		//		if exist --> save in iterable
		//		if not exist --> return null
		FindIterable<Document> iterable = db.getCollection("items")
											.find(eq("item_id", item.getItemId()));
		
		if (iterable.first() == null) { // NOT exist
			// every {} need new Document()
			db.getCollection("items").insertOne(new Document()
				.append("item_id", item.getItemId())
				.append("distance", item.getDistance())
				.append("name", item.getName())
				.append("address", item.getAddress())
				.append("url", item.getUrl())
				.append("image_url", item.getImageUrl())
				.append("rating", item.getRating())
				.append("categories", item.getCategories()));
		}
	}

	

	@Override
	public String getFullname(String userId) {
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));
		if (iterable.first() != null) {
			Document doc = iterable.first();
			return doc.getString("first_name") + " " + doc.getString("last_name");
		}
		return "";
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));
		if (iterable.first() != null) {
			Document doc = iterable.first();
			return doc.getString("password").equals(password);
		}
		return false;
	}
}
