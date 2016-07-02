package com.zubairsaiyed;

import org.apache.log4j.Logger;

import com.lambdaworks.redis.pubsub.api.rx.ChannelMessage;

import rx.functions.Action1;

public class RedisCallback implements Action1<ChannelMessage<String, String>> {
	
	final static Logger logger = Logger.getLogger(RedisCallback.class);

	public void call(ChannelMessage<String, String> message) {
    	logger.debug("RECEIVED: " + message.getChannel() + " " + message.getMessage());
    	QueryMonitor.update(message.getChannel(), Double.parseDouble(message.getMessage().substring(0, message.getMessage().indexOf(' '))));
	}

}
