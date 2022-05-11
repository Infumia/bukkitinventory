package tr.com.infumia.bukkitinventory.event;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.InventoryContents;
import tr.com.infumia.bukkitinventory.event.abs.UpdateEvent;

/**
 * a class that represents page update events.
 */
@RequiredArgsConstructor
public final class PgUpdateEvent implements UpdateEvent {

  /**
   * the contents.
   */
  @NotNull
  private final InventoryContents contents;

  @NotNull
  @Override
  public InventoryContents contents() {
    return this.contents;
  }
}
