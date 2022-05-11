package tr.com.infumia.bukkitinventory.event;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.InventoryContext;
import tr.com.infumia.bukkitinventory.event.base.RealEvent;

/**
 * a class that represents plugin disabled event events.
 *
 * @param context the context.
 * @param event the event.
 * @param plugin the plugin.
 */
public record PluginDisabledEvent(
  @NotNull InventoryContext context,
  @NotNull PluginDisableEvent event,
  @NotNull Plugin plugin
) implements RealEvent<PluginDisableEvent> {

}
