package io.github.watertao.xyao.infras;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public abstract class AbstractInstructionHandler {

  private Options options;

  Options options() {
    if (options == null) {
      options = defineOptions();
      options = options == null ? new Options() : options;
    }
    return options;
  }

  /**
   * Options options = new Options();
   * options.addOption("a", "aaa", false, "布尔型的参数，无参数值, 比如 -a 代表 true，如果不存在  -a 则 false");
   * options.addOption("b", "ccc", true, "有值得参数,如. --bbb bb 的值就是 bb");
   * options.addRequiredOption("c", "ccc", true, "强制需要的参数，没有就报错");
   */
  protected abstract Options defineOptions();

  protected abstract void handle(XyaoInstruction instruction, CommandLine command);

  protected XyaoMessage makeResponseMessage(XyaoInstruction instruction) {
    XyaoMessage message = new XyaoMessage();
    message.getTo().add(new XyaoMessage.Contact(){{
      setId(instruction.getFrom().getId());
      setName(instruction.getFrom().getName());
    }});
    message.setRoom(instruction.getRoom());
    return message;
  }

}
