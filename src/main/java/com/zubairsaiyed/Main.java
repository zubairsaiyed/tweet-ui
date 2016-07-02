package com.zubairsaiyed;

import static spark.Spark.get;
import static spark.Spark.init;
import static spark.Spark.post;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.cluster.RedisClusterClient;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;

import spark.Spark;

public class Main {
	private static RedisURI redisClient;
	private static RedisClusterClient redisClusterClient;
	public static StatefulRedisPubSubConnection<String, String> connection;
	private final static GetRoute getRoute = new GetRoute();
	private final static PostRoute postRoute = new PostRoute();
	final static Logger logger = Logger.getLogger(Main.class);
	
    public static void main(String[] args) {
    	logger.debug("Initializing");
    	
    	Properties prop = new Properties();
    	try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
    		prop.load(input);
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	}

		redisClient = RedisURI.Builder.redis(prop.getProperty("redis_host"), Integer.parseInt(prop.getProperty("redis_port"))).withPassword(prop.getProperty("redis_password")).build();
    	redisClusterClient = RedisClusterClient.create(redisClient);
    	connection = redisClusterClient.connectPubSub();
    	
    	QueryProducer.getInstance();
    	QueryMonitor.getInstance();
    	Spark.port(Integer.parseInt(prop.getProperty("server_port")));
    	Spark.staticFileLocation("/public");
        get("/data/", getRoute);
        post("/data/", postRoute);
        init();
    }
}