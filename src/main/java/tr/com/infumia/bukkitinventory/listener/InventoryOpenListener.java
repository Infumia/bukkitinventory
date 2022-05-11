package tr.com.infumia.bukkitinventory.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import tr.com.infumia.bukkitinventory.SmartInventory;
import tr.com.infumia.bukkitinventory.event.PgOpenEvent;

/**
 * a class that represents inventory open listeners.
 */
public final class InventoryOpenListener implements Listener {

  /**
   * listens the inventory open events.
   *
   * @param event the event to listen.
   */
  @EventHandler
  public void onInventoryOpen(final InventoryOpenEvent event) {
    SmartInventory.getHolder(event.getPlayer().getUniqueId()).ifPresent(holder ->
      holder.getPage().accept(new PgOpenEvent(holder.getContents(), event, holder.getPlugin())));
  }
}
