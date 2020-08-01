package io.github.watertao.xyao.infras;

import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractHandlerTester {

  private ThreadLocal<CommandLineParser> commandLineParserThd = new ThreadLocal<>();


  protected CommandLine parseCommand(Options options, String text) {
    CommandLine commandLine = null;
    String[] instructionPieces = splitCommand(text);
    if (instructionPieces.length <= 0) {
      throw new IllegalArgumentException("缺少指令");
    }
    String[] piecesWithoutBeanName = new String[instructionPieces.length - 1];
    System.arraycopy(instructionPieces, 1, piecesWithoutBeanName, 0, instructionPieces.length - 1);
    if (options!= null) {
      try {
        commandLine = retrieveCommandLineParser().parse(options, piecesWithoutBeanName);
      } catch (ParseException e) {
        throw new IllegalStateException(e.getMessage(), e);
      }
    }
    return commandLine;
  }

  private String[] splitCommand(String command) {
    List<String> list = new ArrayList<String>();
    Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
    while (m.find()) {
      list.add(m.group(1).replace("\"", ""));
    }

    return list.toArray(new String[0]);
  }

  private CommandLineParser retrieveCommandLineParser() {
    CommandLineParser parser = this.commandLineParserThd.get();
    if (parser == null) {
      this.commandLineParserThd.set(new DefaultParser());
    }
    return this.commandLineParserThd.get();
  }

}
