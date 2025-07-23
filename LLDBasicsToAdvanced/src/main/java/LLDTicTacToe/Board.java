package LLDTicTacToe;

import java.util.ArrayList;
import java.util.List;

public class Board {
    PlayingPiece[][] board;
    int size;
    Board(int size){
        this.size = size;
        board = new PlayingPiece[size][size];
    }

boolean addPiece(int row, int col, PlayingPiece playingPiece){
        if(board[row][col] != null ){
            System.out.println("Invalid row and column, enter again");
            return  false;
        }
        board[row][col] = playingPiece;
        return true;
    }

    public List<Game.Pair> getFreeCells() {
        List<Game.Pair> freeCells = new ArrayList<>();
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                if(board[i][j]==null){
                    freeCells.add(new Game.Pair(i,j));
                }

            }
        }
        return freeCells;
    }

    public void printBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != null) {
                    System.out.print(board[i][j].type.name() + "   ");
                } else {
                    System.out.print("    ");

                }
                System.out.print(" | ");
            }
            System.out.println();

        }
    }

}
