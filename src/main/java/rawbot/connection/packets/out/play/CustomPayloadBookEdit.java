package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.connection.WriteBuffer;
import rawbot.game.items.ItemStack;

public class CustomPayloadBookEdit extends CustomPayloadPacket {

  public CustomPayloadBookEdit(ItemStack book) throws IOException {
    super("MC|BEdit");
    WriteBuffer buff = new WriteBuffer();
    buff.writeItemStack(book);
    this.data = buff.getBuff();
  }
}
