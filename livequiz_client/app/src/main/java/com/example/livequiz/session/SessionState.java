package com.example.livequiz.session;

public enum SessionState {
    CLOSED,
    OPEN,
    FINISHED_RESULTS,
    FINISHED_CLOSED;

    public static boolean shouldDisconnect(SessionState state) {
        return state.equals(CLOSED) || state.equals(FINISHED_CLOSED);
    }

    public static boolean canConnect(SessionState state) {
        return state.equals(OPEN) || state.equals(FINISHED_RESULTS);
    }
}