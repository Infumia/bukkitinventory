package tr.com.infumia.bukkitinventory.event;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.InventoryContext;
import tr.com.infumia.bukkitinventory.event.base.PageEvent;

/**
 * a class that represents page close events.
 *
 * @param context the context.
 * @param event the event.
 * @param plugin the plugin.
 */
public record PageCloseEvent(
  @NotNull InventoryContext context,
  @NotNull InventoryCloseEvent event,
  @NotNull Plugin plugin
) implements PageEvent {

}
