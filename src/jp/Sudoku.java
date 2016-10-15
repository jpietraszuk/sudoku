package jp;

import java.util.*;

public class Sudoku {

    private static Set<Integer> FULL_CELL = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
    private final List<Set<Integer>> DATA = new ArrayList<>();

    Sudoku(String sudokuData) {
        char c;
        for (int i = 0; i < 9 *  9; i++) {
            c = sudokuData.charAt(i);
            DATA.add(c == '0' ? new HashSet<>(FULL_CELL) : Collections.singleton(c - '0'));
        }
    }

    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku("040000000001034620603000070000483507000050060000009040005000001800547396000021000");
        sudoku.print();
        sudoku.solve().print();
    }


    private Sudoku solve() {
        while (!solved()) reduction().print();
        return this;
    }

    private boolean solved() {
        for (Set<Integer> cell : DATA)
            if (cell.size() > 1) return false;
        return true;
    }

    private Sudoku reduction() {
        int i = -1;
        for (Set<Integer> cell : DATA) {
            ++i;
            if (cell.size() == 1) continue;

            cell.removeAll(row(i));
            cell.removeAll(col(i));
            cell.removeAll(sqr(i));

            System.nanoTime();
        }
        return this;
    }

    private Collection<Integer> sqr(int idx) {
        Set<Integer> numbers = new HashSet<>();
        int startRow = ((idx / 9) / 3) * 3;
        int startCol = ((idx % 9) / 3) * 3;

        for (int i = 0; i < 3; i++) {
            int startPos = startRow * 9 + i * 9 + startCol;
            for (int j = startPos; j < startPos + 3; j++) {
                Set<Integer> cell = DATA.get(j);
                if (cell.size() == 1 && j != idx)
                    numbers.addAll(cell);
            }
        }
        return numbers;
    }

    private Collection<Integer> row(int idx) {
        Set<Integer> numbers = new HashSet<>();
        int start = (idx / 9) * 9;
        Set<Integer> cell;
        for (int i = start; i < start + 9; i++) {
            cell = DATA.get(i);
            if (cell.size() == 1 && i != idx)
                numbers.addAll(cell);
        }
        return numbers;
    }

    private Collection<Integer> col(int idx) {
        Set<Integer> numbers = new HashSet<>();
        Set<Integer> cell;
        for (int i = idx % 9; i < 81; i+=9) {
            cell = DATA.get(i);
            if (cell.size() == 1 && i != idx)
                numbers.addAll(cell);
        }
        return numbers;
    }

    private void print() {
        int i = 0;
        for (Set<Integer> cell : DATA) {
            System.out.print("" + (cell.size() == 1 ? cell.iterator().next() : 0));
            if ((++i % 9) == 0) System.out.println("");
        }
        System.out.println("");
    }
}
