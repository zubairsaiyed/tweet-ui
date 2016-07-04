package com.zubairsaiyed;

import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;

public class PostRoute implements Route {

	final static Logger logger = Logger.getLogger(PostRoute.class);

	public PostRoute() {}

	// submit new query to Kafka and initiate queryListener
	public Object handle(Request request, Response response) {
		String query = request.body().trim();

		// submit query to Kafka query-topic
		QueryProducer.submitQuery(query);
		logger.debug("submitted query to track : " + query);

		// add queryListener to QueryMonitor if doesn't already exist
		if (QueryMonitor.contains(query)) {
			logger.debug("Query '" + query + "' already being tracked");
		} else {
			QueryMonitor.addQueryListener(query);
		}

		response.status(201);
		response.body("Tracking query " + query);
		return response;
	}
}
