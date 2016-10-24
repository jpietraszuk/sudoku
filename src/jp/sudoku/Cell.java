package jp.sudoku;

import java.util.*;

public class Cell {
    private static List<Integer> FULL_CELL = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

    private final int idx;
    private final List<Integer> possibleValues;

    public Cell(int idx, int value) {
        this.idx = idx;
        this.possibleValues = new ArrayList<>(value == 0 ? FULL_CELL : Collections.singletonList(value));
    }

    public int idx() {
        return idx;
    }

    public boolean isFinal() {
        return possibleValues.size() == 1;
    }

    public int finalValue() {
        return isFinal() ? possibleValues.get(0) : 0;
    }

    public boolean finalValue(int value) {
        if (isFinal() && value != finalValue()) throw new RuntimeException("This cell already has final value");
        if (!isFinal() && !possibleValues.contains(value)) throw new RuntimeException("Final value is not one of possible values");

        possibleValues.clear();
        return possibleValues.add(value);
    }

    public boolean removeAll(Collection<Integer> integers) {
        if (isFinal() && integers.contains(finalValue())) throw new RuntimeException("Can't remove final value");

        return !isFinal() && possibleValues.removeAll(integers);
    }

    public Collection<Integer> possibleValues() {
        return Collections.unmodifiableList(possibleValues);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null) && possibleValues.equals(((Cell)o).possibleValues);
    }

    @Override
    public int hashCode() {
        return possibleValues.hashCode();
    }

    @Override
    public String toString() {
        return Integer.toString(finalValue());
    }
}
