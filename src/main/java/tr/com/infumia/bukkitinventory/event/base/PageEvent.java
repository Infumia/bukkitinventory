package tr.com.infumia.bukkitinventory.event.base;

import org.bukkit.Bukkit;
import tr.com.infumia.bukkitinventory.SmartInventory;

/**
 * an interface to determine click events.
 */
public interface PageEvent extends SmartEvent {

  /**
   * closes the page.
   */
  default void close() {
    Bukkit.getScheduler().runTask(this.plugin(), () ->
      SmartInventory.getHolder(this.context().player()).ifPresent(holder ->
        holder.player().closeInventory()));
  }
}
