package com.zubairsaiyed;

import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import org.apache.log4j.Logger;
import com.lambdaworks.redis.pubsub.api.rx.RedisPubSubReactiveCommands;

public class QueryMonitor {

	private static QueryMonitor instance = new QueryMonitor();
    private static RedisPubSubReactiveCommands<String, String> reactive;
    private static final ConcurrentHashMap<String, QueryData> queries = new ConcurrentHashMap<String, QueryData>();
	private final RedisCallback cb = new RedisCallback();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	final static Logger logger = Logger.getLogger(QueryMonitor.class);
	private Date threshold;

    private QueryMonitor() {
		// initiate reactive (asynch) connection
    	reactive = Main.connection.reactive();
		// define message callback
        reactive.observeChannels().subscribe(cb);
		// begin regular flushing of idle queryListeners
        flushQueryListeners();
        threshold = new Date();
    }

    public static QueryMonitor getInstance() {
    	return instance;
    }

	// add Redis channel subscription to query (hash)
    public static void addQueryListener(String query) {
    	if (!contains(query)) {
    		reactive.subscribe(queryHash(query)).subscribe();
			// initialize query to invalid value >1 (to avoid displaying in UI)
    		put(query, 3.14);
			logger.debug("Added query listener for: " + query);
	    	logger.debug("Now tracking " + QueryMonitor.size() + " queries");
    	} else {
    		logger.debug("Already tracking query " + query);
    	}
    }

    public static boolean contains(String query) {
    	return queries.containsKey(queryHash(query));
    }

    public static boolean containsHash(String queryHash) {
    	return queries.containsKey(queryHash);
    }

    public static Double getValue(String query) {
    	return queries.get(queryHash(query)).getVal();
    }

    private static void put(String query, Double value) {
		queries.put(queryHash(query), new QueryData(value));
    }

    public static void update(String queryHash, Double value) {
    	if(containsHash(queryHash)) {
    		queries.get(queryHash).setVal(value);
    	}
    }

    public static int size() {
    	return queries.size();
    }

    public static String queryHash(String query) {
    	return Integer.toString(query.toLowerCase().hashCode());
    }

	// frequenty clears QueryMonitor of idle queryListeners to avoid overflow
    private void flushQueryListeners() {
        final Runnable flush = new Runnable() {
                public void run() {
                	Iterator<Entry<String,QueryData>> iter = queries.entrySet().iterator();
                	while (iter.hasNext()) {
                	    Entry<String,QueryData> entry = iter.next();
                	    logger.debug(threshold.toString() + "   " + entry.getValue().getLastRead().toString());
                		if (entry.getValue().getLastRead().before(threshold)) {
                			logger.debug("Removing listener: " + entry.getKey());
                			reactive.unsubscribe(entry.getKey()).subscribe();
                			iter.remove();
                		}
                	}
                	threshold = new Date();
                }
            };
        final ScheduledFuture<?> flushHandle = scheduler.scheduleAtFixedRate(flush, 5, 5, SECONDS);
    }

}
