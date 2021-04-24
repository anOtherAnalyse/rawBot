package rawbot.bot.examples;

import java.io.IOException;

import rawbot.bot.Bot;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.packets.in.play.ChatReceivePacket;
import rawbot.connection.packets.out.play.ClientStatusPacket;

public class ChatDumpBot extends Bot {

  private boolean isWindows;

  public ChatDumpBot() {
    super();

    this.isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
  }

  protected void onPacketReceived(PacketIn packet) {
    if(packet instanceof ChatReceivePacket) {
      if(this.isWindows) System.out.println(((ChatReceivePacket) packet).getMessage().getUnformattedText());
      else System.out.println(((ChatReceivePacket) packet).getMessage().getFormattedText());
    }
  }

  protected void onDeath() {
    System.out.println("Ho no we died");
    this.sendPacket(new ClientStatusPacket(ClientStatusPacket.State.PERFORM_RESPAWN)); // Respawn
  }

  protected void onDisconnected(IOException exception) {}

  protected void onTick() {}
}
