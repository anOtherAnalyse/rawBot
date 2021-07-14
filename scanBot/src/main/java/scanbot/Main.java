package scanbot;

import java.lang.NumberFormatException;

public class Main {

  public static void printUsage() {
    System.err.println("Usage: java -jar scanbot.jar <center_x> <center_z> <radius> <host> [port]");
  }

  public static void main(String[] args) {

    if(args.length >= 1) {
      String host = null;
      int port = 25565;

      ScanBot bot = null;
      try {
        if(args.length >= 4) { // Scan new areas
          host = args[3];
          if(args.length >= 5) port = Integer.parseInt(args[4]);

          bot = new ScanBot(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        } else { // Check targets activity
          host = args[0];
          if(args.length >= 2) port = Integer.parseInt(args[1]);

          bot = new ScanBot();
        }
      } catch(NumberFormatException e) {
        Main.printUsage();
        return;
      }

      bot.run(host, port, true, true);
    } else Main.printUsage();
  }
}
