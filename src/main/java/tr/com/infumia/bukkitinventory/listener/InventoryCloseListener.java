package tr.com.infumia.bukkitinventory.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.SmartInventory;

/**
 * a class that represents inventory close listeners.
 *
 * @param plugin the plugin.
 */
public record InventoryCloseListener(
  @NotNull Plugin plugin
) implements Listener {

  /**
   * listens inventory close events.
   *
   * @param event the event to listen.
   */
  @EventHandler
  public void onInventoryClose(final InventoryCloseEvent event) {
    final var holderOptional = SmartInventory.getHolder(event.getPlayer().getUniqueId());
    if (holderOptional.isEmpty()) {
      return;
    }
    final var holder = holderOptional.get();
    holder.page().close(holder);
  }
}
