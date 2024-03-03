package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.chess.gui.Table.*;

public class GameHistoryPanel extends JPanel{

    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANEK_DIMENSION=new Dimension(100,400);

    GameHistoryPanel(){
        this.setLayout(new BorderLayout());
        this.model=new DataModel();
        final JTable table=new JTable(model);
        table.setRowHeight(15);
        this.scrollPane=new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEK_DIMENSION);
        this.add(scrollPane,BorderLayout.CENTER);
        this.setVisible(true);
    }
    void redo(final Board board, final MoveLog moveHistory){
        int currentRow=0;
        this.model.clear();
        for(final Move move:moveHistory.getMoves()){
            final String moveText=move.toString();
            if(move.getMovedPiece().getPieceAlliance().isWhite()){
                this.model.setValueAt(moveText,currentRow,0);
            } else if (move.getMovedPiece().getPieceAlliance().isBlack()) {
                this.model.setValueAt(moveText,currentRow,1);
                currentRow++;
            }
        }
        if(moveHistory.getMoves().size()>0){
            final Move lastMove=moveHistory.getMoves().get(moveHistory.size()-1);
            final String moveText=lastMove.toString();
            if (lastMove.getMovedPiece().getPieceAlliance().isWhite()){
                this.model.setValueAt(moveText+calculateCheckAndCheckMathHash(board),currentRow,0);
            } else if (lastMove.getMovedPiece().getPieceAlliance().isBlack()) {
                this.model.setValueAt(moveText+calculateCheckAndCheckMathHash(board),currentRow-1,1);
            }
        }
        final JScrollBar vertical =scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private String calculateCheckAndCheckMathHash(final Board board) {
        if(board.currentPlayer().isInCheckmate()){
            return "#";
        } else if (board.currentPlayer().isInCheck()) {
            return "+";
        }
        return "";
    }

    public static class DataModel extends DefaultTableModel {
        private final List<Row> values;
        private static final String[] NAMES = {"white", "black"};

        DataModel() {
            this.values = new ArrayList<>();
        }

        public void clear() {
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if (this.values == null) {
                return 0;
            }
            return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int column) {
            final Row currentRow = this.values.get(row);
            if (column == 0) {
                return currentRow.getWhiteMoves();
            } else if (column == 1) {
                return currentRow.getBlackMoves();

            }
            return null;
        }

        @Override
        public void setValueAt(final Object aValue, int row, int column) {
            final Row currentRow;
            if (this.values.size() <= row) {
                currentRow = new Row();
                this.values.add(currentRow);
            } else {
                currentRow = this.values.get(row);
            }
            if (column==0){
                currentRow.setWhiteMoves((String)aValue);
                fireTableRowsInserted(row,row);
            } else if (column==1) {
                currentRow.setBlackMoves((String)aValue);
                fireTableCellUpdated(row,column);
            }
        }
        @Override
        public Class<?>getColumnClass(final int column){
            return Move.class;
        }
        @Override
        public String getColumnName(final int column){
            return NAMES[column];
        }
    }
    private static class Row{
        private String whiteMoves;
        private String blackMoves;

        Row(){

        }
        public String getWhiteMoves(){
            return this.whiteMoves;
        }
        public String getBlackMoves(){
            return this.blackMoves;
        }
        public void setWhiteMoves(final String move){
            this.whiteMoves=move;
        }
        public void setBlackMoves(final String move){
            this.blackMoves=move;
        }
    }
}
