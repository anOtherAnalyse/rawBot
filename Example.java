import rawbot.bot.examples.ChatDumpBot;

import java.lang.NumberFormatException;

public class Example {

  public static void main(String[] args) {
    if(args.length < 1) System.err.println("Usage: java Example <host> [port]");
    else {
      int port = 25565;
      if(args.length >= 2) {
        try {
          port = Integer.parseInt(args[1]);
        } catch(NumberFormatException e) {
          System.err.println(String.format("Ignoring argument \"%s\"", args[1]));
        }
      }

      ChatDumpBot bot = new ChatDumpBot();
      bot.run(args[0], port, true, true);
    }
  }

}
