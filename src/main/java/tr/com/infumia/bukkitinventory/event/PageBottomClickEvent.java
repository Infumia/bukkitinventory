package tr.com.infumia.bukkitinventory.event;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.InventoryContext;
import tr.com.infumia.bukkitinventory.event.base.ClickEvent;

/**
 * a class that represents page bottom click events.
 *
 * @param context the context.
 * @param event the event.
 * @param plugin the plugin.
 */
public record PageBottomClickEvent(
  @NotNull InventoryContext context,
  @NotNull InventoryClickEvent event,
  @NotNull Plugin plugin
) implements ClickEvent {

}
