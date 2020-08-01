package io.github.watertao.xyao.infras;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@Service("help")
@Instruction(
  syntax = "help [options...] [<instruction>]",
  description = "查看其它指令的使用方式，比如 help echo",
  masterOnly = false,
  msgEnv = MessageEnvironmentEnum.BOTH
)
public class HelpHandler extends AbstractInstructionHandler implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  private HelpFormatter helpFormatter = new HelpFormatter();

  @Autowired
  private XyaoChannelProxy channelProxy;

  @Autowired
  private Environment env;

  @Override
  protected Options defineOptions() {
    Options options = new Options();
    options.addOption("w", "web", false, "返回网页版帮助手册");
    return options;
  }

  @Override
  protected void handle(XyaoInstruction instruction, CommandLine command) {

    if (command.hasOption("w")) {
      webManual(instruction);
      return;
    }

    if (command.getArgList().size() > 0) {
      printHelpForSpecificInstruction(instruction, command);
    } else {
      printInstructionList(instruction);
    }

  }

  private void printHelpForSpecificInstruction(XyaoInstruction instruction, CommandLine command) {
    String beanName = command.getArgList().get(0);
    Object bean = null;
    try {
      bean = applicationContext.getBean(beanName);
    } catch (NoSuchBeanDefinitionException e) {
      throw new IllegalArgumentException("指令不存在 [ " + beanName + " ]");
    }

    if (!(bean instanceof AbstractInstructionHandler)) {
      throw new IllegalArgumentException("指令不存在 [ " + beanName + " ]");
    }
    Instruction instructionAnno = bean.getClass().getDeclaredAnnotation(Instruction.class);
    if (instructionAnno != null) {
      StringWriter sw = new StringWriter();
      helpFormatter.printHelp(
        new PrintWriter(sw), 74,
        instructionAnno.syntax(),
        instructionAnno.description(),
        ((AbstractInstructionHandler) bean).options(),
        3,5,null);
      XyaoMessage message = makeResponseMessage(instruction);
      message.getEntities().add(new XyaoMessage.StringEntity(sw.toString()));
      channelProxy.publish(message);
    } else {
      throw new IllegalStateException("指令帮助未定义 [ " + beanName + " ]");
    }
  }

  private void printInstructionList(XyaoInstruction instruction) {

    Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Instruction.class);
    StringBuilder helpLiteral = new StringBuilder();
    for (String beanName : beans.keySet()) {
      Instruction instructionAnno = beans.get(beanName).getClass().getAnnotation(Instruction.class);
      helpLiteral.append("[ " + beanName + " ]   " + instructionAnno.description())
        .append("\n");
    }

    XyaoMessage message = makeResponseMessage(instruction);
    message.getEntities().add(new XyaoMessage.StringEntity(helpLiteral.toString()));
    channelProxy.publish(message);

  }

  private void webManual(XyaoInstruction instruction) {
    XyaoMessage.URLLinkEntity urlLinkEntity = new XyaoMessage.URLLinkEntity();
    urlLinkEntity.setTitle(env.getProperty("xyao.help.title"));
    urlLinkEntity.setDescription(env.getProperty("xyao.help.description"));
    urlLinkEntity.setUrl(env.getProperty("xyao.help.url"));
    urlLinkEntity.setThumbnailUrl(env.getProperty("xyao.help.thumbnail"));
    XyaoMessage xMessage = makeResponseMessage(instruction);
    xMessage.getEntities().add(urlLinkEntity);
    channelProxy.publish(xMessage);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
