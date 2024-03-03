package com.chess.engine.player;

public enum MoveStatus {
    DONE{
        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public boolean isIllegal() {
            return false;
        }
    }, ILLEGAL_MOVE {
        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean isIllegal() {
            return true;
        }
    }, LEAVES_PLAYERS_IN_CHECK {
        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean isIllegal() {
            return false;
        }
    };
    public abstract boolean isDone();
    public abstract boolean isIllegal();
}
