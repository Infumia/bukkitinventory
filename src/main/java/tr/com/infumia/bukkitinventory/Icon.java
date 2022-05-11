package tr.com.infumia.bukkitinventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.bukkitinventory.event.IconClickEvent;
import tr.com.infumia.bukkitinventory.event.IconDragEvent;
import tr.com.infumia.bukkitinventory.event.base.IconEvent;
import tr.com.infumia.bukkitinventory.event.base.RealEvent;

/**
 * a class that represents icons.
 */
@Setter
@RequiredArgsConstructor
@Accessors(fluent = true)
public final class Icon {

  /**
   * an empty {@link Icon} instance.
   */
  public static final Icon EMPTY = Icon.from(new ItemStack(Material.AIR));

  /**
   * the handle list.
   */
  private final Collection<Handle<? extends IconEvent<?>>> handles = new ArrayList<>();

  /**
   * the can see.
   */
  @Getter
  @NotNull
  private Predicate<InventoryContext> canSee = contents -> true;

  /**
   * the can use.
   */
  @Getter
  @NotNull
  private Predicate<InventoryContext> canUse = contents -> true;

  /**
   * the fallback.
   */
  @Getter
  @NotNull
  private ItemStack fallback = new ItemStack(Material.AIR);

  /**
   * the item.
   */
  @NotNull
  private ItemStack item;

  /**
   * creates a simple icon from the given {@link ItemStack} with {@link RealEvent#cancel()} interaction.
   *
   * @param item the item to create.
   *
   * @return a simple icon instance.
   */
  @NotNull
  public static Icon cancel(@NotNull final ItemStack item) {
    return Icon.from(item)
      .whenClick(RealEvent::cancel)
      .whenDrag(RealEvent::cancel);
  }

  /**
   * creates a simple icon from the given {@link ItemStack} with a {@link IconClickEvent}.
   *
   * @param item the item to create.
   * @param consumer the consumer to run.
   * @param requirements the requirements to check.
   *
   * @return a simple icon instance.
   */
  @NotNull
  @SafeVarargs
  public static Icon click(@NotNull final ItemStack item, @NotNull final Consumer<IconClickEvent> consumer,
                           @NotNull final Predicate<IconClickEvent>... requirements) {
    return Icon.from(item)
      .whenClick(consumer, Arrays.asList(requirements));
  }

  /**
   * creates a simple icon from the given {@link ItemStack} with a {@link IconDragEvent}.
   *
   * @param item the item to create.
   * @param consumer the consumer to run.
   * @param requirements the requirements to check.
   *
   * @return a simple icon instance.
   */
  @NotNull
  @SafeVarargs
  public static Icon drag(@NotNull final ItemStack item, @NotNull final Consumer<IconDragEvent> consumer,
                          @NotNull final Predicate<IconDragEvent>... requirements) {
    return Icon.from(item)
      .whenDrag(consumer, Arrays.asList(requirements));
  }

  /**
   * creates a simple icon from the given {@link ItemStack}.
   *
   * @param item the item to create.
   *
   * @return a simple icon instance.
   */
  @NotNull
  public static Icon from(@NotNull final ItemStack item) {
    return new Icon(item);
  }

  /**
   * accepts the upcoming event for all the handles.
   *
   * @param event the event to accept.
   * @param <T> type of the event.
   */
  public <T extends IconEvent<?>> void accept(@NotNull final T event) {
    final var contents = event.context();
    if (this.canSee.test(contents) && this.canUse.test(contents)) {
      //noinspection unchecked
      this.handles.stream()
        .filter(target -> target.cls().isAssignableFrom(event.getClass()))
        .forEach(target -> ((Handle<T>) target).accept(event));
    }
  }

  /**
   * calculates and returns the item of the icon.
   * tests the {@code canSee} with the given contents, and if it returns {@code true},
   * returns {@link #item} else, returns the fallback.
   *
   * @param contents the contents to calculate.
   *
   * @return the calculated item.
   */
  @NotNull
  public ItemStack calculateItem(@NotNull final InventoryContext contents) {
    final ItemStack calculated;
    if (this.canSee.test(contents)) {
      calculated = this.item;
    } else {
      calculated = this.fallback;
    }
    return calculated;
  }

  /**
   * adds the given handle into the icon's handle list.
   *
   * @param handle the handle to add.
   * @param <T> type of the event.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public <T extends IconEvent<?>> Icon handle(@NotNull final Handle<T> handle) {
    this.handles.add(handle);
    return this;
  }

  /**
   * add the given event and requirements to the icon's handles.
   *
   * @param cls the class to determine the type of the event.
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   * @param <T> type of the event.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public <T extends IconEvent<?>> Icon handle(@NotNull final Class<T> cls, @NotNull final Consumer<T> consumer,
                                              @NotNull final List<Predicate<T>> requirements) {
    return this.handle(new Handle<>(cls, consumer, requirements));
  }

  /**
   * adds all the given handles into the icon's handle list.
   *
   * @param handles the handles to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Icon handles(@NotNull final Collection<Handle<? extends IconEvent<?>>> handles) {
    this.handles.addAll(handles);
    return this;
  }

  /**
   * obtains the item.
   *
   * @return item.
   */
  @NotNull
  public ItemStack item() {
    return this.item;
  }

  /**
   * adds the given {@link IconClickEvent} to the icon's handles.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Icon whenClick(@NotNull final Consumer<IconClickEvent> consumer) {
    return this.whenClick(consumer, Collections.emptyList());
  }

  /**
   * adds the given {@link IconClickEvent} with the requirement to the icon's handles.
   *
   * @param consumer the consumer to add.
   * @param requirement the requirement to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Icon whenClick(@NotNull final Consumer<IconClickEvent> consumer,
                        @NotNull final Predicate<IconClickEvent> requirement) {
    return this.handle(IconClickEvent.class, consumer, Collections.singletonList(requirement));
  }

  /**
   * adds the given {@link IconClickEvent} with the requirements to the icon's handles.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Icon whenClick(@NotNull final Consumer<IconClickEvent> consumer,
                        @NotNull final List<Predicate<IconClickEvent>> requirements) {
    return this.handle(IconClickEvent.class, consumer, requirements);
  }

  /**
   * adds the given {@link IconClickEvent} to the icon's handles.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Icon whenDrag(@NotNull final Consumer<IconDragEvent> consumer) {
    return this.whenDrag(consumer, Collections.emptyList());
  }

  /**
   * adds the given {@link IconDragEvent} with the requirement to the icon's handles.
   *
   * @param consumer the consumer to add.
   * @param requirement the requirement to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Icon whenDrag(@NotNull final Consumer<IconDragEvent> consumer,
                       @NotNull final Predicate<IconDragEvent> requirement) {
    return this.whenDrag(consumer, Collections.singletonList(requirement));
  }

  /**
   * adds the given {@link IconDragEvent} with the requirements to the icon's handles.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Icon whenDrag(@NotNull final Consumer<IconDragEvent> consumer,
                       @NotNull final List<Predicate<IconDragEvent>> requirements) {
    return this.handle(IconDragEvent.class, consumer, requirements);
  }
}
