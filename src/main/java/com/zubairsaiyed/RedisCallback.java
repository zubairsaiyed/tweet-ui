package com.zubairsaiyed;

import rx.functions.Action1;
import org.apache.log4j.Logger;
import com.lambdaworks.redis.pubsub.api.rx.ChannelMessage;

public class RedisCallback implements Action1<ChannelMessage<String, String>> {

	final static Logger logger = Logger.getLogger(RedisCallback.class);

	// logs message received and updates QueryMonitor cache with new value
	public void call(ChannelMessage<String, String> message) {
		logger.debug("RECEIVED: " + message.getChannel() + " " + message.getMessage());
		QueryMonitor.update(message.getChannel(), Double.parseDouble(message.getMessage().substring(0, message.getMessage().indexOf(' '))));
	}

}
