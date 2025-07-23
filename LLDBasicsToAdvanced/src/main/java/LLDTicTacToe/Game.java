package LLDTicTacToe;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Game {
    static class Pair{
        int x;
        int y;
        Pair(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    Board gameBoard;
    Deque<Player> players;
    Game(int size){
       gameBoard = new Board(size);
       Player p1 = new Player("Player1", PieceType.X);
       Player p2 = new Player("Player2", PieceType.O);
       players = new LinkedList<>();
       players.add(p1);
       players.add(p2);
    }
    String startGame(){
        boolean winnerDeclared = false;
        while(!winnerDeclared){
            Player playerTurn = players.removeFirst();
            gameBoard.printBoard();
            List<Pair> freeCell = gameBoard.getFreeCells();
            if(freeCell.size() == 0){
                System.out.println("No Result");
                break;
            }

            System.out.print("Player:" + playerTurn.name + " Enter row,column: ");
            Scanner inputScanner = new Scanner(System.in);
            String s = inputScanner.nextLine();
            String[] values = s.split(",");
            int inputRow = Integer.valueOf(values[0]);
            int inputColumn = Integer.valueOf(values[1]);

            PlayingPiece pp = playerTurn.type == PieceType.X ? new PlayingPieceX(PieceType.X) : new PlayingPieceO(PieceType.O);
            if(gameBoard.addPiece(inputRow , inputColumn, pp)){
                if(checkWinner(inputRow, inputColumn, playerTurn.type)){
                    return playerTurn.name;
                }
                players.addLast(playerTurn);
            }else{
                players.addFirst(playerTurn);
                continue;
            }

        }
        return "tie";

    }

    public boolean checkWinner(int row, int column, PieceType type) {
        boolean rowMatch = true;
        boolean columnMatch = true;
        boolean diagonalMatch = true;
        boolean antiDiagonalMatch = true;

        //need to check in row
        for(int i=0;i<gameBoard.size;i++) {

            if(gameBoard.board[row][i] == null || gameBoard.board[row][i].type != type) {
                rowMatch = false;
            }
        }

        //need to check in column
        for(int i=0;i<gameBoard.size;i++) {

            if(gameBoard.board[i][column] == null || gameBoard.board[i][column].type != type) {
                columnMatch = false;
            }
        }

        //need to check diagonals
        for(int i=0, j=0; i<gameBoard.size;i++,j++) {
            if (gameBoard.board[i][j] == null || gameBoard.board[i][j].type != type) {
                diagonalMatch = false;
            }
        }

        //need to check anti-diagonals
        for(int i=0, j=gameBoard.size-1; i<gameBoard.size;i++,j--) {
            if (gameBoard.board[i][j] == null || gameBoard.board[i][j].type != type) {
                antiDiagonalMatch = false;
            }
        }

        return rowMatch || columnMatch || diagonalMatch || antiDiagonalMatch;
    }

}



