package io.github.watertao.xyao.infras;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class XyaoChannelProxy {

  private static final Logger logger = LoggerFactory.getLogger(XyaoChannelProxy.class);

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Autowired
  private Environment env;

  public void publish(XyaoMessage message) {
    try {
      String sMessage = objectMapper.writeValueAsString(message);
      logger.info("[ ^o^ ] " + sMessage);
      redisTemplate.convertAndSend(env.getProperty("xyao.channel"), sMessage);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

}
