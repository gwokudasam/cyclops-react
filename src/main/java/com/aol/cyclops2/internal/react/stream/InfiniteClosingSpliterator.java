package com.aol.cyclops2.internal.react.stream;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import cyclops.async.Queue;
import cyclops.async.Queue.ClosedQueueException;
import com.aol.cyclops2.react.async.subscription.Continueable;

public class InfiniteClosingSpliterator<T> implements Spliterator<T> {
    private long estimate;
    final Supplier<T> s;
    private final Continueable subscription;
    private final Queue queue;

    protected InfiniteClosingSpliterator(final long estimate, final Supplier<T> s, final Continueable subscription, final Queue queue) {
        this.estimate = estimate;
        this.s = s;
        this.subscription = subscription;
        this.queue = queue;
        this.subscription.addQueue(queue);
    }

    public InfiniteClosingSpliterator(final long estimate, final Supplier<T> s, final Continueable subscription) {
        this.estimate = estimate;
        this.s = s;
        this.subscription = subscription;
        this.queue = null;
    }

    @Override
    public long estimateSize() {
        return estimate;
    }

    @Override
    public int characteristics() {
        return IMMUTABLE;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super T> action) {
        Objects.requireNonNull(action);

        try {

            action.accept(s.get());
            if (subscription.closed())
                return false;
            return true;
        } catch (final ClosedQueueException e) {
            return false;
        } catch (final Exception e) {

            return false;
        }

    }

    @Override
    public Spliterator<T> trySplit() {

        return new InfiniteClosingSpliterator(
                                              estimate >>>= 1, s, subscription, queue);
    }

}
