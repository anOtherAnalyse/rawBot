package rawbot.game.items;

import rawbot.game.nbt.NBTTagCompound;

public class ItemStack {

  public final static ItemStack EMPTY = new ItemStack(null, 0, 0);

  private Item item;
  private int size;
  private int damage;

  private NBTTagCompound stackTagCompound;

  public ItemStack(Item item, int size, int damage) {
    this.item = item;
    this.damage = damage;
    this.size = size;
    this.stackTagCompound = null;
  }

  public int getSize() {
    return this.size;
  }

  public Item getItem() {
    return this.item;
  }

  public int getDamage() {
    return this.damage;
  }

  public NBTTagCompound getTagCompound() {
    return this.stackTagCompound;
  }

  public void setTagCompound(NBTTagCompound nbt) {
    this.stackTagCompound = nbt;
  }
}
