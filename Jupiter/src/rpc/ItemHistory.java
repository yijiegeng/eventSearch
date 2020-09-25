package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistory() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// request is form of String
		String userId = request.getParameter("user_id");
		JSONArray array = new JSONArray();

		DBConnection conn = DBConnectionFactory.getConnection();
		Set<Item> items = conn.getFavoriteItems(userId);
		
		// change Item type to JSONArray type
		for (Item item : items) {
			JSONObject obj = item.toJSONObject();
			try {
				// add a field in JSON, for front-end
				obj.append("favorite", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(obj);
		}
		
		// send <userId> & <ItemIds> to Website 
		RpcHelper.writeJsonArray(response, array);
	}

	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	/**
	 *	the input HTTP request looks like:
	 *	    user_id = “1111”,
	 *	    favorite = [“abcd”,“efgh”, ...]
	 */
	// set favorite
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// request is form of JsonObject
			JSONObject input = RpcHelper.readJsonObject(request);	// take request --> input
			String userId = input.getString("user_id");				// take user_id --> userId
			JSONArray array = input.getJSONArray("favorite");		// take favorite --> array
			
			List<String> itemIds = new ArrayList<>();
			
			// change JSONArray type to Item type
			for (int i = 0; i < array.length(); ++i) {
				itemIds.add(array.get(i).toString());
			}
			
			// send <userId> & <ItemIds> to "MySQLConnection.setFavoriteItems" 
			DBConnection conn = DBConnectionFactory.getConnection();
			conn.setFavoriteItems(userId, itemIds);
			conn.close();
			
			// send state information to requester
			RpcHelper.writeJsonObject(response,
					new JSONObject().put("result", "SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");
			JSONArray array = input.getJSONArray("favorite");
			
			List<String> itemIds = new ArrayList<>();
			for (int i = 0; i < array.length(); ++i) {
				itemIds.add(array.get(i).toString());
			}
			
			// call MySQLConnection.unsetFavoriteItems 
			DBConnection conn = DBConnectionFactory.getConnection();
			conn.unsetFavoriteItems(userId, itemIds);
			conn.close();
			
			RpcHelper.writeJsonObject(response,
					new JSONObject().put("result", "SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
