package net.sourceforge.ganttproject.chart.mouse;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class TwoWayIterator<E> implements Iterator<E> {
    private final ListIterator<E> iter;
    private final boolean backwards;

    public TwoWayIterator(List<E> elems, boolean backwards) {
        this.backwards = backwards;

        if (backwards)
            this.iter = elems.listIterator(elems.size());
        else
            this.iter = elems.listIterator();
    }

    @Override
    public boolean hasNext() {
        if (backwards)
            return iter.hasPrevious();
        else
            return iter.hasNext();
    }

    @Override
    public E next() {
        if (backwards)
            return iter.previous();
        else
            return iter.next();
    }

}
