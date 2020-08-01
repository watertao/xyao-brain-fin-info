package io.github.watertao.xyao.instruction;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.watertao.xyao.infras.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


@Service("index")
@Instruction(
  syntax = "index",
  description = "返回上证及深成指数",
  masterOnly = false,
  msgEnv = MessageEnvironmentEnum.BOTH
)
public class IndexHandler extends AbstractInstructionHandler {

  private static final Logger logger = LoggerFactory.getLogger(IndexHandler.class);

  private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
          .setConnectTimeout(3000)
          //.setCookieSpec(CookieSpecs.IGNORE_COOKIES)
          .setConnectionRequestTimeout(3000)
          .setSocketTimeout(3000)
          .build();

  private CloseableHttpClient httpClient;

  @Autowired
  private XyaoChannelProxy channelProxy;

  public IndexHandler() {
    this.httpClient = HttpClients.custom().setDefaultRequestConfig(REQUEST_CONFIG)
            .setMaxConnTotal(50)
            .setMaxConnPerRoute(20)
            .build();
  }


  @Override
  protected Options defineOptions() {

    Options options = new Options();
    // options.addOption("m", "market", true, "市场，默认沪市。(sh:沪市；sz:深市)");

    return options;

  }

  @Override
  protected void handle(XyaoInstruction instruction, CommandLine command) {

    HttpGet httpGet = new HttpGet("http://hq.sinajs.cn/rn=1596204462815&list=s_sh000001,s_sz399001");
    try {
      CloseableHttpResponse response = httpClient.execute(httpGet);
      String responseStr = EntityUtils.toString(response.getEntity());
      String[] lines = responseStr.split("\n");
      StringBuilder sb = new StringBuilder();
      for (String line : lines) {
        line = line.substring(23, line.length() - 2);
        String[] arry = line.split(",");
        sb.append(arry[0]).append(": ").append(arry[1]).append(" ( ")
                .append(Double.valueOf(arry[3]) > 0 ? "🔺" : "🔻")
                .append(arry[3]).append("%").append(" )\n");
      }

      XyaoMessage xyaoMessage = makeResponseMessage(instruction);
      xyaoMessage.getEntities().add(new XyaoMessage.StringEntity(sb.toString()));
      channelProxy.publish(xyaoMessage);
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }



  }




}
