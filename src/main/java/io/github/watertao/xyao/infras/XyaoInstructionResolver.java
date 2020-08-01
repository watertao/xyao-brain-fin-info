package io.github.watertao.xyao.infras;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class XyaoInstructionResolver implements MessageListener, ApplicationContextAware {

  private static final Logger logger = LoggerFactory.getLogger(XyaoInstructionResolver.class);

  private ApplicationContext applicationContext;

  private ObjectMapper objectMapper = new ObjectMapper();

  private ThreadLocal<CommandLineParser> commandLineParserThd = new ThreadLocal<>();

  private Pattern pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

  @Autowired
  private Environment env;

  @Autowired
  private XyaoChannelProxy channelProxy;

  public void onMessage(Message message, byte[] pattern) {

    String messageStr = message.toString();

    logger.info("[ >>> ] " + messageStr);

    XyaoInstruction instruction = null;
    try {
      instruction = objectMapper.readValue(message.toString(), XyaoInstruction.class);
    } catch (JsonProcessingException e) {
      logger.error(e.getMessage(), e);
      return;
    }

    try {
      handleInstruction(instruction);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      makeErrResponse(instruction, e);
    }

  }

  private void handleInstruction(XyaoInstruction instruction) {

    String[] instructionPieces = splitCommand(instruction.getText());
    if (instructionPieces.length <= 0) {
      throw new IllegalArgumentException("缺少指令");
    }
    String beanName = instructionPieces[0];
    String[] piecesWithoutBeanName = new String[instructionPieces.length - 1];
    System.arraycopy(instructionPieces, 1, piecesWithoutBeanName, 0, instructionPieces.length - 1);
    AbstractInstructionHandler handler = null;
    try {
       handler = (AbstractInstructionHandler) applicationContext.getBean(beanName);
    } catch (NoSuchBeanDefinitionException e) {
      throw new IllegalArgumentException("无法识别指令 [ " + beanName + " ]");
    }

    Instruction instructionAnno = handler.getClass().getDeclaredAnnotation(Instruction.class);
    if (instructionAnno == null) {
      throw new IllegalArgumentException("无法识别指令 [ " + beanName + " ]");
    }

    if (instructionAnno.masterOnly() && !instruction.getFrom().getIsMaster()) {
      throw new IllegalArgumentException("没有执行此指令的权限 [ " + beanName + " ]");
    }

    if (MessageEnvironmentEnum.ROOM.equals(instructionAnno.msgEnv()) && instruction.getRoom() == null) {
      throw new IllegalArgumentException("这条指令只能在群组内执行");
    }

    if (MessageEnvironmentEnum.WHISPER.equals(instructionAnno.msgEnv()) && instruction.getRoom() != null) {
      throw new IllegalArgumentException("这条指令只能在私聊语境中执行");
    }

    CommandLine commandLine = null;
    try {
      if (handler.options() != null) {
        commandLine = retrieveCommandLineParser().parse(handler.options(), piecesWithoutBeanName);
      }

    } catch (ParseException e) {
      makeErrResponse(instruction, e);
      return;
    }


    handler.handle(instruction, commandLine);

  }

  private void makeErrResponse(XyaoInstruction instruction, Exception exception) {
    XyaoMessage message = new XyaoMessage();
    message.setRoom(instruction.getRoom());
    message.getTo().add(new XyaoMessage.Contact(){{
      setId(instruction.getFrom().getId());
      setName(instruction.getFrom().getName());
    }});
    message.getEntities().add(new XyaoMessage.StringEntity());

    if (exception instanceof MissingArgumentException) {
      MissingArgumentException e = (MissingArgumentException) exception;
      ((XyaoMessage.StringEntity) message.getEntities().get(0)).setPayload(
        "选项 [ " + e.getOption().getArgName() + " ] 缺少值"
      );
    } else if (exception instanceof MissingOptionException) {
      MissingOptionException e = (MissingOptionException) exception;
      ((XyaoMessage.StringEntity) message.getEntities().get(0)).setPayload(
        "缺少选项 [ " + e.getMissingOptions().get(0) + " ]"
      );
    } else if (exception instanceof UnrecognizedOptionException) {
      UnrecognizedOptionException e = (UnrecognizedOptionException) exception;
      ((XyaoMessage.StringEntity) message.getEntities().get(0)).setPayload(
        "无法识别选项 [ " + e.getOption() + " ]"
      );
    } else {
      ((XyaoMessage.StringEntity) message.getEntities().get(0)).setPayload(
        exception.getMessage()
      );
    }

    ((XyaoMessage.StringEntity) message.getEntities().get(0)).setPayload(
      ((XyaoMessage.StringEntity) message.getEntities().get(0)).getPayload() +
        "\n( 使用 " + env.getProperty("xyao.brain") + ":help 或 " + env.getProperty("xyao.brain")  + ":help -w 了解更多 )"
    );

    channelProxy.publish(message);

  }

  private CommandLineParser retrieveCommandLineParser() {
    CommandLineParser parser = this.commandLineParserThd.get();
    if (parser == null) {
      this.commandLineParserThd.set(new DefaultParser());
    }
    return this.commandLineParserThd.get();
  }

  private String[] splitCommand(String command) {
    List<String> list = new ArrayList<String>();
    Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
    while (m.find()) {
      list.add(m.group(1).replace("\"", ""));
    }

    return list.toArray(new String[0]);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
