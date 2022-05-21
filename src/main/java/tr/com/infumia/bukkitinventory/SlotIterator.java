package tr.com.infumia.bukkitinventory;

import com.google.common.base.Preconditions;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.bukkitinventory.util.Pattern;
import tr.com.infumia.bukkitinventory.util.SlotPos;

/**
 * a class that represents slot iterators.
 */
@Accessors(fluent = true)
public final class SlotIterator {

  /**
   * the blacklisted.
   */
  private final Set<SlotPos> blacklisted = new HashSet<>();

  /**
   * the contents.
   */
  @NotNull
  private final InventoryContext contents;

  /**
   * the start column.
   */
  private final int startColumn;

  /**
   * the start row.
   */
  private final int startRow;

  /**
   * the type.
   */
  @NotNull
  private final SlotIterator.Type type;

  /**
   * the allow override.
   */
  @Getter
  @Setter
  private boolean allowOverride = true;

  /**
   * the blacklist pattern.
   */
  @Nullable
  private Pattern<Boolean> blacklistPattern;

  /**
   * the blacklist pattern column offset.
   */
  private int blacklistPatternColumnOffset;

  /**
   * the blacklist pattern row offset.
   */
  private int blacklistPatternRowOffset;

  /**
   * the column.
   */
  @Getter
  @Setter
  private int column;

  /**
   * the end column.
   */
  private int endColumn;

  /**
   * the end row.
   */
  private int endRow;

  /**
   * the pattern.
   */
  @Nullable
  private Pattern<Boolean> pattern;

  /**
   * the pattern column offset.
   */
  private int patternColumnOffset;

  /**
   * the pattern row offset.
   */
  private int patternRowOffset;

  /**
   * the row.
   */
  @Getter
  @Setter
  private int row;

  /**
   * the started.
   */
  @Getter
  private boolean started;

  /**
   * ctor.
   *
   * @param contents the contents.
   * @param type the type.
   */
  public SlotIterator(@NotNull final InventoryContext contents, @NotNull final SlotIterator.Type type) {
    this(contents, type, 0, 0);
  }

  /**
   * ctor.
   *
   * @param contents the contents.
   * @param type the type.
   * @param startRow the start row.
   * @param startColumn the start column.
   */
  public SlotIterator(@NotNull final InventoryContext contents, @NotNull final SlotIterator.Type type,
                      final int startRow, final int startColumn) {
    this.contents = contents;
    this.type = type;
    this.endRow = this.contents.page().row() - 1;
    this.endColumn = this.contents.page().column() - 1;
    this.startRow = startRow;
    this.row = startRow;
    this.startColumn = startColumn;
    this.column = startColumn;
  }

  /**
   * blacklists the given slot index.
   * <p>
   * blacklisting a slot will make the iterator skip the given slot and directly go to the next
   * un-blacklisted slot.
   *
   * @param index the index to blacklist.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public SlotIterator blacklist(final int index) {
    final int count = this.contents.page().column();
    this.blacklisted.add(SlotPos.of(index / count, index % count));
    return this;
  }

  /**
   * blacklists the given slot position.
   * <p>
   * blacklisting a slot will make the iterator
   * skip the given slot and directly go to the next
   * un-blacklisted slot.
   *
   * @param row the row of the slot to blacklist.
   * @param column the column of the slot to blacklist.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public SlotIterator blacklist(final int row, final int column) {
    this.blacklisted.add(SlotPos.of(row, column));
    return this;
  }

  /**
   * blacklists the given slot position.
   * <p>
   * Blacklisting a slot will make the iterator
   * skip the given slot and directly go to the next
   * un-blacklisted slot.
   *
   * @param slotPos the slot to blacklist.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public SlotIterator blacklist(@NotNull final SlotPos slotPos) {
    return this.blacklist(slotPos.getRow(), slotPos.getColumn());
  }

  /**
   * This method has the inverse effect of {@link #withPattern(Pattern, int, int)},
   * where the other method would only allow the iterator to go,
   * this method prohibits this slots to iterate over.
   *
   * @param pattern the pattern where the slot iterator cannot iterate.
   * @param rowOffset the row offset from the top left corner.
   * @param columnOffset the column offset from the top left corner.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public SlotIterator blacklistPattern(@NotNull final Pattern<Boolean> pattern, final int rowOffset,
                                       final int columnOffset) {
    this.blacklistPatternRowOffset = rowOffset;
    this.blacklistPatternColumnOffset = columnOffset;
    if (pattern.getDefaultValue().isEmpty()) {
      pattern.setDefault(false);
    }
    this.blacklistPattern = pattern;
    return this;
  }

  /**
   * this method has the inverse effect of {@link #withPattern(Pattern)}, where the other method would only allow the
   * iterator to go, this method prohibits this slots to iterate over.
   *
   * @param pattern the pattern where the slot iterator cannot iterate.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public SlotIterator blacklistPattern(@NotNull final Pattern<Boolean> pattern) {
    return this.blacklistPattern(pattern, 0, 0);
  }

  /**
   * sets the slot where the iterator should end.
   * <p>
   * if the row is a negative value, it is set to the maximum row count.
   * <p>
   * if the column is a negative value, it is set to maximum column count.
   *
   * @param row the row where the iterator should end.
   * @param column the column where the iterator should end.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public SlotIterator endPosition(final int row, final int column) {
    var tempRow = row;
    var tempColumn = column;
    if (row < 0) {
      tempRow = this.contents.page().row() - 1;
    }
    if (tempColumn < 0) {
      tempColumn = this.contents.page().column() - 1;
    }
    Preconditions.checkArgument(tempRow * tempColumn >= this.startRow * this.startColumn,
      "The end position needs to be after the start of the slot iterator");
    this.endRow = tempRow;
    this.endColumn = tempColumn;
    return this;
  }

  /**
   * sets the slot where the iterator should end.
   * <p>
   * if the row of the SlotPos is a negative value, it is set to the maximum row count.
   * <p>
   * if the column of the SlotPos is a negative value, it is set to maximum column count.
   *
   * @param endPosition the slot where the iterator should end.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public SlotIterator endPosition(@NotNull final SlotPos endPosition) {
    return this.endPosition(endPosition.getRow(), endPosition.getColumn());
  }

  /**
   * checks if this iterator has been ended.
   * <p>
   * an iterator is not ended until it has reached the last slot of the inventory.
   *
   * @return {@code true} if this iterator has been ended.
   */
  public boolean ended() {
    return this.row == this.endRow
      && this.column == this.endColumn;
  }

  /**
   * gets the icon at the current position in the inventory.
   *
   * @return the icon at the current position.
   */
  @NotNull
  public Optional<Icon> get() {
    return this.contents.get(this.row, this.column);
  }

  /**
   * moves the cursor to the next position inside the inventory.
   * <p>
   * this has no effect if the cursor is already at the last position of the inventory.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public SlotIterator next() {
    if (this.ended()) {
      this.started = true;
      return this;
    }
    do {
      if (this.started) {
        if (this.type == Type.HORIZONTAL) {
          ++this.column;
          this.column %= this.contents.page().column();
          if (this.column == 0) {
            this.row++;
          }
        } else if (this.type == Type.VERTICAL) {
          ++this.row;
          this.row %= this.contents.page().row();
          if (this.row == 0) {
            this.column++;
          }
        }
      } else {
        this.started = true;
      }
    }
    while (!this.canPlace() && !this.ended());
    return this;
  }

  /**
   * moves the cursor to the previous position inside the inventory.
   * <p>
   * this has no effect if the cursor is already  at the first position of the inventory.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public SlotIterator previous() {
    if (this.row == 0 && this.column == 0) {
      this.started = true;
      return this;
    }
    do {
      if (this.started) {
        if (this.type == Type.HORIZONTAL) {
          this.column--;
          if (this.column == 0) {
            this.column = this.contents.page().column() - 1;
            this.row--;
          }
        } else if (this.type == Type.VERTICAL) {
          this.row--;
          if (this.row == 0) {
            this.row = this.contents.page().row() - 1;
            this.column--;
          }
        }
      } else {
        this.started = true;
      }
    }
    while (!this.canPlace() && (this.row != 0 || this.column != 0));
    return this;
  }

  /**
   * resets iterator to its original position specified while creation.
   * <p>
   * when the iterator gets reset to its original position, {@code started} gets set back to {@code false}.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public SlotIterator reset() {
    this.started = false;
    return this.row(this.startRow)
      .column(this.startColumn);
  }

  /**
   * replaces the icon at the current position in the inventory by the given icon.
   *
   * @param icon the new icon.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public SlotIterator set(@NotNull final Icon icon) {
    if (this.canPlace()) {
      this.contents.set(this.row, this.column, icon);
    }
    return this;
  }

  /**
   * setting a pattern using this method will use it as a guideline where the slot iterator can set icons or not.
   * if the pattern doesn't fill the whole inventory, the slot iterator is limited to the space the pattern provides.
   * if the pattern has the {@code wrapAround} flag set, then the iterator can iterate over the entire inventory,
   * even if the pattern would not fill it by itself.
   * <p>
   * the offset defines the top-left corner of the pattern. if the {@code wrapAround} flag is set, then the entire
   * pattern will be just shifted by the given amount.
   * <p>
   * if the provided pattern has no default value set, this method will set it to {@code false}.
   * <br><br>
   * if you pass {@code null} into the {@code pattern} parameter, this functionality will be disabled and
   * the iterator will continue to work as normal.
   *
   * @param pattern the pattern to use as a guideline.
   * @param rowOffset the row offset from the top left corner.
   * @param columnOffset the column offset from the top left corner.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public SlotIterator withPattern(@NotNull final Pattern<Boolean> pattern, final int rowOffset,
                                  final int columnOffset) {
    this.patternRowOffset = rowOffset;
    this.patternColumnOffset = columnOffset;
    if (pattern.getDefaultValue().isEmpty()) {
      pattern.setDefault(false);
    }
    this.pattern = pattern;
    return this;
  }

  /**
   * setting a pattern using this method will use it as a guideline where the slot iterator can set icons or not.
   * if the pattern doesn't fill the whole inventory, the slot iterator is limited to the space the pattern provides.
   * if the pattern has the {@code wrapAround} flag set, then the iterator can iterate over the entire inventory,
   * even if the pattern would not fill it by itself.
   * <p>
   * if the provided pattern has no default value set, this method will set it to {@code false}.
   * <p>
   * if you pass {@code null} into the {@code pattern} parameter, this functionality will be disabled and
   * the iterator will continue to work as normal.
   *
   * @param pattern the pattern to use as a guideline.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public SlotIterator withPattern(@NotNull final Pattern<Boolean> pattern) {
    return this.withPattern(pattern, 0, 0);
  }

  /**
   * checks if the item can place.
   *
   * @return {@code true} if the item can place the current location.
   */
  private boolean canPlace() {
    final var patternAllows = new AtomicBoolean(true);
    Optional.ofNullable(this.pattern).ifPresent(booleanPattern ->
      patternAllows.set(this.checkPattern(booleanPattern, this.patternRowOffset, this.patternColumnOffset)));
    final var blacklistPatternAllows = new AtomicBoolean(true);
    Optional.ofNullable(this.blacklistPattern).ifPresent(booleanPattern ->
      blacklistPatternAllows.set(!this.checkPattern(booleanPattern, this.blacklistPatternRowOffset, this.blacklistPatternColumnOffset)));
    return !this.blacklisted.contains(SlotPos.of(this.row, this.column)) &&
      (this.allowOverride || this.get().isEmpty()) &&
      patternAllows.get() &&
      blacklistPatternAllows.get();
  }

  /**
   * checks the pattern.
   *
   * @param pattern the pattern to check.
   * @param rowOffset the raw offset to check.
   * @param columnOffset the column offset to check.
   *
   * @return {@code true} if the checking was successful.
   */
  private boolean checkPattern(@NotNull final Pattern<Boolean> pattern, final int rowOffset, final int columnOffset) {
    final var object = pattern.getObject(this.row() - rowOffset, this.column() - columnOffset);
    if (pattern.isWrapAround()) {
      return object.orElse(false);
    }
    return this.row() >= rowOffset && this.column() >= columnOffset &&
      this.row() < pattern.getRowCount() + rowOffset &&
      this.column() < pattern.getColumnCount() + columnOffset &&
      object.orElse(false);
  }

  /**
   * an enum class that contains slot iterator types.
   */
  public enum Type {

    /**
     * iterates horizontally from the left to the right of the inventory, and
     * jump to the next line when the last column is reached.
     */
    HORIZONTAL,
    /**
     * iterates vertically from the up to the down of the inventory, and
     * jump to the next column when the last line is reached.
     */
    VERTICAL
  }
}
