package tictactoe;

public class Board {
    int size;
    Piece board[][];
    Board(int size){
        this.size = size;
        board = new Piece[size][size];
    }

    void addPiece(int x, int y, Piece piece){
        this.board[x][y] = piece;
    }

    boolean canAddPiece(int x, int y){
       if(x<0 || y<0 || x>=size || y>= size)
        return false;
       if(this.board[x][y] != null)return false;
       return true;
    }

    boolean checkIFWinner(int x, int y, Piece piece){
        boolean rowMatch = true;
        boolean columnMatch = true;
        boolean diagonalMatch = true;
        boolean antiDiagonalMatch = true;

        //need to check in row
        for(int i=0;i<size;i++) {

            if(board[x][i] == null || !board[x][i].c.equals(piece.c)) {
                rowMatch = false;
            }
        }

        //need to check in column
        for(int i=0;i<size;i++) {

            if(board[i][y] == null || !board[i][y].c.equals(piece.c)) {
                columnMatch = false;
            }
        }

        //need to check diagonals
        for(int i=0, j=0; i<size;i++,j++) {
            if (board[i][j] == null || !board[i][j].c.equals(piece.c)) {
                diagonalMatch = false;
            }
        }

        //need to check anti-diagonals
        for(int i=0, j=size-1; i<size;i++,j--) {
            if (board[i][j] == null || !board[i][j].c.equals(piece.c)) {
                antiDiagonalMatch = false;
            }
        }

        return rowMatch || columnMatch || diagonalMatch || antiDiagonalMatch;
    }

    void displayBoard(){
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                if(board[i][j] != null)
                    System.out.print(board[i][j].toString()+" ");
                else
                    System.out.print("__ ");
            }
            System.out.println();
        }
    }
}
