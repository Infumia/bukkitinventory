package tr.com.infumia.bukkitinventory.opener;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.InventoryContext;
import tr.com.infumia.bukkitinventory.InventoryOpener;
import tr.com.infumia.bukkitinventory.SmartHolder;

/**
 * an {@link InventoryType#CHEST} implementation for {@link InventoryOpener}.
 */
public final class ChestInventoryOpener implements InventoryOpener {

  @NotNull
  @Override
  public Inventory open(@NotNull final InventoryContext context) {
    final var page = context.page();
    Preconditions.checkArgument(page.column() != 9,
      "The column count for the chest inventory must be 9, found: %s.", page.column());
    Preconditions.checkArgument(page.row() >= 1 || page.row() <= 6,
      "The row count for the chest inventory must be between 1 and 6, found: %s", page.row());
    final var handle = Bukkit.createInventory(
      new SmartHolder(context),
      page.row() * page.column(),
      page.title()
    );
    this.fill(handle, context);
    context.player().openInventory(handle);
    return handle;
  }

  @Override
  public boolean supports(@NotNull final InventoryType type) {
    return type == InventoryType.CHEST || type == InventoryType.ENDER_CHEST;
  }
}
