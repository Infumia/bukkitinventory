package tr.com.infumia.bukkitinventory.event;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.Icon;
import tr.com.infumia.bukkitinventory.InventoryContext;
import tr.com.infumia.bukkitinventory.event.base.ClickEvent;
import tr.com.infumia.bukkitinventory.event.base.IconEvent;

/**
 * a class that represents icon icon events.
 *
 * @param context the context.
 * @param event the event.
 * @param icon the icon.
 * @param plugin the plugin.
 */
public record IconClickEvent(
  @NotNull InventoryContext context,
  @NotNull InventoryClickEvent event,
  @NotNull Icon icon,
  @NotNull Plugin plugin
) implements ClickEvent, IconEvent<InventoryClickEvent> {

}
