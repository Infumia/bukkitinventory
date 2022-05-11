package tr.com.infumia.bukkitinventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine inventory holders.
 */
@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public final class SmartHolder implements InventoryHolder {

  /**
   * the contents.
   */
  @NotNull
  private final InventoryContext context;

  /**
   * the active.
   */
  @Setter
  private boolean active = true;

  @Override
  @NotNull
  public Inventory getInventory() {
    return this.context.topInventory();
  }

  /**
   * obtains the page.
   *
   * @return page.
   */
  @NotNull
  public Page page() {
    return this.context().page();
  }

  /**
   * obtains the player.
   *
   * @return player.
   */
  @NotNull
  public Player player() {
    return this.context().player();
  }

  /**
   * obtains the plugin.
   *
   * @return plugin.
   */
  @NotNull
  public Plugin plugin() {
    return this.page().inventory().plugin();
  }
}
