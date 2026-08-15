package Questions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import LLDTicTacToe.Game;

/*
Functional Requirements :
User should be able to move their own pieces
Each user should get their turn alternatively 
Board should be 8*8 one 
2 color choices on a board , should be able to support more colour with a max 2 


Non-Functional Requirements :
Thread safety 
CheckMate
Stalemate
extensible to change the stratergy of each piece 

Core entities:

GameController 
Board
Cell
Pieces
MoveStratergy - Each peice should have a move stratergy
Player


*/

enum Color{
    WHITE,
    BLACK;
}

enum PieceType{
    KING,QUEEN,ROOK,BISHOP,PAWN,KNIGHT;
}

interface MoveStratergy{
    boolean isValid(int currentRow, int currentCol, int newRow, int newCol);
}

class KnightMoveStratergy implements MoveStratergy{

    @Override
    public boolean isValid(int currentRow, int currentCol,
                           int newRow, int newCol) {

        int rowDiff = Math.abs(newRow - currentRow);
        int colDiff = Math.abs(newCol - currentCol);

        return (rowDiff == 2 && colDiff == 1)
                || (rowDiff == 1 && colDiff == 2);
    }
}

class KingMoveStratergy implements MoveStratergy{

    @Override
    public boolean isValid(int currentRow, int currentCol,
                           int newRow, int newCol) {

        int rowDiff = Math.abs(newRow - currentRow);
        int colDiff = Math.abs(newCol - currentCol);

        return rowDiff <= 1
                && colDiff <= 1
                && (rowDiff + colDiff > 0);
    }

    
    
}

class PawnMoveStratergy implements MoveStratergy{


    private final Color color;

    PawnMoveStratergy(Color color) {
        this.color = color;
    }

    @Override
    public boolean isValid(int currentRow, int currentCol,
                           int newRow, int newCol) {

        int direction = color == Color.WHITE ? 1 : -1;

        int rowDiff = newRow - currentRow;
        int colDiff = Math.abs(newCol - currentCol);

        // Normal one-square move
        if (rowDiff == direction && colDiff == 0) {
            return true;
        }

        // Initial two-square move
        if (rowDiff == 2 * direction
                && colDiff == 0
                && ((color == Color.WHITE && currentRow == 1)
                    || (color == Color.BLACK && currentRow == 6))) {
            return true;
        }

        // Diagonal capture
        if (rowDiff == direction && colDiff == 1) {
            return true;
        }

        return false;
    }
    
}

class QueenMoveStratergy implements MoveStratergy{

    @Override
    public boolean isValid(int currentRow, int currentCol,
                           int newRow, int newCol) {

        int rowDiff = Math.abs(newRow - currentRow);
        int colDiff = Math.abs(newCol - currentCol);

        boolean horizontalOrVertical =
                currentRow == newRow || currentCol == newCol;

        boolean diagonal =
                rowDiff == colDiff;

        return (horizontalOrVertical || diagonal)
                && (rowDiff + colDiff > 0);
    }
    
}

class BishopMoveStratergy implements MoveStratergy{

    @Override
    public boolean isValid(int currentRow, int currentCol,
                           int newRow, int newCol) {

        int rowDiff = Math.abs(newRow - currentRow);
        int colDiff = Math.abs(newCol - currentCol);

        return rowDiff == colDiff && rowDiff > 0;
    }
    
}

class RookMoveStratergy implements MoveStratergy{

    @Override
    public boolean isValid(int currentRow, int currentCol,
                           int newRow, int newCol) {

        return currentRow == newRow
                || currentCol == newCol;
    }
    
}

abstract class Piece{
    int col;
    int row;
    PieceType pieceType;
    MoveStratergy moveStratergy;
    Color color;
    Piece(int row, int col, PieceType pieceType,MoveStratergy moveStratergy, Color color){
        this.row = row;
        this.col = col;
        this.pieceType = pieceType;
        this.moveStratergy = moveStratergy;
        this.color = color;
    }

    abstract void movePiece(int x, int y);

abstract boolean isValidMove(int x, int y);
}
class KnightPiece extends Piece{
    KnightPiece(int row, int col, Color color) {
        super(row, col, PieceType.KNIGHT, new KnightMoveStratergy(), color);
        //TODO Auto-generated constructor stub
    }
    
    @Override
    void movePiece(int x, int y) {
        this.row = x;
        this.col = y;
    }

    @Override
    boolean isValidMove(int x, int y) {
        return moveStratergy.isValid(this.row, this.col, x, y);
    }
    
}

class QueenPiece extends Piece{
    QueenPiece(int row, int col, Color color) {
        super(row, col, PieceType.QUEEN, new QueenMoveStratergy(), color);
        //TODO Auto-generated constructor stub
    }
    
    @Override
    void movePiece(int x, int y) {
        this.row = x;
        this.col = y;
    }

    @Override
    boolean isValidMove(int x, int y) {
        return moveStratergy.isValid(this.row, this.col, x, y);
    }
    
}

class PawnPiece extends Piece{
    PawnPiece(int row, int col, Color color) {
        super(row, col, PieceType.PAWN, new PawnMoveStratergy(color), color);
        //TODO Auto-generated constructor stub
    }
    
    @Override
    void movePiece(int x, int y) {
        this.row = x;
        this.col = y;
    }

    @Override
    boolean isValidMove(int x, int y) {
        return moveStratergy.isValid(this.row, this.col, x, y);
    }
    
} 

class KingPiece extends Piece{
    KingPiece(int row, int col, Color color) {
        super(row, col, PieceType.KING, new KingMoveStratergy(), color);
        //TODO Auto-generated constructor stub
    }
    
    @Override
    void movePiece(int x, int y) {
        this.row = x;
        this.col = y;
    }

    @Override
    boolean isValidMove(int x, int y) {
        return moveStratergy.isValid(this.row, this.col, x, y);
    }
    
} 

class RookPiece extends Piece{
    RookPiece(int row, int col, Color color) {
        super(row, col, PieceType.ROOK, new RookMoveStratergy(), color);
        //TODO Auto-generated constructor stub
    }
    
    @Override
    void movePiece(int x, int y) {
        this.row = x;
        this.col = y;
    }

    @Override
    boolean isValidMove(int x, int y) {
        return moveStratergy.isValid(this.row, this.col, x, y);
    }
    
} 

class BishopPiece extends Piece{
    BishopPiece(int row, int col, Color color) {
        super(row, col, PieceType.BISHOP, new BishopMoveStratergy(), color);
        //TODO Auto-generated constructor stub
    }
    
    @Override
    void movePiece(int x, int y) {
        this.row = x;
        this.col = y;
    }

    @Override
    boolean isValidMove(int x, int y) {
        return moveStratergy.isValid(this.row, this.col, x, y);
    }
    
} 

class Cell{
    Piece piece;
    int row;
    int col;
    Cell(int x, int y){
        this.row = x;
        this.col = y;
    }
    void addPiece(Piece piece){
        this.piece = piece;
    }

void removePiece(){
        this.piece = null;
    }

    Piece getPiece(){
        return this.piece;
    }
}
class Player{
    Color color;
    String name;
    Player(Color color, String name){
        this.color = color;
        this.name = name;
    }
}
class Board{
    Cell board[][];
    Board(){
        board = new Cell[8][8];
        this.init();
    }
    private void init() {
        // 1. Initialize all cells
        for(int row = 0; row < 8; row++){
            for(int col = 0; col < 8; col++){
                board[row][col] = new Cell(row, col);
            }
        }
        //add pieces to start game
        // White back row
        addPiece(0, 0, new RookPiece(0, 0, Color.WHITE));
        addPiece(0, 1, new KnightPiece(0, 1, Color.WHITE));
        addPiece(0, 2, new BishopPiece(0, 2, Color.WHITE));
        addPiece(0, 3, new QueenPiece(0, 3, Color.WHITE));
        addPiece(0, 4, new KingPiece(0, 4, Color.WHITE));
        addPiece(0, 5, new BishopPiece(0, 5, Color.WHITE));
        addPiece(0, 6, new KnightPiece(0, 6, Color.WHITE));
        addPiece(0, 7, new RookPiece(0, 7, Color.WHITE));
         // White pawns
         for(int col = 0; col < 8; col++){
            addPiece(1, col, new PawnPiece(1, col, Color.WHITE));
        }

        // 3. Add BLACK pieces

        // Black back row
        addPiece(7, 0, new RookPiece(7, 0, Color.BLACK));
        addPiece(7, 1, new KnightPiece(7, 1, Color.BLACK));
        addPiece(7, 2, new BishopPiece(7, 2, Color.BLACK));
        addPiece(7, 3, new QueenPiece(7, 3, Color.BLACK));
        addPiece(7, 4, new KingPiece(7, 4, Color.BLACK));
        addPiece(7, 5, new BishopPiece(7, 5, Color.BLACK));
        addPiece(7, 6, new KnightPiece(7, 6, Color.BLACK));
        addPiece(7, 7, new RookPiece(7, 7, Color.BLACK));

        // Black pawns
        for(int col = 0; col < 8; col++){
            addPiece(6, col, new PawnPiece(6, col, Color.BLACK));
        }
    }

    void addPiece(int x, int y, Piece piece){
        board[x][y].addPiece(piece);
    }

    void removePiece(int x, int y){
        board[x][y].removePiece();
    }

    Piece getPiece(int x, int y){
        return board[x][y].getPiece();
    }

    void printBoard() {

        System.out.println();
        System.out.println("    0   1   2   3   4   5   6   7");
        System.out.println("  +---+---+---+---+---+---+---+---+");

        for (int row = 0; row < 8; row++) {

            System.out.print(row + " |");

            for (int col = 0; col < 8; col++) {

                Piece piece = board[row][col].getPiece();

                if (piece == null) {
                    System.out.print("   |");
                } else {
                    System.out.print(" " + getPieceSymbol(piece) + " |");
                }
            }

            System.out.println();
            System.out.println("  +---+---+---+---+---+---+---+---+");
        }

        System.out.println();
    }

    private String getPieceSymbol(Piece piece) {

        String symbol;

        switch (piece.pieceType) {
            case KING:
                symbol = "K";
                break;
            case QUEEN:
                symbol = "Q";
                break;
            case ROOK:
                symbol = "R";
                break;
            case BISHOP:
                symbol = "B";
                break;
            case KNIGHT:
                symbol = "N";
                break;
            case PAWN:
                symbol = "P";
                break;
            default:
                symbol = "?";
        }

        return piece.color == Color.WHITE
                ? symbol
                : symbol.toLowerCase();
    }

}

class GameController{
    Board board;
    List<Player> players;
    boolean running = false;
    GameController(Player p1, Player p2){
        board = new Board();
        players = new ArrayList<>(2);
        players.add(p2);
        players.add(p1);
    }
    void startGame() {

        running = true;
        board.printBoard();
        while (running) {

            // 1. Get current player
            Player currentPlayer = getPlayerTurn();

            System.out.println(
                    "Current player: " + currentPlayer.name
                            + " [" + currentPlayer.color + "]"
            );

            // 2. Get source position from user
            System.out.println("Enter source row and column:");

            Scanner scanner = new Scanner(System.in);

            int sourceX = scanner.nextInt();
            int sourceY = scanner.nextInt();

            // 3. Get piece
            Piece piece = board.getPiece(sourceX, sourceY);

            // No piece at source
            if (piece == null) {
                System.out.println("No piece at selected position");
                continue;
            }

            // 4. Check ownership
            if (piece.color != currentPlayer.color) {
                System.out.println("You cannot move opponent's piece");
                continue;
            }

            // 5. Get destination
            System.out.println("Enter destination row and column:");

            int newX = scanner.nextInt();
            int newY = scanner.nextInt();

            // 6. Check destination is inside board
            if (!isValidPosition(newX, newY)) {
                System.out.println("Invalid destination");
                continue;
            }

            // 7. Check piece movement strategy
            if (!piece.isValidMove(newX, newY)) {
                System.out.println("Invalid move for " + piece.pieceType);
                continue;
            }

            // 8. Check destination
            Piece destinationPiece =
                    board.getPiece(newX, newY);

            // Same color piece exists
            if (destinationPiece != null
                    && destinationPiece.color == currentPlayer.color) {

                System.out.println(
                        "Cannot move. Destination occupied by your piece"
                );

                continue;
            }

            // 9. Check whether path is clear
            if (!isPathClear(piece, newX, newY)) {
                System.out.println("Path is blocked");
                continue;
            }

            // 10. Capture opponent piece
            if (destinationPiece != null) {

                System.out.println(
                        "Captured " + destinationPiece.pieceType
                );

                board.removePiece(newX, newY);
            }

            // 11. Remove piece from old position
            int oldX = piece.row;
            int oldY = piece.col;

            board.removePiece(oldX, oldY);

            // 12. Move piece internally
            piece.movePiece(newX, newY);

            // 13. Add piece to new position
            board.addPiece(newX, newY, piece);

            // 14. Check checkmate
            if (isCheckMate()) {
                System.out.println("CHECKMATE!");
                running = false;
                continue;
            }

            // 15. Check stalemate
            if (isStaleMate()) {
                System.out.println("STALEMATE!");
                running = false;
                continue;
            }

            //print board
            board.printBoard();
            // 16. Switch turn
            switchTurn();
        }
    }

    

    private boolean isValidPosition(int x, int y) {

        return x >= 0
                && x < 8
                && y >= 0
                && y < 8;
    }

    private boolean isPathClear(
        Piece piece,
        int newX,
        int newY) {

        if (piece.pieceType == PieceType.KNIGHT
                || piece.pieceType == PieceType.KING) {
            return true;
        }

        int rowDirection =
                Integer.compare(newX, piece.row);

        int colDirection =
                Integer.compare(newY, piece.col);

        int currentRow = piece.row + rowDirection;
        int currentCol = piece.col + colDirection;

        while (currentRow != newX
                || currentCol != newY) {

            if (board.getPiece(currentRow, currentCol) != null) {
                return false;
            }

            currentRow += rowDirection;
            currentCol += colDirection;
        }

        return true;
    }

    private boolean isCheckMate() {

        Player currentPlayer = getPlayerTurn();
    
        // Player must first be in check
        if (!isKingInCheck(currentPlayer.color)) {
            return false;
        }
    
        // If player has no legal move, it is checkmate
        return !hasAnyLegalMove(currentPlayer.color);
    }
    
    private boolean isStaleMate() {
    
        Player currentPlayer = getPlayerTurn();
    
        // Stalemate means NOT in check
        if (isKingInCheck(currentPlayer.color)) {
            return false;
        }
    
        // But player must have no legal moves
        return !hasAnyLegalMove(currentPlayer.color);
    }

    private void switchTurn() {

        Player currentPlayer = players.remove(0);
        players.add(currentPlayer);
    }

    Player getPlayerTurn() {
        return players.get(0);
    }

    private boolean isLegalMove(
        Piece piece,
        int newRow,
        int newCol) {

    if (!isValidPosition(newRow, newCol)) {
        return false;
    }

    Piece destination = board.getPiece(newRow, newCol);

    // Cannot capture own piece
    if (destination != null
            && destination.color == piece.color) {
        return false;
    }

    // Piece movement rule
    if (!piece.isValidMove(newRow, newCol)) {
        return false;
    }

    // Path must be clear
    if (!isPathClear(piece, newRow, newCol)) {
        return false;
    }

    // Simulate move
    int oldRow = piece.row;
    int oldCol = piece.col;

    board.removePiece(oldRow, oldCol);

    if (destination != null) {
        board.removePiece(newRow, newCol);
    }

    piece.movePiece(newRow, newCol);
    board.addPiece(newRow, newCol, piece);

    // Is our King still in check?
    boolean kingInCheck = isKingInCheck(piece.color);

    // Undo move
    board.removePiece(newRow, newCol);

    piece.movePiece(oldRow, oldCol);

    board.addPiece(oldRow, oldCol, piece);

    if (destination != null) {
        board.addPiece(newRow, newCol, destination);
    }

    return !kingInCheck;
}

    private boolean hasAnyLegalMove(Color color) {

        for (int row = 0; row < 8; row++) {
    
            for (int col = 0; col < 8; col++) {
    
                Piece piece = board.getPiece(row, col);
    
                if (piece == null || piece.color != color) {
                    continue;
                }
    
                // Try every possible destination
                for (int newRow = 0; newRow < 8; newRow++) {
    
                    for (int newCol = 0; newCol < 8; newCol++) {
    
                        if (isLegalMove(piece, newRow, newCol)) {
                            return true;
                        }
                    }
                }
            }
        }
    
        return false;
    }

    private boolean isKingInCheck(Color color) {

        Piece king = findKing(color);
    
        if (king == null) {
            return false;
        }
    
        for (int row = 0; row < 8; row++) {
    
            for (int col = 0; col < 8; col++) {
    
                Piece piece = board.getPiece(row, col);
    
                if (piece == null || piece.color == color) {
                    continue;
                }
    
                // Can opponent piece geometrically reach the King?
                if (!piece.isValidMove(king.row, king.col)) {
                    continue;
                }
    
                // Knight can jump over pieces
                if (piece.pieceType == PieceType.KNIGHT) {
                    return true;
                }
    
                // Check whether path is clear
                if (isPathClear(piece, king.row, king.col)) {
                    return true;
                }
            }
        }
    
        return false;
    }

    private Piece findKing(Color color) {

        for (int row = 0; row < 8; row++) {
    
            for (int col = 0; col < 8; col++) {
    
                Piece piece = board.getPiece(row, col);
    
                if (piece != null
                        && piece.color == color
                        && piece.pieceType == PieceType.KING) {
    
                    return piece;
                }
            }
        }
    
        return null;
    }
}
  
public class ChessGame {
    public static void main(String[] args) {
        Player p1 = new Player(Color.WHITE, "Umesh");
        Player p2 = new Player(Color.BLACK, "Lipsa");
        new GameController(p1, p2).startGame();;
    }
}
