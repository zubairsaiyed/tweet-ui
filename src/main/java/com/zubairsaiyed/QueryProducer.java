package com.zubairsaiyed;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class QueryProducer {

	private static QueryProducer instance = new QueryProducer();
	private static kafka.javaapi.producer.Producer<String, String> producer;
	private static final String topic = "query-topic";
	final static Logger logger = Logger.getLogger(QueryProducer.class);

	private QueryProducer() {
		Properties prop = new Properties();
		try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
    		prop.load(input);
    	} catch (IOException ex) {
			logger.error("Unable to read configuration file! Make sure configuration parameters are defined.");
    		ex.printStackTrace();
    	}

		// initialize Kafka connection
	    Properties properties = new Properties();
	    properties.put("metadata.broker.list", prop.getProperty("kafka_server") + ":" + prop.getProperty("kafka_port"));
	    properties.put("serializer.class", "kafka.serializer.StringEncoder");
	    ProducerConfig producerConfig = new ProducerConfig(properties);
	    producer = new kafka.javaapi.producer.Producer<String, String>(producerConfig);
	}

	public static QueryProducer getInstance() {
		return instance;
	}

	public static void submitQuery(String query) {
	    KeyedMessage<String, String> message = null;
	    message = new KeyedMessage<String, String>(topic, query);
	    producer.send(message);
	}

	public static void shutdown() {
		producer.close();
	}

}
