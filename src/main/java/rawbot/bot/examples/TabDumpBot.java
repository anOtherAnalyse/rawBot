package rawbot.bot.examples;

import java.io.IOException;

import rawbot.bot.Bot;
import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.packets.in.play.PlayerListItemPacket;

public class TabDumpBot extends Bot {

  private static final long TTL = 5000;

  private boolean queue_passed;
  private long connected;

  public TabDumpBot() {
    super();

    this.queue_passed = false;
    this.connected = -1;
  }

  protected void onPacketReceived(PacketIn packet) {
    switch(packet.getId(EnumConnectionState.PLAY)) {
      case 46:
        {
          if(this.queue_passed) {
            PlayerListItemPacket list = (PlayerListItemPacket) packet;
            if(list.getAction() == PlayerListItemPacket.Action.ADD_PLAYER) {
              for(PlayerListItemPacket.AddPlayerData player : list.getPlayers()) {
                System.out.println(String.format("%s%s - %d (gamemode: %d)", player.profile.getName(), player.displayName == null ? "" : player.displayName.getUnformattedText(), player.ping, player.gamemode));
              }
            }
          }
        }
        break;
      case 53:
        {
          if(! this.queue_passed) {
            this.dateAndPrint("Queue passed..");
            this.queue_passed = true;
            this.connected = System.currentTimeMillis();
          }
        }
        break;
    }
  }

  protected void onDeath() {}

  protected void onDisconnected(IOException exception) {}

  protected void onTick() {
    if(this.connected > 0 && System.currentTimeMillis() - this.connected > TTL) {
      this.disconnect();
    }
  }
}
