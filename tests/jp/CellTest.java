package jp;

import org.junit.Test;

public class CellTest {

    @Test
    public void check() {
        for (int i = 0; i < 81; i++) {
            Cell c = new Cell(i, 1);
            System.out.println("Cell[" + c.idx() + "] belongs to row: " + c.row() + " and col: " + c.col());
        }
    }
}