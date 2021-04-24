package rawbot.bot;

import java.lang.InterruptedException;
import java.lang.Thread;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import rawbot.authlib.AuthenticationException;
import rawbot.connection.Connection;
import rawbot.connection.ConnectionInit;
import rawbot.connection.EnumConnectionState;
import rawbot.connection.exceptions.DisconnectedException;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.packets.in.play.EntityMetaDataPacket;
import rawbot.connection.packets.in.play.JoinGamePacket;
import rawbot.connection.packets.in.play.KeepAliveRequestPacket;
import rawbot.connection.packets.out.PacketOut;
import rawbot.connection.packets.out.play.KeepAliveResponsePacket;

/* Bot backbone */
public abstract class Bot {

  private static final int RETRY_DELAY = 10000; // delay before retrying to connect to server after failure
  private static final int RECONNECT_DELAY = 2000; // after beeing disconnected

  private Connection co;

  protected int player_id;

  private Lock sequential_lock; // helps keeping all operations synchronous

  public Bot() {
    this.sequential_lock = new ReentrantLock();
  }

  /* Bot methods to overwrite - all ran synchronously */

  protected abstract void onPacketReceived(PacketIn packet);

  protected abstract void onDeath();

  protected abstract void onDisconnected(IOException exception);

  protected abstract void onTick(); // Ran 20 times per second

  /* Run the bot on a server */
  public void run(String host, int port, boolean stayConnected) {

    do {

      // Connect to server
      try {
        this.connect(host, port);
      } catch(AuthenticationException except) {
        System.err.println(String.format("Authentication error: %s", except.getMessage()));
        break;
      } catch(UnknownHostException except) {
        System.err.println(String.format("Unknown host: %s", except.getMessage()));
        break;
      } catch(IOException except) {
        System.err.println(String.format("Can not connect to server: %s", except.getMessage()));
      } catch(RuntimeException except) {
        System.err.println(String.format("Internal error: %s", except.getMessage()));
        except.printStackTrace();
        break;
      }

      if(this.co != null) {

        TickThread thread = new TickThread(this);
        thread.start();

        while(true) { // Get incoming packets

          PacketIn packet;
          try {
            packet = this.co.readPacket();
          } catch (DisconnectedException except) {
            System.out.println(String.format("Disconnected: %s", except.getMessage()));
            this.onDisconnectedAS(except);
            break;
          } catch (IOException except) {
            System.out.println(String.format("Socket closed: %s", except.getMessage()));
            this.onDisconnectedAS(except);
            break;
          } catch (RuntimeException except) {
            System.err.println(String.format("Internal error: %s", except.getMessage()));
            except.printStackTrace();
            stayConnected = false;
            break;
          }

          if(packet == null) continue; // Packet isn't implemented yet

          switch(packet.getId(EnumConnectionState.PLAY)) {
            case 31: // Respond to keep alive packets to stay connected
              this.sendPacket(new KeepAliveResponsePacket(((KeepAliveRequestPacket) packet).getNumber()));
              break;
            case 35:
              this.player_id = ((JoinGamePacket) packet).getPlayerId();
              break;
            case 60:
              {
                EntityMetaDataPacket meta = (EntityMetaDataPacket) packet;
                if(meta.getEntityId() == this.player_id) {
                  Object health = meta.getMetaData((byte) 7);
                  if(health != null && health instanceof Float && (Float)health <= 0f) {
                    this.onDeathAS();
                  }
                }
              }
              break;
          }

          this.onPacketReceivedAS(packet);
        }

        thread.setDead(); // Kill tick thread

        try {
          this.co.close();
        } catch(IOException except) {
          System.err.println("Could not close socket: " + except.getMessage());
        }

        if(stayConnected) {
          System.out.println(String.format("Reconnecting in %d seconds..", Bot.RECONNECT_DELAY / 1000));

          try {
            Thread.sleep(Bot.RECONNECT_DELAY);
          } catch (InterruptedException e) {
            System.err.println("Main thread was interrupted, aborting..");
            stayConnected = false;
          }
        }

      } else if(stayConnected) {
        System.out.println(String.format("Retrying in %d seconds..", Bot.RETRY_DELAY / 1000));

        try {
          Thread.sleep(Bot.RETRY_DELAY);
        } catch (InterruptedException e) {
          System.err.println("Main thread was interrupted, aborting..");
          stayConnected = false;
        }
      }

      this.co = null;
      this.player_id = 0;

    } while(stayConnected);
  }

  // Send a client packet to server
  protected void sendPacket(PacketOut packet) {
    try {
      this.co.writePacket(packet);
    } catch(IOException except) {
      System.err.println(String.format("Error: could not send packet \"%s\": %s", packet.getClass().toString(), except.getMessage()));
    }
  }

  private void connect(String host, int port) throws IOException, UnknownHostException, AuthenticationException {
    ConnectionInit handle = new ConnectionInit(host, port);
    handle.login();
    this.co = handle.getConnection();
  }

  /* Asynchronous callbacks - make sure every bot operation is treated sequentially - easier to manage */

  public void onTickAS() {
    this.sequential_lock.lock();
    this.onTick();
    this.sequential_lock.unlock();
  }

  private void onPacketReceivedAS(PacketIn packet) {
    this.sequential_lock.lock();
    this.onPacketReceived(packet);
    this.sequential_lock.unlock();
  }

  private void onDeathAS() {
    this.sequential_lock.lock();
    this.onDeath();
    this.sequential_lock.unlock();
  }

  private void onDisconnectedAS(IOException exception) {
    this.sequential_lock.lock();
    this.onDisconnected(exception);
    this.sequential_lock.unlock();
  }

  /* Tick thread */
  private static class TickThread extends Thread {
    private Bot bot;

    private boolean is_dead;

    public TickThread(Bot bot) {
      this.bot = bot;
      this.is_dead = false;
    }

    private boolean isDead() {
      return this.is_dead;
    }

    public void setDead() {
      this.is_dead = true;
    }

    public void run() {
      while(! this.is_dead) {
        this.bot.onTickAS();

        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          System.err.println("Tick thread was interrupted, aborting..");
          break;
        }
      }
    }
  }
}
