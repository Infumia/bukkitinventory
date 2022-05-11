package tr.com.infumia.bukkitinventory.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.InventoryContents;
import tr.com.infumia.bukkitinventory.SmartInventory;
import tr.com.infumia.bukkitinventory.event.abs.OpenEvent;

/**
 * a class that represents page open events.
 */
@RequiredArgsConstructor
public final class PgOpenEvent implements OpenEvent {

  /**
   * the contents.
   */
  @NotNull
  private final InventoryContents contents;

  /**
   * the event.
   */
  @NotNull
  private final InventoryOpenEvent event;

  /**
   * the plugin.
   */
  @NotNull
  private final Plugin plugin;

  @Override
  public void cancel() {
    this.event.setCancelled(true);
  }

  @Override
  public void close() {
    Bukkit.getScheduler().runTask(this.plugin, () ->
      SmartInventory.getHolder(this.contents.player()).ifPresent(holder ->
        holder.getPlayer().closeInventory()));
  }

  @NotNull
  @Override
  public InventoryContents contents() {
    return this.contents;
  }

  @NotNull
  @Override
  public InventoryOpenEvent getEvent() {
    return this.event;
  }
}
