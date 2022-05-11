package tr.com.infumia.bukkitinventory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.event.base.SmartEvent;

/**
 * a class that represents handles.
 *
 * @param cls the cls.
 * @param consumer the consumer.
 * @param requirements the requirements.
 * @param <T> type of the event.
 */
public record Handle<T extends SmartEvent>(
  @NotNull Class<T> cls,
  @NotNull Consumer<T> consumer,
  @NotNull List<Predicate<T>> requirements
) implements Consumer<T> {

  @Override
  public void accept(@NotNull final T t) {
    if (this.requirements.stream().allMatch(req -> req.test(t))) {
      this.consumer.accept(t);
    }
  }
}
