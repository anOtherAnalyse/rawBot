package rawbot.connection.packets;

import java.lang.Class;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.util.Map;
import java.util.HashMap;

import rawbot.connection.EnumConnectionState;
import rawbot.connection.packets.in.PacketIn;
import rawbot.connection.packets.in.login.*;
import rawbot.connection.packets.in.play.*;
import rawbot.connection.packets.in.status.*;

/* Record of inbound packets by id */

public class Packets {

  private Map<Integer, Class<? extends PacketIn>> inbound_packets;

  public Packets() {
    this.inbound_packets = new HashMap<Integer, Class<? extends PacketIn>>();
  }

  public void setConnexionState(EnumConnectionState state) {
    this.inbound_packets.clear();

    switch(state) {
      case HANDSHAKING:
        break;
      case PLAY:
        this.setPlayPackets();
        break;
      case STATUS:
        this.setStatusPackets();
        break;
      case LOGIN:
        this.setLoginPackets();
    }
  }

  public Class<? extends PacketIn> getById(int id) {
    return this.inbound_packets.get(id);
  }

  private void registerPacket(Class<? extends PacketIn> packet, EnumConnectionState state) {
    PacketIn instance = null;

    try {
      instance = packet.newInstance(); // handle exceptions
    } catch(InstantiationException e) {
      throw new RuntimeException(String.format("Could no instantiate object of type %s", packet.toString()));
    } catch(IllegalAccessException e) {
      throw new RuntimeException(String.format("Constructor of class %s as private access", packet.toString()));
    }

    if(this.inbound_packets.put(instance.getId(state), packet) != null)
      throw new RuntimeException(String.format("Packet ID %d mapped twice in inbound packets record", instance.getId(state)));
  }

  private void setStatusPackets() {
    this.registerPacket(StatusAnswerPacket.class, EnumConnectionState.STATUS);
  }

  private void setLoginPackets() {
    this.registerPacket(DisconnectPacket.class, EnumConnectionState.LOGIN);
    this.registerPacket(EncryptionRequestPacket.class, EnumConnectionState.LOGIN);
    this.registerPacket(LoginSuccessPacket.class, EnumConnectionState.LOGIN);
    this.registerPacket(EnableCompressionPacket.class, EnumConnectionState.LOGIN);
  }

  private void setPlayPackets() {
    this.registerPacket(SpawnMobPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(SpawnPlayerPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(UpdateTileEntityPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(BlockChangePacket.class, EnumConnectionState.PLAY);
    this.registerPacket(ServerDifficultyPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(ChatReceivePacket.class, EnumConnectionState.PLAY);
    this.registerPacket(ConfirmTransactionPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(SetSlotPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(CustomPayloadPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(CustomSoundPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(DisconnectPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(EntityStatusPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(UnloadChunkPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(ChangeGameStatePacket.class, EnumConnectionState.PLAY);
    this.registerPacket(KeepAliveRequestPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(ChunkDataPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(JoinGamePacket.class, EnumConnectionState.PLAY);
    this.registerPacket(MapPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(PlayerAbilitiesPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(PlayerListItemPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(PlayerPosLookPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(DestroyEntitiesPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(RespawnPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(HeldItemChangePacket.class, EnumConnectionState.PLAY);
    this.registerPacket(EntityMetaDataPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(EntityAttachPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(UpdateHealthPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(ScoreboardObjectivePacket.class, EnumConnectionState.PLAY);
    this.registerPacket(SetPassengersPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(TeamPacket.class, EnumConnectionState.PLAY);
    this.registerPacket(TimeUpdatePacket.class, EnumConnectionState.PLAY);
  }
}
