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
//        Sudoku sudoku = new Sudoku("172549683645873219389261745496327851813456972257198436964715328731682594528934167");
//        Sudoku sudoku = new Sudoku("040000000001034620603000070000483507000050060000009040005000001800547396000021000");
        Sudoku sudoku = new Sudoku("000000680000073009309000045490000000803050902000000036960000308700680000028000000");
        sudoku.solve().debugPrint();
    }


    private Sudoku solve() {
        while (!solved()) {
            debugPrint();
            if (finalValueReduction()) {
                System.out.println("finalValueReduction"); continue;
            }
            if (pairsReduction()) {
                System.out.println("pairsReduction"); continue;
            }
            if (uniqueCandidate()) {
                System.out.println("uniqueCandidate"); continue;
            }
            if (doublesReduction()) {
                System.out.println("doublesReduction"); continue;
            }

            print();
            throw new RuntimeException("Can't solve it");
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
            Set<Integer> col = containingPV.stream().map(Cell::col).collect(Collectors.toSet());
            if (col.size() == 1) {
                List<Cell> colCells = cellsInCol(containingPV.iterator().next().idx()).stream().filter(cell -> !(containingPV.contains(cell) || cell.isFinal())).collect(Collectors.toList());
                for (Cell c : colCells) {
                    result |= c.removeAll(toBeDeleted);
                }
            }
            Set<Integer> row = containingPV.stream().map(Cell::row).collect(Collectors.toSet());
            if (row.size() == 1) {
                List<Cell> rowCells = cellsInRow(containingPV.iterator().next().idx()).stream().filter(cell -> !(containingPV.contains(cell) || cell.isFinal())).collect(Collectors.toList());
                for (Cell c : rowCells) {
                    result |= c.removeAll(toBeDeleted);
                }
            }

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

    private boolean uniqueCandidate() {
        for (int i = 0; i < 81; ++i) {
            Cell cell = DATA.get(i);
            if (cell.isFinal()) continue;
            if (onlyPosibleValue(cell, cellsInSqr(cell.idx()))) {
                isValid();
                return true;
            }
        }
        return false;
    }

    private boolean onlyPosibleValue(Cell cell, Collection<Cell> data) {
        Predicate<Cell> finalOrCell = c -> c.isFinal() || cell == c;
        for (Integer pv : cell.possibleValues() ){
            boolean contains = data.stream().filter(finalOrCell.negate()).anyMatch(c -> c.possibleValues().contains(pv));
            if (!contains) {
                print();
                return cell.finalValue(pv);
            }
        }

        return false;
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

    private void print() {
        DATA.forEach(cell -> System.out.print(cell.finalOrZero() + (cell.endOfRow() ? "\n" : "")));
        System.out.println("");
    }

    private final Integer[] VALUES_1 = new Integer[]{1, 2, 3};
    private final Integer[] VALUES_2 = new Integer[]{4, 5, 6};
    private final Integer[] VALUES_3 = new Integer[]{7, 8, 9};

    private void debugPrint() {
        printEquals();
        for (int i = 0; i < 9; i++) {
            List<Cell> row = cellsInRow(i * 9);
            printRow(row, VALUES_1);
            printRow(row, VALUES_2);
            printRow(row, VALUES_3);
            if (((i+1) % 3) == 0) printEquals(); else printMinuses();
        }
        System.out.println("\n");
    }

    private void printRow(List<Cell> row, Integer[]  VALUES) {
        System.out.print("|");
        int i = 0;
        for(Cell c : row) {
            Collection<Integer> pv = c.possibleValues();
            for(Integer v : VALUES) System.out.print(pv.contains(v) ? v.toString() : " ");
            if ((++i % 3) == 0 && i != 9) System.out.print("| |"); else System.out.print("|");
        }
        System.out.println("");
    }

    private void printMinuses() {
        System.out.println("------------- ------------- -------------");
    }

    private void printEquals() {
        System.out.println("=========================================");
    }
}
