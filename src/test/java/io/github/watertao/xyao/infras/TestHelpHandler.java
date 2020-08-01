package io.github.watertao.xyao.infras;

import io.github.watertao.xyao.infras.HelpHandler;
import org.apache.commons.cli.CommandLine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class TestHelpHandler extends AbstractHandlerTester {

  @Autowired
  private HelpHandler handler;


  @Test
  public void testHelpSpecifiedInstruction() {

    String text = "help helpss";


    XyaoInstruction instruction = new XyaoInstruction();
    instruction.setFrom(new XyaoInstruction.Contact(){{
      setId("testid");
      setName("watertao");
    }});
    instruction.setText(text);
    CommandLine commandLine = parseCommand(handler.options(), text);


    handler.handle(instruction, commandLine);

  }



}
