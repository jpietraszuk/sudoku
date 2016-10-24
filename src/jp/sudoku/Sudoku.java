package jp.sudoku;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Sudoku {

    private final List<Cell> DATA = new ArrayList<>();

    public Sudoku(String sudokuData) {
        for (int i = 0; i < sudokuData.length(); ++i) {
            DATA.add(new Cell(i, sudokuData.charAt(i) - '0'));
        }
    }

    public String solve() {
        while (!isSolved()) {
            if (finalValueReduction()) continue;
            if (pairsReduction()) continue;
            if (uniqueCandidate()) continue;
            if (doublesReduction()) continue;

            throw new RuntimeException("Can't solve it, try with simpler puzzle");
        }
        return printSolution(DATA);
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

    private Collection<Integer> finalValues(Cell cell, Collection<Cell> data) {
        Predicate<Cell> notCell = c -> !(cell == c);
        return data.stream().filter(Cell::isFinal).filter(notCell).map(Cell::finalValue).collect(Collectors.toSet());
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

    private boolean multiplePairs(List<Cell> cells) {
        // Select cells that have only 2 possible values
        List<Cell> pairs = cells.stream().filter(c -> c.possibleValues().size() == 2).collect(Collectors.toList());
        // Check if some pairs are repeated
        Set<Cell> noDuplicates = new HashSet<>(pairs);
        if (noDuplicates.size() == pairs.size()) return false;

        boolean result = false;
        List<Integer> pairIdx = new ArrayList<>();
        for (Cell pairCell : noDuplicates) {
            pairIdx.clear();
            for (int i = 0; i < cells.size(); i++) {
                if (pairCell.equals(cells.get(i)))
                    pairIdx.add(i);
            }
            if (pairIdx.size() == 1) continue;
            for (int j = 0; j < cells.size(); ++j) {
                Cell cell = cells.get(j);
                if (pairIdx.contains(j) || cell.isFinal()) continue;
                result |= cell.removeAll(pairCell.possibleValues());
            }
        }
        return result;
    }

    private boolean uniqueCandidate() {
        for (int i = 0; i < 81; ++i) {
            Cell cell = DATA.get(i);
            if (cell.isFinal()) continue;
            if (!onlyPossibleValue(cell, cellsInSqr(cell.idx()))) continue;

            return true;
        }
        return false;
    }

    private boolean onlyPossibleValue(Cell cell, Collection<Cell> data) {
        Predicate<Cell> notFinalNorCell = c -> !(c.isFinal() || cell == c);
        for (Integer pv : cell.possibleValues() ){
            boolean contains = data.stream().filter(notFinalNorCell).anyMatch(c -> c.possibleValues().contains(pv));
            if (!contains) {
                return cell.finalValue(pv);
            }
        }
        return false;
    }

    private boolean doublesReduction() {
        boolean result = false;
        for (int i = 0; i < 9 && !result; ++i) {
            result = doubleValues(cellsInSqr(i * 9 + (i % 3) * 3));
        }
        return result;
    }

    private boolean doubleValues(List<Cell> sqr) {
        Set<Integer> allPV = new TreeSet<>();
        for (Cell c : sqr) {
            if (c.isFinal()) continue;
            allPV.addAll(c.possibleValues());
        }
        boolean result = false;
        for (Integer pv : allPV) {
            List<Cell> containingPV = sqr.stream().filter(cell -> !cell.isFinal() && cell.possibleValues().contains(pv)).collect(Collectors.toList());
            if (containingPV.size() != 2) continue;
            Set<Integer> toBeDeleted = Collections.singleton(pv);
            Function<Cell, Integer> toCol = c -> c.idx() % 9;
            Set<Integer> col = containingPV.stream().map(toCol).collect(Collectors.toSet());
            if (col.size() == 1) {
                List<Cell> colCells = cellsInCol(containingPV.iterator().next().idx()).stream().filter(cell -> !(containingPV.contains(cell) || cell.isFinal())).collect(Collectors.toList());
                for (Cell c : colCells) {
                    result |= c.removeAll(toBeDeleted);
                }
            }
            Function<Cell, Integer> toRow = c -> c.idx() / 9;
            Set<Integer> row = containingPV.stream().map(toRow).collect(Collectors.toSet());
            if (row.size() == 1) {
                List<Cell> rowCells = cellsInRow(containingPV.iterator().next().idx()).stream().filter(cell -> !(containingPV.contains(cell) || cell.isFinal())).collect(Collectors.toList());
                for (Cell c : rowCells) {
                    result |= c.removeAll(toBeDeleted);
                }
            }

        }
        return result;
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

    private boolean isSolved() {
        return DATA.stream().allMatch(Cell::isFinal) && isValid();
    }

    private boolean isValid() {
        for (int i = 0; i < 9; i++) {
            check(cellsInRow(i*9).stream().filter(Cell::isFinal).collect(Collectors.toList()));
            check(cellsInCol(i).stream().filter(Cell::isFinal).collect(Collectors.toList()));
            check(cellsInSqr(i * 9 + (i % 3) * 3).stream().filter(Cell::isFinal).collect(Collectors.toList()));
        }
        return true;
    }

    private void check(Collection<Cell> data) {
        HashSet<Cell> s = new HashSet<>();
        s.addAll(data);
        if (data.size() != s.size())
            throw new RuntimeException("Invalid structure");
    }

    private String printSolution(Collection<Cell> sudoku) {
        StringBuilder sb = new StringBuilder();
        sudoku.forEach(sb::append);
        return sb.toString();
    }

}
