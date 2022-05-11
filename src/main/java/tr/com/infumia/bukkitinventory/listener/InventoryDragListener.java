package tr.com.infumia.bukkitinventory.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.SmartInventory;
import tr.com.infumia.bukkitinventory.event.IconDragEvent;
import tr.com.infumia.bukkitinventory.util.SlotPos;

/**
 * a class that represents inventory drag listeners.
 *
 * @param plugin the plugin.
 */
public record InventoryDragListener(
  @NotNull Plugin plugin
) implements Listener {

  /**
   * listens inventory drag events.
   *
   * @param event the event to listen.
   */
  @EventHandler(priority = EventPriority.LOW)
  public void onInventoryDrag(final InventoryDragEvent event) {
    final var holderOptional = SmartInventory.getHolder(event.getWhoClicked().getUniqueId());
    if (holderOptional.isEmpty()) {
      return;
    }
    final var holder = holderOptional.get();
    final var inventory = event.getInventory();
    final var contents = holder.context();
    for (final var slot : event.getRawSlots()) {
      final var pos = SlotPos.of(slot);
      contents.get(pos).ifPresent(icon ->
        icon.accept(new IconDragEvent(contents, event, icon, this.plugin)));
      if (slot < inventory.getSize() && !contents.isEditable(pos)) {
        event.setCancelled(true);
        break;
      }
    }
  }
}
