package com.zubairsaiyed;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.cluster.RedisClusterClient;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;
import spark.Spark;
import static spark.Spark.get;
import static spark.Spark.init;
import static spark.Spark.post;

public class Main {
	private static RedisURI redisClient;
	private static RedisClusterClient redisClusterClient;
	public static StatefulRedisPubSubConnection<String, String> connection;
	private final static GetRoute getRoute = new GetRoute();
	private final static PostRoute postRoute = new PostRoute();
	final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
    	logger.debug("Initializing server");

    	Properties prop = new Properties();
    	try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
    		prop.load(input);
    	} catch (IOException ex) {
			logger.error("Unable to read configuration file! Make sure configuration parameters are defined.")
    		ex.printStackTrace();
    	}

		// establishing Redis connection
		redisClient = RedisURI.Builder.redis(prop.getProperty("redis_host"), Integer.parseInt(prop.getProperty("redis_port"))).withPassword(prop.getProperty("redis_password")).build();
    	redisClusterClient = RedisClusterClient.create(redisClient);
    	connection = redisClusterClient.connectPubSub();

		// initializing QueryProducer and QueryMonitor
    	QueryProducer.getInstance();
    	QueryMonitor.getInstance();

		// Configuring server port and static file location
    	Spark.port(Integer.parseInt(prop.getProperty("server_port")));
    	Spark.staticFileLocation("/public");

		// Defining URI routing
        get("/data/", getRoute);
        post("/data/", postRoute);

		// Finalizing initialization
        init();
    }
}
