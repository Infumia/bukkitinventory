package tr.com.infumia.bukkitinventory.event.base;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine smart events.
 *
 * @param <E> type of the event.
 */
public interface RealEvent<E extends Event> extends SmartEvent {

  /**
   * cancels the event.
   */
  default void cancel() {
    if (this.event() instanceof Cancellable cancellable) {
      cancellable.setCancelled(true);
    }
  }

  /**
   * checks if the event is cancelled.
   *
   * @return {@code true} if the event is cancelled.
   */
  default boolean cancelled() {
    return this.event() instanceof Cancellable cancellable && cancellable.isCancelled();
  }

  /**
   * obtains the event.
   *
   * @return event.
   */
  @NotNull
  E event();
}
