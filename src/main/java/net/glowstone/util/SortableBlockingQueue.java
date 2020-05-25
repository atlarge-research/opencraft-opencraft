package net.glowstone.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The sortable blocking queue is a blocking queue implementation that allows for its elements to be sorted.
 *
 * @param <Element> the type of elements that can be stored in the queue.
 */
public final class SortableBlockingQueue<Element> implements BlockingQueue<Element> {

    private final Comparator<Element> comparator;
    private final List<Element> elements;
    private final Lock lock;
    private final Condition notEmpty;

    /**
     * Create a sortable blocking queue.
     *
     * @param comparator the comparator that should be used for sorting elements.
     */
    public SortableBlockingQueue(Comparator<Element> comparator) {
        this.comparator = comparator.reversed();
        elements = new ArrayList<>();
        lock = new ReentrantLock();
        notEmpty = this.lock.newCondition();
    }


    /**
     * Sort the elements in the queue.
     */
    public void sort() {
        lock.lock();
        try {
            elements.sort(comparator);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Update the queue by adding and removing elements and then sorting them.
     *
     * @param additions the elements that should be added to the queue.
     * @param removals the elements that should be removed from the queue.
     */
    public void update(Collection<Element> additions, Collection<Element> removals) {
        lock.lock();
        try {
            removeAll(removals);
            addAll(additions);
            sort();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return elements.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.lock();
        try {
            return elements.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(Object object) {
        lock.lock();
        try {
            return elements.contains(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        lock.lock();
        try {
            return elements.containsAll(collection);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean add(@NotNull Element element) {
        lock.lock();
        try {
            elements.add(element);
            if (elements.size() == 1) {
                notEmpty.signalAll();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Element> collection) {
        lock.lock();
        try {
            boolean shouldSignal = elements.isEmpty();
            boolean changed = elements.addAll(collection);
            shouldSignal &= !elements.isEmpty();
            if (shouldSignal) {
                notEmpty.signalAll();
            }
            return changed;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(@NotNull Element element) {
        return add(element);
    }

    @Override
    public boolean offer(Element element, long timeout, @NotNull TimeUnit unit) {
        return add(element);
    }

    @Override
    public void put(@NotNull Element element) {
        add(element);
    }

    @Override
    public Element element() {
        lock.lock();
        try {
            if (elements.isEmpty()) {
                throw new NoSuchElementException();
            }
            int index = elements.size() - 1;
            return elements.get(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Element peek() {
        lock.lock();
        try {
            if (elements.isEmpty()) {
                return null;
            }
            int index = elements.size() - 1;
            return elements.get(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Element poll() {
        lock.lock();
        try {
            if (elements.isEmpty()) {
                return null;
            }
            int index = elements.size() - 1;
            return elements.remove(index);
        } finally {
            lock.unlock();
        }
    }

    @Nullable
    @Override
    public Element poll(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        lock.lock();
        try {
            while (elements.isEmpty()) {
                notEmpty.await();
                // TODO: Add timeout
            }
            int index = elements.size() - 1;
            return elements.remove(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Element remove() {
        lock.lock();
        try {
            if (elements.isEmpty()) {
                throw new NoSuchElementException();
            }
            int index = elements.size() - 1;
            return elements.remove(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(Object object) {
        lock.lock();
        try {
            return elements.remove(object);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        lock.lock();
        try {
            return elements.removeAll(collection);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeIf(Predicate<? super Element> predicate) {
        lock.lock();
        try {
            return elements.removeIf(predicate);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        lock.lock();
        try {
            return elements.retainAll(collection);
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    @Override
    public Element take() throws InterruptedException {
        lock.lock();
        try {
            while (elements.isEmpty()) {
                notEmpty.await();
            }
            int index = elements.size() - 1;
            return elements.remove(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        lock.lock();
        try {
            elements.clear();
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    @Override
    public Object[] toArray() {
        lock.lock();
        try {
            return elements.toArray();
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] ts) {
        lock.lock();
        try {
            //noinspection SuspiciousToArrayCall
            return elements.toArray(ts);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int drainTo(@NotNull Collection<? super Element> collection) {
        lock.lock();
        try {
            int count = elements.size();
            collection.addAll(elements);
            elements.clear();
            return count;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int drainTo(@NotNull Collection<? super Element> collection, int max) {
        lock.lock();
        try {
            int count = 0;
            for (int index = elements.size() - 1; index >= 0 && count < max; index--) {
                Element element = elements.remove(index);
                collection.add(element);
                count++;
            }
            return count;
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    @Override
    public Iterator<Element> iterator() {
        return elements.iterator();
    }

    @Override
    public void forEach(Consumer<? super Element> consumer) {
        lock.lock();
        try {
            elements.forEach(consumer);
        } finally {
            lock.unlock();
        }
    }
}
