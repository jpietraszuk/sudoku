package jp.utils;

import jp.sudoku.Cell;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class PrintUtils {

    private final static Integer[] VALUES_1 = new Integer[]{1, 2, 3};
    private final static Integer[] VALUES_2 = new Integer[]{4, 5, 6};
    private final static Integer[] VALUES_3 = new Integer[]{7, 8, 9};

    public static void printDebug(Collection<Cell> data, Function<Integer, List<Cell>> cellsInRow) {
        final Runnable printMinuses = () -> System.out.println("------------- ------------- -------------");
        final Runnable printEquals = () -> System.out.println("=========================================");

        printEquals.run();
        for (int i = 0; i < 9; i++) {
            List<Cell> row = cellsInRow.apply(i * 9);
            printRow(row, VALUES_1);
            printRow(row, VALUES_2);
            printRow(row, VALUES_3);
            if (((i+1) % 3) == 0) printEquals.run(); else printMinuses.run();
        }
        System.out.println("\n");
    }

    private static void printRow(List<Cell> row, Integer[]  VALUES) {
        System.out.print("|");
        int i = 0;
        for(Cell c : row) {
            Collection<Integer> pv = c.possibleValues();
            for(Integer v : VALUES) System.out.print(pv.contains(v) ? v.toString() : " ");
            if ((++i % 3) == 0 && i != 9) System.out.print("| |"); else System.out.print("|");
        }
        System.out.println("");
    }

}
