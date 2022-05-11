package tr.com.infumia.bukkitinventory.event.abs;

import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine  events.
 */
public interface OpenEvent extends PageEvent {

  /**
   * obtains the event.
   *
   * @return event.
   */
  @NotNull
  InventoryOpenEvent getEvent();
}
