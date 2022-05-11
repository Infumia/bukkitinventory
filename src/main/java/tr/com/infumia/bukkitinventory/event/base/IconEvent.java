package tr.com.infumia.bukkitinventory.event.base;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.Icon;

/**
 * an interface to determine smart events.
 *
 * @param <E> type of the event.
 */
public interface IconEvent<E extends Event> extends RealEvent<E> {

  /**
   * obtains the icon.
   *
   * @return icon.
   */
  @NotNull
  Icon icon();
}
