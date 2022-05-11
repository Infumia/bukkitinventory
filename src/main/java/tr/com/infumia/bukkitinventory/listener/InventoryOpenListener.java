package tr.com.infumia.bukkitinventory.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.SmartInventory;
import tr.com.infumia.bukkitinventory.event.PageOpenEvent;

/**
 * a class that represents inventory open listeners.
 *
 * @param plugin the plugin.
 */
public record InventoryOpenListener(
  @NotNull Plugin plugin
) implements Listener {

  /**
   * listens the inventory open events.
   *
   * @param event the event to listen.
   */
  @EventHandler
  public void onInventoryOpen(final InventoryOpenEvent event) {
    final var holderOptional = SmartInventory.getHolder(event.getPlayer().getUniqueId());
    if (holderOptional.isEmpty()) {
      return;
    }
    final var holder = holderOptional.get();
    holder.page().accept(new PageOpenEvent(holder.context(), event, holder.plugin()));
  }
}
