package tr.com.infumia.bukkitinventory.event.base;

import java.util.Optional;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine click events.
 */
public interface ClickEvent extends RealEvent<InventoryClickEvent>, PageEvent {

  /**
   * obtains the action type.
   *
   * @return action type.
   */
  @NotNull
  default InventoryAction action() {
    return this.event().getAction();
  }

  /**
   * obtains the click type.
   *
   * @return click type.
   */
  @NotNull
  default ClickType clickType() {
    return this.event().getClick();
  }

  /**
   * obtains the column.
   *
   * @return column.
   */
  default int column() {
    return this.event().getSlot() % 9;
  }

  /**
   * obtains the current item.
   *
   * @return current item.
   */
  @NotNull
  default Optional<ItemStack> current() {
    return Optional.ofNullable(this.event().getCurrentItem());
  }

  /**
   * obtains the cursor.
   *
   * @return cursor.
   */
  @NotNull
  default Optional<ItemStack> cursor() {
    return Optional.ofNullable(this.event().getCursor());
  }

  /**
   * obtains the row.
   *
   * @return row.
   */
  default int row() {
    return this.event().getSlot() / 9;
  }

  /**
   * obtains the slot type.
   *
   * @return slot type.
   */
  @NotNull
  default InventoryType.SlotType slotType() {
    return this.event().getSlotType();
  }
}
