package scanbot;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import rawbot.bot.Bot;
import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.packets.in.play.BlockChangePacket;
import rawbot.connection.packets.in.play.PlayerPosLookPacket;
import rawbot.connection.packets.in.play.RespawnPacket;
import rawbot.connection.packets.out.play.ChatSendPacket;
import rawbot.connection.packets.out.play.ClientStatusPacket;
import rawbot.connection.packets.out.play.PlayerDiggingPacket;
import rawbot.game.position.BlockPos;
import rawbot.game.position.ChunkPos;
import rawbot.game.position.Vec3d;
import rawbot.game.utils.EnumFacing;

public class ScanBot extends Bot {

  private static final int TIMEOUT = 5000; // Real timeouts seems rare
  private static final int TICK_RATE = 4; // How many packets sent by tick
  private static final int BASE_WINDOW_SIZE = 12;
  private static final int WAVE_SIZE = 8;

  public static enum Mode {AREA, DEFINED};
  private Mode mode;

  // Scaned area details
  private ScanTarget parameters;

  // Send window
  private LinkedList<RequestsWave> window;
  private int window_size;

  // Different ACK number for each wave
  private int current_ack_number;

  // Timed out waves to send again
  private LinkedList<RequestsWave> retry;

  // Current wave
  private RequestsWave current;

  // Player position
  private Vec3d player;

  // Bot isn't in queue server anymore
  private boolean queue_passed;
  private long server_join;

  // Last reported scan advancement
  private int advancement;

  // For defined area scan mode
  private long last_run;

  // Scan a rectangle area
  public ScanBot(int center_x, int center_z, int radius) {
    this.parameters = new ScanTargetArea(center_x, center_z, radius);
    this.mode = Mode.AREA;

    this.init();
  }

  // Scan targeted positions frequently
  public ScanBot() {
    this.parameters = new ScanTargetDefined();
    this.mode = Mode.DEFINED;

    this.init();
  }

  private void init() {
    this.window = new LinkedList<RequestsWave>();
    this.window_size = ScanBot.BASE_WINDOW_SIZE;

    this.retry = new LinkedList<RequestsWave>();
    this.current_ack_number = -2047;
    this.current = this.nextWave();

    this.player = new Vec3d(0d, 0d, 0d);

    this.queue_passed = false;
    this.advancement = 0;
    this.last_run = 0;
  }

  protected void onPacketReceived(PacketIn packet) {
    switch(packet.getId(EnumConnectionState.PLAY)) {
      case 47:
        {
          PlayerPosLookPacket pos = (PlayerPosLookPacket) packet;
          this.player.x = pos.getPlayerX(this.player.x);
          this.player.z = pos.getPlayerZ(this.player.z);
        }
        break;
      case 53:
        {
          if(! this.queue_passed) {
            this.dateAndPrint("Queue passed. Starting the scan..");
            if(this.mode == Mode.AREA) this.printRemainingTime();
            this.queue_passed = true;
            this.server_join = System.currentTimeMillis();
          }
        }
        break;
      case 11:
        {
          BlockPos position = ((BlockChangePacket) packet).getPosition();

          if(position.y == -16) { // Player presence
            ChunkPos chunk = new ChunkPos(position.x >> 4, position.z >> 4);
            ChunkPos bot = new ChunkPos((int)this.player.x >> 4, (int)this.player.z >> 4);
            if(Math.abs(chunk.x - bot.x) > (ScanTargetArea.PLAYER_RENDER_DISTANCE / 2) || Math.abs(chunk.z - bot.z) > (ScanTargetArea.PLAYER_RENDER_DISTANCE / 2)) { // Not us
              this.dateAndPrint(String.format("Player presence around (%d, %d)", (chunk.x << 4) + 8, (chunk.z << 4) + 8));
            }
          } else if(position.y < -16) { // ACK

            // Remove corresponding wave from window
            boolean found = false;
            Iterator iterator = this.window.iterator();
            while(iterator.hasNext()) {
              RequestsWave current = (RequestsWave) iterator.next();
              if(current.getACK() == position.y) {
                iterator.remove();
                found = true;
                break;
              }
            }

            if(! found) {
              this.dateAndPrint(String.format("Received ACK for wave[%d] not present in current window, consider increasing timeout delay", position.y), System.err);
            }
          }
        }
        break;
    }
  }

  protected void onDeath() {
    if(this.queue_passed) { // avoid queue
      this.dateAndPrint("Bot was killed, respawning..");
      this.sendPacket(new ClientStatusPacket(ClientStatusPacket.State.PERFORM_RESPAWN));
    }
  }

  protected void onDisconnected(IOException exception) {
    // Timeout all requests from current window
    while(! this.window.isEmpty()) {
      this.retry.add(this.window.remove());
    }

    // Reset current wave
    if(this.current != null) this.current.reset(this.nextACKNumber());

    this.player = new Vec3d(0d, 0d, 0d);
    this.queue_passed = false;

    this.last_run = 0;
  }

  protected void onTick() {
    if(! this.queue_passed) return;

    // Manage timeouts
    long time = System.currentTimeMillis();

    // Don't send right after joining + DEFINED scan redo delay
    if((time - this.server_join <= 1000) || (time - this.last_run <= 120000)) return;

    Iterator iterator = this.window.iterator();
    while(iterator.hasNext()) {
      RequestsWave current = (RequestsWave) iterator.next();
      if(time - current.getTime() >= ScanBot.TIMEOUT) {
        this.retry.add(current);
        iterator.remove();
        this.dateAndPrint(String.format("Requests Wave[%d] timed out", current.getACK()), System.err);
      } else break;
    }

    // Send requests
    if(this.window.size() < this.window_size) {
      for(int burst = 0; burst < ScanBot.TICK_RATE; burst ++) {

        // Change current wave
        if(this.current == null) {
          this.current = this.nextWave();

          // Update progress
          if(this.mode == Mode.AREA) {
            int new_advancement = this.parameters.getAdvancement();
            if(new_advancement != this.advancement) {
              this.advancement = new_advancement;
              this.dateAndPrint(String.format("Scan progress: %d%%", this.advancement));
            }
          }

          if(this.current == null) { // Scan completed, start over
            this.parameters.reset();
            this.advancement = 0;
            this.current = this.nextWave();
            if(this.mode == Mode.DEFINED) {
              this.last_run = System.currentTimeMillis(); // Will left some leftovers in current window, hope they don't time out
              break;
            } else this.dateAndPrint("Whole area scanned, repeating..");
          }
        }

        int index = this.current.nextIndex();
        if(index == -1) {
          // Send ACK request
          this.current.setTime(System.currentTimeMillis());
          this.sendPacket(new PlayerDiggingPacket(new BlockPos((int) this.player.x, this.current.getACK(), (int) this.player.z), EnumFacing.UP, PlayerDiggingPacket.Action.STOP_DESTROY_BLOCK));

          // Add current wave to send window
          this.window.add(this.current);
          this.current = null;

          // Don't continue if window is full
          if(this.window.size() >= this.window_size) {
            this.dateAndPrint(String.format("Send window is full (size: %d)", this.window_size), System.err);
            break;
          }

        } else { // Send normal request
          this.sendPacket(new PlayerDiggingPacket(this.parameters.getPositionForIndex(index), EnumFacing.UP, PlayerDiggingPacket.Action.STOP_DESTROY_BLOCK));
        }
      }
    }
  }

  // Get next wave to be sent
  private RequestsWave nextWave() {
    if(this.retry.isEmpty()) return this.parameters.getNextWave(ScanBot.WAVE_SIZE, this.nextACKNumber());

    RequestsWave next = this.retry.remove();
    next.reset(this.nextACKNumber());
    return next;
  }

  // new ACK number to be used in next wave
  private int nextACKNumber() {
    this.current_ack_number = -17 - (((-1 * (this.current_ack_number + 17)) + 1) % 2031);
    return this.current_ack_number;
  }

  private void printRemainingTime() {
    int total = this.parameters.bestCaseRemainingTime(ScanBot.WAVE_SIZE, ScanBot.TICK_RATE);
    int hours = total / 3600;
    int minutes = total / 60 - hours * 60;
    int seconds = total - minutes * 60 - hours * 3600;
    this.dateAndPrint(String.format("Best case remaining time: %d hours %d min %d sec", hours, minutes, seconds));
  }
}
