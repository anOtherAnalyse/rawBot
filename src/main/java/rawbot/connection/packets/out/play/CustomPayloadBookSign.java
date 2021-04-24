package rawbot.connection.packets.out.play;

import java.io.IOException;

import rawbot.game.items.ItemStack;

public class CustomPayloadBookSign extends CustomPayloadBookEdit {

  public CustomPayloadBookSign(ItemStack book) throws IOException {
    super(book);
    this.channel = "MC|BSign";
  }
}
