package com.chess;

import com.chess.engine.board.Board;
import com.chess.gui.Table;

public class JChess {
    public static void main(String[]args){
        Board board=Board.createStandartBoard();
        System.out.print(board);
        Table.get().show();
    }
}
