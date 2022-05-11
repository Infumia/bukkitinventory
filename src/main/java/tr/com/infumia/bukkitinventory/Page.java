package tr.com.infumia.bukkitinventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.bukkitinventory.event.PageBottomClickEvent;
import tr.com.infumia.bukkitinventory.event.PageClickEvent;
import tr.com.infumia.bukkitinventory.event.PageCloseEvent;
import tr.com.infumia.bukkitinventory.event.PageInitEvent;
import tr.com.infumia.bukkitinventory.event.PageOpenEvent;
import tr.com.infumia.bukkitinventory.event.PageOutsideClickEvent;
import tr.com.infumia.bukkitinventory.event.PageTickEvent;
import tr.com.infumia.bukkitinventory.event.PageUpdateEvent;
import tr.com.infumia.bukkitinventory.event.base.PageEvent;
import tr.com.infumia.bukkitinventory.event.base.SmartEvent;

/**
 * an interface to determine {@link Inventory}.
 */
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Page {

  /**
   * the handles.
   */
  private final Collection<Handle<? extends PageEvent>> handles = new ArrayList<>();

  /**
   * the inventory manager.
   */
  @Getter
  @NotNull
  private final SmartInventory inventory;

  /**
   * the async.
   */
  @Setter
  @Getter
  private boolean async = false;

  /**
   * the can close.
   */
  @Setter
  @Getter
  @NotNull
  private Predicate<PageCloseEvent> canClose = event -> true;

  /**
   * the column.
   */
  @Setter
  @Getter
  private int column = 9;

  /**
   * the id.
   */
  @Setter
  @Getter
  @NotNull
  private String id = "none";

  /**
   * the parent.
   */
  @Setter
  @Getter
  @Nullable
  private Page parent;

  /**
   * the row.
   */
  @Setter
  @Getter
  private int row = 1;

  /**
   * the start delay time.
   */
  @Setter
  @Getter
  private long startDelay = 1L;

  /**
   * the tick time.
   */
  @Setter
  @Getter
  private long tick = 1L;

  /**
   * the tick enable.
   */
  @Setter
  @Getter
  private boolean tickEnable = true;

  /**
   * the title.
   */
  @Setter
  @Getter
  @NotNull
  private String title = "Smart Inventory";

  /**
   * the inventory type.
   */
  @Setter
  @Getter
  @NotNull
  private InventoryType type = InventoryType.CHEST;

  /**
   * creates a simple page instance from the given parameters.
   *
   * @param inventory the inventory to create.
   *
   * @return a simple page instance.
   */
  public static Page build(@NotNull final SmartInventory inventory) {
    return new Page(inventory);
  }

  /**
   * accepts the upcoming event.
   *
   * @param event the event to accept.
   * @param <T> type of the event.
   */
  public <T extends SmartEvent> void accept(@NotNull final T event) {
    //noinspection unchecked
    this.handles.stream()
      .filter(handle -> handle.cls().isAssignableFrom(event.getClass()))
      .forEach(handle -> ((Handle<T>) handle).accept(event));
  }

  /**
   * checks the bounds.
   *
   * @param row the row to check.
   * @param column the column to check.
   *
   * @return {@code true} if the given row and column are correct for the page size.
   */
  public boolean checkBounds(final int row, final int column) {
    if (row >= 0) {
      return column >= 0;
    }
    if (row < this.row()) {
      return column < this.column();
    }
    return false;
  }

  /**
   * closes the player's page.
   *
   * @param holder the holder to close.
   */
  public void close(@NotNull final SmartHolder holder) {
    final var player = holder.player();
    final var context = holder.context();
    final var event = new PageCloseEvent(
      context,
      new InventoryCloseEvent(player.getOpenInventory()),
      holder.plugin()
    );
    if (!this.canClose.test(event)) {
      Bukkit.getScheduler().runTask(holder.plugin(), context::reopen);
      return;
    }
    this.accept(event);
    this.inventory.stopTick(player.getUniqueId());
    holder.active(false);
  }

  /**
   * adds the given handle.
   *
   * @param handle the handle to add.
   * @param <T> type of the event.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public <T extends PageEvent> Page handle(@NotNull final Handle<T> handle) {
    this.handles.add(handle);
    return this;
  }

  /**
   * adds the given consumer.
   *
   * @param cls the class to determine the type of the event.
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   * @param <T> type of the event.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public <T extends PageEvent> Page handle(@NotNull final Class<T> cls, @NotNull final Consumer<T> consumer,
                                           @NotNull final List<Predicate<T>> requirements) {
    return this.handle(new Handle<>(cls, consumer, requirements));
  }

  /**
   * notifies the update.
   *
   * @param context the context to notify.
   */
  public void notifyUpdate(@NotNull final InventoryContext context) {
    this.accept(new PageUpdateEvent(context, this.inventory.plugin()));
  }

  /**
   * notifies update event for all.
   */
  public void notifyUpdateForAll() {
    SmartInventory.notifyUpdateForAllById(this.id);
  }

  /**
   * opens the page for the player.
   *
   * @param player the player to open.
   * @param page the page to open.
   * @param properties the properties to open with.
   * @param close the close to open.
   *
   * @return a new {@link Inventory} instance.
   */
  @NotNull
  public Inventory open(@NotNull final Player player, final int page, @NotNull final Map<String, Object> properties,
                        final boolean close) {
    if (close) {
      SmartInventory.getHolder(player).ifPresent(holder ->
        holder.player().closeInventory());
    } else {
      SmartInventory.getHolder(player).ifPresent(holder -> {
        final var oldPage = holder.page();
        if (this.row != oldPage.row() || this.column != oldPage.column()) {
          holder.player().closeInventory();
        }
      });
    }
    final var opener = this.inventory().findOpener(this.type).orElseThrow(() ->
      new IllegalStateException("No opener found for the inventory type " + this.type.name()));
    final var contents = new InventoryContext(this, player);
    contents.pagination().currentPage(page);
    properties.forEach(contents::property);
    this.accept(new PageInitEvent(contents, this.inventory.plugin()));
    final var opened = opener.open(contents);
    if (this.tickEnable()) {
      this.inventory().tick(player.getUniqueId(), this);
    }
    return opened;
  }

  /**
   * opens the page to the given player.
   *
   * @param player the player to open.
   *
   * @return a new {@link Inventory} instance.
   */
  @NotNull
  public Inventory open(@NotNull final Player player) {
    return this.open(player, 0);
  }

  /**
   * opens the page to the given player.
   *
   * @param player the player to open.
   * @param page the page to open.
   *
   * @return a new {@link Inventory} instance.
   */
  @NotNull
  public Inventory open(@NotNull final Player player, final int page) {
    return this.open(player, page, Collections.emptyMap());
  }

  /**
   * opens the page to the given player.
   *
   * @param player the player to open.
   * @param properties the properties to open.
   *
   * @return a new {@link Inventory} instance.
   */
  @NotNull
  public Inventory open(@NotNull final Player player, @NotNull final Map<String, Object> properties) {
    return this.open(player, 0, properties);
  }

  /**
   * opens the page for the player.
   *
   * @param player the player to open.
   * @param page the page to open.
   * @param properties the properties to open with.
   *
   * @return a new {@link Inventory} instance.
   */
  @NotNull
  public Inventory open(@NotNull final Player player, final int page, @NotNull final Map<String, Object> properties) {
    return this.open(player, page, properties, false);
  }

  /**
   * adds the given consumer as a bottom inventory click event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenBottomClick(@NotNull final Consumer<PageBottomClickEvent> consumer) {
    return this.whenBottomClick(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as a bottom inventory click event.
   *
   * @param consumer the consumer to add.
   * @param requirement the requirement to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenBottomClick(@NotNull final Consumer<PageBottomClickEvent> consumer,
                              @NotNull final Predicate<PageBottomClickEvent> requirement) {
    return this.whenBottomClick(consumer, Collections.singletonList(requirement));
  }

  /**
   * adds the given consumer as a bottom inventory click event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenBottomClick(@NotNull final Consumer<PageBottomClickEvent> consumer,
                              @NotNull final List<Predicate<PageBottomClickEvent>> requirements) {
    return this.handle(PageBottomClickEvent.class, consumer, requirements);
  }

  /**
   * adds the given consumer as a close event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenClose(@NotNull final Consumer<PageCloseEvent> consumer) {
    return this.whenClose(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as a close event.
   *
   * @param consumer the consumer to add.
   * @param requirement the requirement to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenClose(@NotNull final Consumer<PageCloseEvent> consumer,
                        @NotNull final Predicate<PageCloseEvent> requirement) {
    return this.whenClose(consumer, Collections.singletonList(requirement));
  }

  /**
   * adds the given consumer as a close event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenClose(@NotNull final Consumer<PageCloseEvent> consumer,
                        @NotNull final List<Predicate<PageCloseEvent>> requirements) {
    return this.handle(PageCloseEvent.class, consumer, requirements);
  }

  /**
   * adds the given consumer as an empty slot click event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenEmptyClick(@NotNull final Consumer<PageClickEvent> consumer) {
    return this.whenEmptyClick(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as an empty slot click event.
   *
   * @param consumer the consumer to add.
   * @param requirement the requirement to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenEmptyClick(@NotNull final Consumer<PageClickEvent> consumer,
                             @NotNull final Predicate<PageClickEvent> requirement) {
    return this.whenEmptyClick(consumer, Collections.singletonList(requirement));
  }

  /**
   * adds the given consumer as an empty slot click event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenEmptyClick(@NotNull final Consumer<PageClickEvent> consumer,
                             @NotNull final List<Predicate<PageClickEvent>> requirements) {
    return this.handle(PageClickEvent.class, consumer, requirements);
  }

  /**
   * adds the given consumer as an init event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenInit(@NotNull final Consumer<PageInitEvent> consumer) {
    return this.whenInit(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as an init event.
   *
   * @param consumer the consumer to add.
   * @param requirement the requirement to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenInit(@NotNull final Consumer<PageInitEvent> consumer,
                       @NotNull final Predicate<PageInitEvent> requirement) {
    return this.whenInit(consumer, Collections.singletonList(requirement));
  }

  /**
   * adds the given consumer as an init event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenInit(@NotNull final Consumer<PageInitEvent> consumer,
                       @NotNull final List<Predicate<PageInitEvent>> requirements) {
    return this.handle(PageInitEvent.class, consumer, requirements);
  }

  /**
   * adds the given consumer as an open event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenOpen(@NotNull final Consumer<PageOpenEvent> consumer) {
    return this.whenOpen(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as an open event.
   *
   * @param consumer the consumer to add.
   * @param requirement the requirement to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenOpen(@NotNull final Consumer<PageOpenEvent> consumer,
                       @NotNull final Predicate<PageOpenEvent> requirement) {
    return this.whenOpen(consumer, Collections.singletonList(requirement));
  }

  /**
   * adds the given consumer as an open event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenOpen(@NotNull final Consumer<PageOpenEvent> consumer,
                       @NotNull final List<Predicate<PageOpenEvent>> requirements) {
    return this.handle(PageOpenEvent.class, consumer, requirements);
  }

  /**
   * adds the given consumer as an outside inventory click event.
   *
   * @param consumer the consumer to add.
   * @param requirement the requirement to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenOutsideClick(@NotNull final Consumer<PageOutsideClickEvent> consumer,
                               @NotNull final Predicate<PageOutsideClickEvent> requirement) {
    return this.whenOutsideClick(consumer, Collections.singletonList(requirement));
  }

  /**
   * adds the given consumer as an outside inventory click event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenOutsideClick(@NotNull final Consumer<PageOutsideClickEvent> consumer) {
    return this.whenOutsideClick(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as an outside inventory click event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenOutsideClick(@NotNull final Consumer<PageOutsideClickEvent> consumer,
                               @NotNull final List<Predicate<PageOutsideClickEvent>> requirements) {
    return this.handle(PageOutsideClickEvent.class, consumer, requirements);
  }

  /**
   * adds the given consumer as a tick event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenTick(@NotNull final Consumer<PageTickEvent> consumer) {
    return this.whenTick(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as a tick event.
   *
   * @param consumer the consumer to add.
   * @param requirement the requirement to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenTick(@NotNull final Consumer<PageTickEvent> consumer,
                       @NotNull final Predicate<PageTickEvent> requirement) {
    return this.whenTick(consumer, Collections.singletonList(requirement));
  }

  /**
   * adds the given consumer as a tick event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenTick(@NotNull final Consumer<PageTickEvent> consumer,
                       @NotNull final List<Predicate<PageTickEvent>> requirements) {
    return this.handle(PageTickEvent.class, consumer, requirements);
  }

  /**
   * adds the given consumer as an update event.
   *
   * @param consumer the consumer to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenUpdate(@NotNull final Consumer<PageUpdateEvent> consumer) {
    return this.whenUpdate(consumer, Collections.emptyList());
  }

  /**
   * adds the given consumer as an update event.
   *
   * @param consumer the consumer to add.
   * @param requirement the requirement to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenUpdate(@NotNull final Consumer<PageUpdateEvent> consumer,
                         @NotNull final Predicate<PageUpdateEvent> requirement) {
    return this.whenUpdate(consumer, Collections.singletonList(requirement));
  }

  /**
   * adds the given consumer as an update event.
   *
   * @param consumer the consumer to add.
   * @param requirements the requirements to add.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Page whenUpdate(@NotNull final Consumer<PageUpdateEvent> consumer,
                         @NotNull final List<Predicate<PageUpdateEvent>> requirements) {
    return this.handle(PageUpdateEvent.class, consumer, requirements);
  }
}
