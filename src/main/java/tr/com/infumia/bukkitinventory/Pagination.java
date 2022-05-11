package tr.com.infumia.bukkitinventory;

import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * a class that represents pagination.
 */
@Accessors(fluent = true)
public final class Pagination {

  /**
   * the current page.
   */
  @Getter
  @Setter
  private int currentPage;

  /**
   * the icons.
   */
  @NotNull
  private Icon[] icons = new Icon[0];

  /**
   * the icons per page.
   */
  @Setter
  @Getter
  private int iconsPerPage = 5;

  /**
   * adds all the current page icons to the given iterator.
   *
   * @param iterator the iterator.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Pagination addToIterator(@NotNull final SlotIterator iterator) {
    for (final var item : this.getPageIcons()) {
      iterator.next().set(item);
      if (iterator.ended()) {
        break;
      }
    }
    return this;
  }

  /**
   * Sets the current page to the first page.
   * <p>
   * this is equivalent to: {@code page(0)}.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Pagination first() {
    this.currentPage = 0;
    return this;
  }

  /**
   * gets the icons of the current page.
   * <p>
   * this returns an array of the size of the icons per page.
   *
   * @return the current page icons.
   */
  @NotNull
  public Icon[] getPageIcons() {
    return Arrays.copyOfRange(this.icons,
      this.currentPage * this.iconsPerPage,
      (this.currentPage + 1) * this.iconsPerPage);
  }

  /**
   * checks if the current page is the first page.
   * <p>
   * this is equivalent to: {@code page == 0}.
   *
   * @return {@code true} if this page is the first page.
   */
  public boolean isFirst() {
    return this.currentPage == 0;
  }

  /**
   * checks if the current page is the last page.
   * <p>
   * this is equivalent to: {@code page == iconsCount / iconsPerPage}.
   *
   * @return {@code true} if this page is the last page.
   */
  public boolean isLast() {
    return this.currentPage >= (int) Math.ceil((double) this.icons.length / (double) this.iconsPerPage) - 1;
  }

  /**
   * sets the current page to the last page.
   * <p>
   * this is equivalent to: {@code page(iconsCount / iconsPerPage)}.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Pagination last() {
    return this.currentPage(this.getPageIcons().length / this.iconsPerPage);
  }

  /**
   * sets the current page to the next page,
   * if the current page is already the last page, this do nothing.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Pagination next() {
    if (!this.isLast()) {
      this.currentPage++;
    }
    return this;
  }

  /**
   * sets the current page to the previous page,
   * if the current page is already the first page, this do nothing.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Pagination previous() {
    if (!this.isFirst()) {
      this.currentPage--;
    }
    return this;
  }

  /**
   * sets all the icons for this Pagination.
   *
   * @param icons the icons.
   *
   * @return {@code this}, for chained calls.
   */
  @NotNull
  public Pagination setIcons(@NotNull final Icon... icons) {
    this.icons = icons.clone();
    return this;
  }
}
