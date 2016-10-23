package jp;

import java.util.*;

public class Cell {
    private static List<Integer> FULL_CELL = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

    private final int idx;
    private final List<Integer> possibleValues;

    public Cell(int idx) {
        this(idx, new ArrayList<>(FULL_CELL));
    }

    public Cell(int idx, int value) {
        this(idx, Collections.singletonList(value));
    }

    public Cell(int idx, List<Integer> possibleValues) {
        this.idx = idx;
        this.possibleValues = possibleValues;
    }

    public boolean isFinal() {
        return possibleValues.size() == 1;
    }

    public Integer finalValue() {
        if (!isFinal()) throw new RuntimeException("Cell is not final yet");
        return possibleValues.get(0);
    }

    public int finalOrZero() {
        return isFinal() ? possibleValues.get(0) : 0;
    }

    public boolean endOfRow() {
        return ((idx+1) % 9) == 0;
    }

    public int idx() {
        return idx;
    }

    public int row() {
        return idx / 9;
    }

    public int col() {
        return idx % 9;
    }
    public boolean removeAll(Collection<Integer> integers) {
        return !isFinal() && possibleValues.removeAll(integers);
    }

    public boolean finalValue(int value) {
        if (isFinal()) return false;
        possibleValues.clear();
        return possibleValues.add(value);
    }

    public Collection<Integer> possibleValues() {
        return Collections.unmodifiableList(possibleValues);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        return possibleValues.equals(cell.possibleValues);
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 31 * result + possibleValues.hashCode();
        return result;
    }
}
