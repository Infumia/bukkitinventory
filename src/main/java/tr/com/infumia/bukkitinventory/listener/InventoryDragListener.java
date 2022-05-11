package tr.com.infumia.bukkitinventory.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import tr.com.infumia.bukkitinventory.SmartInventory;
import tr.com.infumia.bukkitinventory.event.IcDragEvent;
import tr.com.infumia.bukkitinventory.util.SlotPos;

/**
 * a class that represents inventory drag listeners.
 */
public final class InventoryDragListener implements Listener {

  /**
   * listens inventory drag events.
   *
   * @param event the event to listen.
   */
  @EventHandler(priority = EventPriority.LOW)
  public void onInventoryDrag(final InventoryDragEvent event) {
    SmartInventory.getHolder(event.getWhoClicked().getUniqueId()).ifPresent(holder -> {
      final var inventory = event.getInventory();
      final var contents = holder.getContents();
      for (final var slot : event.getRawSlots()) {
        final var pos = SlotPos.of(slot);
        contents.get(pos).ifPresent(icon ->
          icon.accept(new IcDragEvent(contents, event, icon, holder.getPlugin())));
        if (slot < inventory.getSize() && !contents.isEditable(pos)) {
          event.setCancelled(true);
          break;
        }
      }
    });
  }
}
