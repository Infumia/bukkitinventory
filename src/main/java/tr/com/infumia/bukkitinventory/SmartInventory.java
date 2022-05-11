package tr.com.infumia.bukkitinventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.bukkitinventory.event.PageTickEvent;
import tr.com.infumia.bukkitinventory.listener.InventoryClickListener;
import tr.com.infumia.bukkitinventory.listener.InventoryCloseListener;
import tr.com.infumia.bukkitinventory.listener.InventoryDragListener;
import tr.com.infumia.bukkitinventory.listener.InventoryOpenListener;
import tr.com.infumia.bukkitinventory.listener.PlayerQuitListener;
import tr.com.infumia.bukkitinventory.listener.PluginDisableListener;
import tr.com.infumia.bukkitinventory.opener.ChestInventoryOpener;

/**
 * a class that manages all smart inventories.
 */
@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public final class SmartInventory {

  /**
   * default inventory openers.
   */
  private static final List<InventoryOpener> DEFAULT_OPENERS = Collections.singletonList(
    new ChestInventoryOpener());

  /**
   * all listener to register.
   */
  private static final Function<Plugin, List<Listener>> LISTENERS = plugin -> Arrays.asList(
    new InventoryClickListener(plugin),
    new InventoryOpenListener(plugin),
    new InventoryCloseListener(plugin),
    new PlayerQuitListener(plugin),
    new PluginDisableListener(plugin),
    new InventoryDragListener(plugin));

  /**
   * the openers.
   */
  private final Collection<InventoryOpener> openers = new ArrayList<>();

  /**
   * the plugin.
   */
  @NotNull
  private final Plugin plugin;

  /**
   * the tasks.
   */
  private final Map<UUID, BukkitTask> tasks = new ConcurrentHashMap<>();

  static {
    try {
      Class.forName("tr.com.infumia.bukkitinventory.event.PluginDisabledEvent");
    } catch (final ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * closes all the smart inventories which are opened.
   */
  public static void closeAllSmartInventories() {
    Bukkit.getOnlinePlayers().stream()
      .filter(player -> SmartInventory.getHolder(player).isPresent())
      .forEach(HumanEntity::closeInventory);
  }

  /**
   * obtains the given {@code player}'s smart holder.
   *
   * @param player the player to obtain.
   *
   * @return smart holder.
   */
  @NotNull
  public static Optional<SmartHolder> getHolder(@NotNull final Player player) {
    final var holder = player.getOpenInventory().getTopInventory().getHolder();
    if (!(holder instanceof SmartHolder)) {
      return Optional.empty();
    }
    return Optional.of((SmartHolder) holder)
      .filter(SmartHolder::active);
  }

  /**
   * obtains the given {@code uniqueId}'s smart holder.
   *
   * @param uniqueId the unique id to obtain.
   *
   * @return smart holder.
   */
  @NotNull
  public static Optional<SmartHolder> getHolder(@NotNull final UUID uniqueId) {
    return Optional.ofNullable(Bukkit.getPlayer(uniqueId))
      .flatMap(SmartInventory::getHolder);
  }

  /**
   * obtains the given {@code player}'s smart holder.
   *
   * @param player the player to obtain.
   *
   * @return smart holder.
   */
  @Nullable
  public static SmartHolder getHolderOrNull(@NotNull final Player player) {
    return SmartInventory.getHolder(player).orElse(null);
  }

  /**
   * obtains the smart holders of all the online players.
   *
   * @return smart holders of online players.
   */
  @NotNull
  public static List<SmartHolder> getHolders() {
    return Bukkit.getOnlinePlayers().stream()
      .map(SmartInventory::getHolderOrNull)
      .filter(Objects::nonNull)
      .toList();
  }

  /**
   * obtains the players that see the given page.
   *
   * @param page the page to obtain.
   *
   * @return a player list.
   */
  @NotNull
  public static List<Player> getOpenedPlayers(@NotNull final Page page) {
    return SmartInventory.getHolders().stream()
      .filter(holder -> page.id().equals(holder.page().id()))
      .map(SmartHolder::player)
      .toList();
  }

  /**
   * notifies update for the player's inventory.
   *
   * @param player the player to notify.
   */
  public static void notifyUpdate(@NotNull final Player player) {
    SmartInventory.getHolder(player).ifPresent(smartHolder ->
      smartHolder.context().notifyUpdate());
  }

  /**
   * notifies for all page with the id.
   *
   * @param id the id to find and run the update method.
   */
  public static void notifyUpdateForAllById(@NotNull final String id) {
    SmartInventory.getHolders().stream()
      .map(SmartHolder::page)
      .filter(page -> page.id().equals(id))
      .forEach(Page::notifyUpdateForAll);
  }

  /**
   * finds a {@link InventoryOpener} from the given {@link InventoryType}.
   *
   * @param type the type to find.
   *
   * @return the inventory opener from the given type.
   */
  @NotNull
  public Optional<InventoryOpener> findOpener(@NotNull final InventoryType type) {
    for (final var opener : this.openers) {
      if (opener.supports(type)) {
        return Optional.of(opener);
      }
    }
    for (final var opener : SmartInventory.DEFAULT_OPENERS) {
      if (opener.supports(type)) {
        return Optional.of(opener);
      }
    }
    return Optional.empty();
  }

  /**
   * initiates the manager.
   */
  public void init() {
    SmartInventory.LISTENERS.apply(this.plugin).forEach(listener ->
      Bukkit.getPluginManager().registerEvents(listener, this.plugin));
  }

  /**
   * registers the given inventory openers.
   *
   * @param openers the openers to register.
   */
  public void registerOpeners(@NotNull final InventoryOpener... openers) {
    Collections.addAll(this.openers, openers);
  }

  /**
   * removes given uniqueId of the ticking task.
   *
   * @param uniqueId the uniqueId to set.
   */
  public void removeTask(@NotNull final UUID uniqueId) {
    this.tasks.remove(uniqueId);
  }

  /**
   * stops the ticking of the given uniqueId.
   *
   * @param uniqueId the uniqueId to stop.
   */
  public void stopTick(@NotNull final UUID uniqueId) {
    this.task(uniqueId).ifPresent(runnable -> {
      Bukkit.getScheduler().cancelTask(runnable.getTaskId());
      this.removeTask(uniqueId);
    });
  }

  /**
   * sets the given player of the ticking task to the given task.
   *
   * @param uniqueId the unique id to set.
   * @param task the task to set.
   */
  public void task(@NotNull final UUID uniqueId, @NotNull final BukkitTask task) {
    this.tasks.put(uniqueId, task);
  }

  /**
   * obtains the given uniqueId's task.
   *
   * @param uniqueId the uniqueId to obtain.
   *
   * @return a {@link BukkitRunnable} instance.
   */
  @NotNull
  public Optional<BukkitTask> task(@NotNull final UUID uniqueId) {
    return Optional.ofNullable(this.tasks.get(uniqueId));
  }

  /**
   * starts the ticking of the given player with the given page.
   *
   * @param uniqueId the unique id to start.
   * @param page the page to start.
   */
  public void tick(@NotNull final UUID uniqueId, @NotNull final Page page) {
    final var task = new Runnable() {
      @Override
      public void run() {
        SmartInventory.getHolder(uniqueId)
          .map(SmartHolder::context)
          .ifPresent(context -> page.accept(new PageTickEvent(context, SmartInventory.this.plugin)));
      }
    };
    final BukkitTask bukkitTask;
    if (page.async()) {
      bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, task, page.startDelay(), page.tick());
    } else {
      bukkitTask = Bukkit.getScheduler().runTaskTimer(this.plugin, task, page.startDelay(), page.tick());
    }
    this.task(uniqueId, bukkitTask);
  }

  /**
   * unregisters the given inventory openers.
   *
   * @param openers the openers to unregister.
   */
  public void unregisterOpeners(@NotNull final InventoryOpener... openers) {
    this.openers.removeAll(Arrays.asList(openers));
  }
}
