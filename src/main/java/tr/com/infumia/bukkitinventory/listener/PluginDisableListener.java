package tr.com.infumia.bukkitinventory.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.SmartInventory;
import tr.com.infumia.bukkitinventory.event.PluginDisabledEvent;

/**
 * a class that represents plugin disable events.
 *
 * @param plugin the plugin.
 */
public record PluginDisableListener(
  @NotNull Plugin plugin
) implements Listener {

  /**
   * listens the plugin disable events.
   *
   * @param event the event to listen.
   */
  @EventHandler
  public void onPluginDisable(final PluginDisableEvent event) {
    SmartInventory.getHolders().forEach(holder -> {
      final var page = holder.page();
      page.accept(new PluginDisabledEvent(holder.context(), event, this.plugin));
      holder.player().closeInventory();
    });
  }
}
