package jp;

import jp.sudoku.Sudoku;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SudokuTest {

    @Test
    public void solvedPuzzle() {
        Sudoku puzzle = new Sudoku("248675139751934628693218475926483517184752963537169842475396281812547396369821754");
        assertThat(puzzle.solve(), equalTo("248675139751934628693218475926483517184752963537169842475396281812547396369821754"));
    }

    @Test
    public void simplePuzzle() {
        Sudoku puzzle = new Sudoku("040000000001034620603000070000483507000050060000009040005000001800547396000021000");
        assertThat(puzzle.solve(), equalTo("248675139751934628693218475926483517184752963537169842475396281812547396369821754"));
    }

    @Test
    public void hardPuzzle() {
        Sudoku puzzle = new Sudoku("000000680000073009309000045490000000803050902000000036960000308700680000028000000");
        assertThat(puzzle.solve(), equalTo("172549683645873219389261745496327851813456972257198436964715328731682594528934167"));
    }
}