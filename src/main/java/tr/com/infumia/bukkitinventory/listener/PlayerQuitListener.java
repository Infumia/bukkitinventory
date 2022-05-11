package tr.com.infumia.bukkitinventory.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.SmartInventory;
import tr.com.infumia.bukkitinventory.event.PlayerQuitedEvent;

/**
 * a class that represents player quit listeners.
 *
 * @param plugin the plugin.
 */
public record PlayerQuitListener(
  @NotNull Plugin plugin
) implements Listener {

  /**
   * listens the player quit event.
   *
   * @param event the event to listen.
   */
  @EventHandler
  public void onPlayerQuit(final PlayerQuitEvent event) {
    final var holderOptional = SmartInventory.getHolder(event.getPlayer());
    if (holderOptional.isEmpty()) {
      return;
    }
    final var holder = holderOptional.get();
    final var page = holder.page();
    page.accept(new PlayerQuitedEvent(holder.context(), event, this.plugin));
    page.inventory().stopTick(event.getPlayer().getUniqueId());
  }
}
