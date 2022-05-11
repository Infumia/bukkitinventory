package tr.com.infumia.bukkitinventory.event;

import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.InventoryContext;
import tr.com.infumia.bukkitinventory.event.base.PageEvent;
import tr.com.infumia.bukkitinventory.event.base.RealEvent;

/**
 * a class that represents page open events.
 *
 * @param context the context.
 * @param event the event.
 * @param plugin the plugin.
 */
public record PageOpenEvent(
  @NotNull InventoryContext context,
  @NotNull InventoryOpenEvent event,
  @NotNull Plugin plugin
) implements RealEvent<InventoryOpenEvent>, PageEvent {

}
