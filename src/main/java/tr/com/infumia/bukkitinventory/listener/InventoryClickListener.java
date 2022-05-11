package tr.com.infumia.bukkitinventory.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.SmartInventory;
import tr.com.infumia.bukkitinventory.event.IconClickEvent;
import tr.com.infumia.bukkitinventory.event.PageBottomClickEvent;
import tr.com.infumia.bukkitinventory.event.PageClickEvent;
import tr.com.infumia.bukkitinventory.event.PageOutsideClickEvent;
import tr.com.infumia.bukkitinventory.util.SlotPos;

/**
 * a class that represents inventory click listeners.
 *
 * @param plugin the plugin.
 */
public record InventoryClickListener(
  @NotNull Plugin plugin
) implements Listener {

  /**
   * listens inventory click events.
   *
   * @param event the event to listen.
   */
  @EventHandler
  public void onInventoryClick(final InventoryClickEvent event) {
    final var holderOptional = SmartInventory.getHolder(event.getWhoClicked().getUniqueId());
    if (holderOptional.isEmpty()) {
      return;
    }
    final var holder = holderOptional.get();
    if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
      event.setCancelled(true);
      return;
    }
    final var page = holder.page();
    final var contents = holder.context();
    final var plugin = holder.plugin();
    final var clicked = event.getClickedInventory();
    if (clicked == null) {
      page.accept(new PageOutsideClickEvent(contents, event, plugin));
      return;
    }
    final var player = event.getWhoClicked();
    if (clicked.equals(player.getOpenInventory().getBottomInventory())) {
      page.accept(new PageBottomClickEvent(contents, event, plugin));
      return;
    }
    final var current = event.getCurrentItem();
    if (current == null || current.getType() == Material.AIR) {
      page.accept(new PageClickEvent(contents, event, plugin));
      return;
    }
    final var slot = event.getSlot();
    final var row = slot / 9;
    final var column = slot % 9;
    if (!page.checkBounds(row, column)) {
      return;
    }
    final var slotPos = SlotPos.of(row, column);
    if (!contents.isEditable(slotPos)) {
      event.setCancelled(true);
    }
    contents.get(slotPos).ifPresent(item ->
      item.accept(new IconClickEvent(contents, event, item, plugin)));
    if (!contents.isEditable(slotPos) && player instanceof Player) {
      ((Player) player).updateInventory();
    }
  }
}
