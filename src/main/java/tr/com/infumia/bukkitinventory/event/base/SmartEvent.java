package tr.com.infumia.bukkitinventory.event.base;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.InventoryContext;

/**
 * an interface to determine smart events.
 */
public interface SmartEvent {

  /**
   * obtains the context.
   *
   * @return context.
   */
  @NotNull
  InventoryContext context();

  /**
   * obtains the plugin.
   *
   * @return plugin.
   */
  @NotNull
  Plugin plugin();
}
