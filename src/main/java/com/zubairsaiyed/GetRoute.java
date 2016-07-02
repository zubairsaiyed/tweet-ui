package com.zubairsaiyed;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import spark.Request;
import spark.Response;
import spark.Route;

public class GetRoute implements Route {
	
	final static Logger logger = Logger.getLogger(GetRoute.class);
	
	public GetRoute() {}
	
	public Object handle(Request request, Response response) {
		String[] queries = request.queryParamsValues("q");

		JSONArray arr = new JSONArray();
		for (String query : queries) {
			arr.add(getValue(query));
		}
		
		response.raw().setContentType("application/json");
		return arr;
	}
	
	private JSONObject getValue(String query) {

		if (!QueryMonitor.contains(query)) {
			logger.debug("Invalid query '"+query+"'. Not currently being tracked.");
			return null;
		}
		
		JSONArray arr = new JSONArray();
        JSONObject obj = new JSONObject();
		obj.put("data", QueryMonitor.getValue(query));
		obj.put("name", query);
		
		return obj;
	}
}
