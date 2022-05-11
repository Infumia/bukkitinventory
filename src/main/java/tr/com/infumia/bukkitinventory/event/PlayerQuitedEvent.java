package tr.com.infumia.bukkitinventory.event;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.InventoryContext;
import tr.com.infumia.bukkitinventory.event.base.RealEvent;

/**
 * a class that represents player quited event events.
 *
 * @param context the context.
 * @param event the event.
 * @param plugin the plugin.
 */
public record PlayerQuitedEvent(
  @NotNull InventoryContext context,
  @NotNull PlayerQuitEvent event,
  @NotNull Plugin plugin
) implements RealEvent<PlayerQuitEvent> {

}
