package jp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SudokuTest {

    private Sudoku sudoku;
    private final List<Cell> row = new ArrayList<>();

    @Before
    public void setUp() {
        int i = 0;
        row.add(new Cell(i++, new ArrayList<>(Arrays.asList(1,3,5,7))));
        row.add(new Cell(i++, new ArrayList<>(Arrays.asList(3,5,6))));
        row.add(new Cell(i++, new ArrayList<>(Arrays.asList(6,7))));
        row.add(new Cell(i++, new ArrayList<>(Arrays.asList(1,6,7))));
        row.add(new Cell(i++, new ArrayList<>(Arrays.asList(6,7))));
        row.add(new Cell(i++, 9));
        row.add(new Cell(i++, 8));
        row.add(new Cell(i++, 4));
        row.add(new Cell(i++, 2));

        sudoku = new Sudoku("040000000001034620603000070000483507000050060000009040005000001800547396000021000");
    }

    @Test
    public void check() {
        System.out.println("Before:");
        sudoku.multiplePairs(row);
        System.out.println("After:");
        print(row);
    }

    private void print(Collection<Cell> data) {
        for (Cell cell : data) {
            System.out.println("Cell["+cell.idx()+"]: "+cell.possibleValues());
        }
    }
}