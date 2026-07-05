package tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    Board board;
    List<Player> playerList;
    Game(int size, int numOfPayers){
        board = new Board(size);
        playerList = new ArrayList<>();
        playerList.add(new Player("PlayerX", PieceType.X));
        playerList.add(new Player("PlayerO", PieceType.O));
    }
    public void start(){
        boolean isWinnerDeclared = false;
        while(!isWinnerDeclared){
            Player p = playerList.remove(0);
            PieceType pt = p.pieceType;

            System.out.print("Player:" + p.name + " Enter row,column: ");
            Scanner inputScanner = new Scanner(System.in);
            String s = inputScanner.nextLine();
            String[] values = s.split(",");
            int inputRow = Integer.valueOf(values[0]);
            int inputColumn = Integer.valueOf(values[1]);

            if(!board.canAddPiece(inputRow, inputColumn))
                break;
            Piece piece = new Piece(pt.toString());
            board.addPiece(inputRow, inputColumn, piece);
            board.displayBoard();
            if(board.checkIFWinner(inputRow, inputColumn, piece)){
                System.out.println("Winner is : "+p.name );
                isWinnerDeclared = true;
            }
            playerList.add(p);
        }
    }
}
