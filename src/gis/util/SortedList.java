package gis.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A generic, duplicate-free sorted list.
 * 
 * @param <T>
 */
public class SortedList<T> implements Iterable<T> {

  protected final Comparator<T> comparator;

  protected Object[] elements;

  protected int size = 0;

  public SortedList(final Comparator<T> comparator) {
    this(comparator, 50);
  }

  public SortedList(final Comparator<T> comparator, final int initialCapacity) {
    this.comparator = comparator;
    elements = new Object[initialCapacity];
  }

  @SuppressWarnings("unchecked")
  public boolean add(final T e) {
    final int index = insertionIndexOf(e);
    if(index < size) {
      if(comparator.compare(e, (T) elements[index]) != 0) {
        makeRoom(1);
        for(int i = size; i > index; --i) {
          elements[i] = elements[i - 1];
        }
      }
    }
    elements[index] = e;
    ++size;
    return true;
  }

  public boolean addAll(final List<? extends T> list) {
    final int reqSize = Math.max(size + list.size(), elements.length);
    final Object[] newElements = new Object[reqSize];
    int i = 0;// index of elements
    int j = 0;// index of (ordered) list
    int k = 0;// index of newElements
    while(i < size && j < list.size()) {
      @SuppressWarnings("unchecked")
      final int comp = comparator.compare((T) elements[i], list.get(j));
      if(comp < 0) {
        newElements[k++] = elements[i++];
      } else if(comp > 0) {
        newElements[k++] = list.get(j++);
      } else {
        // duplicate element
        newElements[k++] = list.get(j++);
        ++i;
      }
    }
    while(i < size) {
      newElements[k++] = elements[i++];
    }
    while(j < list.size()) {
      newElements[k++] = list.get(j++);
    }
    elements = newElements;
    size = k;
    return true;
  }

  public void clear() {
    for(int i = 0; i < size; ++i) {
      elements[i] = null;
    }
    size = 0;
  }

  @SuppressWarnings("unchecked")
  public T get(final int index) {
    return (T) elements[index];
  }

  @SuppressWarnings("unchecked")
  public int indexOf(final T e) {
    final int index = insertionIndexOf(e);
    if(index < size && comparator.compare(e, (T) elements[index]) == 0) return index;
    return -1;
  }

  /**
   * Returns the index in {@link #elements}, where <i>e</i> should be inserted.
   * If <i>e</i> is already stored in the array, then its position is returned.
   * If the returned value is the index position of another element, then it and
   * all other elements with a higher index need to be shifted to the right,
   * prior to insertion of <i>e</i>.
   * 
   * @param e element
   * @return where to insert <i>e</i>
   */
  @SuppressWarnings("unchecked")
  protected int insertionIndexOf(final T e) {
    // use binary search
    int iMin = 0;
    int iMax = size;
    int iMean;
    while(iMax > iMin) {
      iMean = (iMin + iMax) / 2;
      // elements[iMean] too small
      if(comparator.compare((T) elements[iMean], e) < 0) {
        // check right half
        iMin = iMean + 1;
      } else {
        iMax = iMean;
      }
    }
    return iMin;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public Iterator<T> iterator() {
    return new SortedListIterator(this);
  }

  @SuppressWarnings("unchecked")
  public boolean remove(final T e) {
    final int index = insertionIndexOf(e);
    if(index < size && comparator.compare(e, (T) elements[index]) == 0) {
      shiftAllLeft(index + 1, 1);
      --size;
      return true;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  public T remove(final int index) {
    if(index >= 0 && index < size) {
      final T e = (T) elements[index];
      shiftAllLeft(index + 1, 1);
      --size;
      return e;
    }
    return null;
  }

  protected void shiftAllLeft(final int index, final int shift) {
    for(int i = index; i < size; ++i) {
      elements[i - shift] = elements[i];
    }
  }

  public int size() {
    return size;
  }

  public Object[] toArray() {
    final Object[] array = new Object[size];
    for(int i = 0; i < array.length; ++i) {
      array[i] = elements[i];
    }
    return array;
  }

  @SuppressWarnings({ "unchecked", "hiding"})
  public <T> T[] toArray(final T[] array) {
    for(int i = 0; i < array.length; ++i) {
      array[i] = (T) elements[i];
    }
    return array;
  }

  private void makeRoom(final int additionalSize) {
    final int reqSize = size + additionalSize;
    if(reqSize > elements.length) {
      // grow array
      final int capacity = Math.max(2 * elements.length, reqSize);
      final Object[] newElements = new Object[capacity];
      for(int i = 0; i < size; ++i) {
        newElements[i] = elements[i];
      }
      elements = newElements;
    }
  }

  public static void main(final String[] args) {
    final Comparator<Integer> comparator = new Comparator<Integer>() {

      @Override
      public int compare(final Integer i1, final Integer i2) {
        if(i1 < i2)
        return -1;
        if(i1 > i2)
        return 1;
        return 0;
      }

    };
    final SortedList<Integer> list = new SortedList<Integer>(comparator);
    list.elements[0] = new Integer(0);
    list.elements[1] = new Integer(2);
    list.elements[2] = new Integer(4);
    list.elements[3] = new Integer(6);
    list.elements[4] = new Integer(8);
    list.size = 5;

    final List<Integer> li = new ArrayList<Integer>();
    li.add(-1);
    li.add(1);
    li.add(3);
    li.add(4);
    li.add(7);
    li.add(10);

    list.addAll(li);

    System.out.println(list);

    list.add(-3);
    list.add(15);
    list.add(5);
    System.out.println(list);

    list.remove(Integer.valueOf(-3));
    System.out.println(list);
    list.remove(Integer.valueOf(15));
    System.out.println(list);
    list.remove(Integer.valueOf(2));
    System.out.println(list);
    System.out.println();

    list.remove(0);
    System.out.println(list);
    list.remove(2);
    System.out.println(list);
    list.remove(7);
    System.out.println(list);

    final Iterator<Integer> it = list.iterator();
    while(it.hasNext()) {
      System.out.print(it.next() + " ");
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("[");
    for(int i = 0; i < size - 1; ++i) {
      sb.append(elements[i]);
      sb.append(", ");
    }
    if(size > 0) {
      sb.append(elements[size - 1]);
    }
    sb.append("]");
    return sb.toString();
  }

  public class SortedListIterator implements Iterator<T> {

    SortedList<T> list;
    int index = 0;

    SortedListIterator(final SortedList<T> list) {
      this.list = list;
    }

    @Override
    public boolean hasNext() {
      return index < size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T next() {
      if(index >= list.elements.length) throw new NoSuchElementException();
      return (T) list.elements[index++];
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

  }
}
