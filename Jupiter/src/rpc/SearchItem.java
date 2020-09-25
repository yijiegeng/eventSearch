package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// #1
		/************** Send a "Hello!" *******************/
//		PrintWriter out = response.getWriter();
//		// get username
//		if(request.getParameter("username") != null) {
//			String username = request.getParameter("username");
//			out.println("Hello " + username);
//		}
//		// get password
//		if(request.getParameter("password") != null) {
//			String password = request.getParameter("password");
//			out.println("password is " + password);
//		}
//		out.close();
		
		
		// #2
		/************** Send a HTML file *******************/
//		response.setContentType("text/html");
//		PrintWriter out = response.getWriter();
//		out.println("<html><body>");
//		out.println("<h1> This is a HTML page </h1>");
//		out.println("</body></html>");
		
		
		// #3
		/************** Send a jsonObject *******************/
//		response.setContentType("application/json");
//		PrintWriter out = response.getWriter();
//		String username = "";
//		if(request.getParameter("username") != null) {
//			username = request.getParameter("username");
//		}
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("username", username);
//		}catch(JSONException e){
//			e.printStackTrace();
//		}
//		out.print(obj);
//		out.close();
		
		// #4
		/************** Send a jsonArray (Multiple input) *******************/
		/************** no need to setContentType (in helper method)**************/
//		JSONArray array = new JSONArray();
//		try {
//			array.put(new JSONObject().put("username", "qqq"));
//			array.put(new JSONObject().put("username", "www"));
//		}catch(JSONException e){
//			e.printStackTrace();
//		}
//		RpcHelper.writeJsonArray(response, array);

		// #5
		/************** Send real data *******************/
		JSONArray array = new JSONArray();
		try {
			double lat = Double.parseDouble(request.getParameter("lat"));
			double lon = Double.parseDouble(request.getParameter("lon"));
			String keyword = request.getParameter("term");
			
			//TicketMasterAPI tmAPI = new TicketMasterAPI();
			//List<Item> items = tmAPI.search(lat, lon, keyword);
			
			DBConnection connection = DBConnectionFactory.getConnection();
			List<Item> items = connection.searchItems(lat, lon, keyword);
			connection.close();
			
			for (Item item: items) {
				JSONObject obj = item.toJSONObject();
				array.put(obj);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
