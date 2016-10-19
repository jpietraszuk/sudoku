package jp;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Sudoku {

    private final List<Cell> DATA = new ArrayList<>();

    Sudoku(String sudokuData) {
        int value;
        for (int i = 0; i < sudokuData.length(); ++i) {
            value = sudokuData.charAt(i) - '0';
            DATA.add(value == 0 ? new Cell(i) : new Cell(i, value) );
        }
    }

    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku("040000000001034620603000070000483507000050060000009040005000001800547396000021000");
        sudoku.print();
        sudoku.solve().print();
    }


    private Sudoku solve() {
        while (!solved()) {
            if (!finalValueReduction())
                pairsReduction();
        }
        return this;
    }

    private boolean solved() {
        return DATA.stream().allMatch(Cell::isFinal);
    }

    private boolean finalValueReduction() {
        boolean result = false;
        for (Cell cell : DATA) {
            if (cell.isFinal()) continue;

            result |= cell.removeAll(finalValues(cell, cellsInRow(cell.idx())));
            result |= cell.removeAll(finalValues(cell, cellsInCol(cell.idx())));
            result |= cell.removeAll(finalValues(cell, cellsInSqr(cell.idx())));
        }
        return result;
    }

    private boolean pairsReduction() {
        boolean result = false;
        for (int i = 0; i < 9; ++i) {
            result |= multiplePairs(cellsInRow(i * 9));
            result |= multiplePairs(cellsInCol(i));
            result |= multiplePairs(cellsInSqr(i * 9 + (i % 3) * 3));
        }
        return result;
    }

    boolean multiplePairs(List<Cell> cells) {
        // Select cells that have only 2 possible values
        List<Cell> pairs = cells.stream().filter(c -> c.possibleValues().size() == 2).collect(Collectors.toList());
        // Check if some pairs are repeated
        Set<Cell> noDuplicates = new HashSet<>(pairs);
        if (noDuplicates.size() == pairs.size()) return false;

        boolean result = false;
        List<Integer> pairIdx = new ArrayList<>();
        for (Cell pairCell : noDuplicates) {
            for (int i = 0; i < cells.size(); i++) {
                if (pairCell.equals(cells.get(i))) pairIdx.add(i);
            }
            if (pairIdx.size() == 1) continue;
            for (int j = 0; j < cells.size(); ++j) {
                Cell cell = cells.get(j);
                if (pairIdx.contains(j) || cell.isFinal()) continue;
                result |= cell.removeAll(pairCell.possibleValues());
            }
            pairIdx.clear();
        }
        return result;
    }

    private Collection<Integer> finalValues(Cell cell, Collection<Cell> data) {
        Predicate<Cell> notCell = c -> !(cell == c);
        return data.stream().filter(Cell::isFinal).filter(notCell).map(Cell::finalValue).collect(Collectors.toSet());
    }

    private List<Cell> cellsInSqr(int idx) {
        int startRow = ((idx / 9) / 3) * 3;
        int startCol = ((idx % 9) / 3) * 3;
        List<Cell> sqr = new ArrayList<>(9);
        for (int i = 0; i < 3; i++) {
            int startPos = startRow * 9 + i * 9 + startCol;
            for (int j = startPos; j < startPos + 3; j++) {
                sqr.add(DATA.get(j));
            }
        }
        return sqr;
    }

    private List<Cell> cellsInRow(int idx) {
        List<Cell> row = new ArrayList<>(9);
        int start = (idx / 9) * 9;
        for (int i = start; i < start + 9; i++) {
            row.add(DATA.get(i));
        }
        return row;
    }

    private List<Cell> cellsInCol(int idx) {
        List<Cell> col = new ArrayList<>(9);
        for (int i = idx % 9; i < 81; i+=9) {
            col.add(DATA.get(i));
        }
        return col;
    }

    private void print() {
        DATA.forEach(cell -> System.out.print(cell.finalOrZero() + (cell.endOfRow() ? "\n" : "")));
        System.out.println("");
    }
}
