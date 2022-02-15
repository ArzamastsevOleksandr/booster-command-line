package cliclient.command.handler;

import java.util.List;

public abstract class Paginator<T> {

    int pagination, count, endInclusive;
    int startInclusive = 1;

    Paginator(int pagination, int count) {
        this.pagination = pagination;
        this.count = count;
        this.endInclusive = Math.min(this.startInclusive + this.pagination - 1, this.count);
    }

    boolean isInRange() {
        return startInclusive <= count;
    }

    boolean isEmpty() {
        return count == 0;
    }

    void updateRange() {
        startInclusive = endInclusive + 1;
        endInclusive = Math.min(endInclusive + pagination, count);
    }

    int limit() {
        int limit = endInclusive - startInclusive + 1;
        return Math.max(limit, 0);
    }

    String counter() {
        return endInclusive + "/" + count;
    }

    abstract List<T> nextBatch();

    List<T> nextBatchAndUpdateRange() {
        if (isEmpty()) {
            return List.of();
        }
        List<T> nextBatch = nextBatch();
        updateRange();
        return nextBatch;
    }

}
