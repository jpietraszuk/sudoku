package jp;

import jp.sudoku.Cell;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CellTest {

    @Test
    public void finalValueCheck() {
        Cell cell = new Cell(0, 1);
        assertThat(cell.finalValue(), equalTo(1));
    }

    @Test
    public void finalVAlueCheckAfterReduction() {
        Cell cell = new Cell(0, 0);
        cell.removeAll(Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9));
        assertThat(cell.finalValue(), equalTo(1));
    }

    @Test
    public void settingFinalValue() {
        Cell cell = new Cell(0, 0);
        cell.finalValue(4);
        assertThat(cell.finalValue(), equalTo(4));
    }
}