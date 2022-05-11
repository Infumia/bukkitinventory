package tr.com.infumia.bukkitinventory;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * a class that opens {@link Inventory}s from the given {@link InventoryType}s.
 */
public interface InventoryOpener {

  /**
   * fills the given context to the given inventory.
   *
   * @param inventory the inventory to fill.
   * @param context the context to fill.
   */
  default void fill(@NotNull final Inventory inventory, @NotNull final InventoryContext context) {
    final var items = context.all();
    for (var row = 0; row < items.length; row++) {
      for (var column = 0; column < items[row].length; column++) {
        if (items[row][column] != null) {
          inventory.setItem(9 * row + column, items[row][column].calculateItem(context));
        }
      }
    }
  }

  /**
   * opens the page for the given player.
   *
   * @param context the context to open.
   *
   * @return opened inventory itself.
   */
  @NotNull
  Inventory open(@NotNull InventoryContext context);

  /**
   * checks if the inventory type is supporting for {@code this}.
   *
   * @param type the type to check.
   *
   * @return {@code true} if the type supports the type..
   */
  boolean supports(@NotNull InventoryType type);
}
