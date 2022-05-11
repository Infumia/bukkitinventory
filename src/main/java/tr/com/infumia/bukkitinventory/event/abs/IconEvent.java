package tr.com.infumia.bukkitinventory.event.abs;

import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.Icon;

/**
 * an interface to determine icon events.
 */
public interface IconEvent extends SmartEvent {

  /**
   * obtains the icon.
   *
   * @return icon.
   */
  @NotNull
  Icon icon();
}
