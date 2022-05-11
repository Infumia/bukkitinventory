package tr.com.infumia.bukkitinventory.event.abs;

import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.InventoryContents;

/**
 * an interface to determine smart events.
 */
public interface SmartEvent {

  /**
   * cancels the vent.
   */
  default void cancel() {
  }

  /**
   * closes the inventory.
   */
  default void close() {
  }

  /**
   * obtains the contents.
   *
   * @return contents.
   */
  @NotNull
  InventoryContents contents();
}
