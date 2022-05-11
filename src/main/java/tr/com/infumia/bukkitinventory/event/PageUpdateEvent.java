package tr.com.infumia.bukkitinventory.event;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.InventoryContext;
import tr.com.infumia.bukkitinventory.event.base.PageEvent;

/**
 * a class that represents page update events.
 *
 * @param context the context.
 * @param plugin the plugin.
 */
public record PageUpdateEvent(
  @NotNull InventoryContext context,
  @NotNull Plugin plugin
) implements PageEvent {

}
