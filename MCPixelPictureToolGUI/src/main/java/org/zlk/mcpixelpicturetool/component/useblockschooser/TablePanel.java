package org.zlk.mcpixelpicturetool.component.useblockschooser;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;

public class TablePanel<E extends Component> extends JPanel implements Iterable<E> {
    private final HashMap<Point, E> componentTable = new HashMap<>();
    private int count = 0;

    public TablePanel() {
        super(new GridLayout());
    }

    public TablePanel(int rows, int cols) {
        super(new GridLayout(rows, cols));
    }

    public E getComponent(int columnIndex, int rowIndex) {
        return componentTable.get(new Point(columnIndex, rowIndex));
    }

    public int getCount() {
        return count;
    }

    private GridLayout getTableLayout() {
        return (GridLayout) getLayout();
    }

    private int getColumnIndex(int count) {
        return (count - 1) % getTableLayout().getColumns();
    }

    private int getRowIndex(int count) {
        return (count - 1) / getTableLayout().getColumns();
    }

    @SuppressWarnings("unchecked")
    public E add(Component comp) {
        try {
            E v = (E) comp;
            count++;
            componentTable.put(new Point(getColumnIndex(count), getRowIndex(count)), v);
            return (E) super.add(comp);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Types are not supported: " + comp.getClass().getName());
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new TablePanelIterator();
    }

    public class TablePanelIterator implements Iterator<E> {
        private int iteratorCount;

        @Override
        public boolean hasNext() {
            return iteratorCount < getCount();
        }

        @Override
        public E next() {
            iteratorCount++;
            return getComponent(getColumnIndex(iteratorCount), getRowIndex(iteratorCount));
        }
    }
}
