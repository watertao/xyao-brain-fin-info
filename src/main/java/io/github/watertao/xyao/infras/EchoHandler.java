package io.github.watertao.xyao.infras;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("echo")
@Instruction(
  syntax = "echo <content>",
  description = "回音测试，用于检验 brain 模块是否正常工作",
  msgEnv = MessageEnvironmentEnum.BOTH,
  masterOnly = false
)
public class EchoHandler extends AbstractInstructionHandler {

  @Override
  protected Options defineOptions() {
    return null;
  }

  @Autowired
  private XyaoChannelProxy channelProxy;

  @Override
  protected void handle(XyaoInstruction instruction, CommandLine command) {
    if (instruction.getText().length() <= 5) {
      throw new IllegalArgumentException("缺少参数");
    }
    String echoContent = instruction.getText().substring(5);
    XyaoMessage xMessage = makeResponseMessage(instruction);
    xMessage.getEntities().add(new XyaoMessage.StringEntity(echoContent));
    channelProxy.publish(xMessage);
  }
}
