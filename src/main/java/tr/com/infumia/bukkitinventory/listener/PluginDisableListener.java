package tr.com.infumia.bukkitinventory.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import tr.com.infumia.bukkitinventory.SmartInventory;
import tr.com.infumia.bukkitinventory.event.PlgnDisableEvent;

/**
 * a class that represents plugin disable events.
 */
public final class PluginDisableListener implements Listener {

  /**
   * listens the plugin disable events.
   *
   * @param event the event to listen.
   */
  @EventHandler
  public void onPluginDisable(final PluginDisableEvent event) {
    SmartInventory.getHolders().forEach(holder -> {
      final var page = holder.getPage();
      page.accept(new PlgnDisableEvent(holder.getContents(), event));
      holder.getPlayer().closeInventory();
    });
  }
}
