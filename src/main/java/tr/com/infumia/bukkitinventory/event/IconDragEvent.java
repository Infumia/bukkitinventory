package tr.com.infumia.bukkitinventory.event;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.Icon;
import tr.com.infumia.bukkitinventory.InventoryContext;
import tr.com.infumia.bukkitinventory.event.base.IconEvent;
import tr.com.infumia.bukkitinventory.event.base.PageEvent;

/**
 * a class that represents icon drag events.
 *
 * @param context the context.
 * @param event the event.
 * @param icon the icon.
 * @param plugin the plugin.
 */
public record IconDragEvent(
  @NotNull InventoryContext context,
  @NotNull InventoryDragEvent event,
  @NotNull Icon icon,
  @NotNull Plugin plugin
) implements IconEvent<InventoryDragEvent>, PageEvent {

  /**
   * obtains the added items.
   *
   * @return added items.
   */
  @NotNull
  public Map<Integer, ItemStack> added() {
    return this.event.getNewItems();
  }

  /**
   * obtains the drag type.
   *
   * @return drag type.
   */
  @NotNull
  public DragType dragType() {
    return this.event.getType();
  }

  /**
   * obtains the new cursor.
   *
   * @return new cursor.
   */
  @NotNull
  public Optional<ItemStack> newCursor() {
    return Optional.ofNullable(this.event.getCursor());
  }

  /**
   * obtains the slots.
   *
   * @return slots.
   */
  @NotNull
  public Set<Integer> slots() {
    return this.event.getInventorySlots();
  }
}
