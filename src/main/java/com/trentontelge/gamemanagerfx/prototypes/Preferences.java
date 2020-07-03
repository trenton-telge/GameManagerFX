package com.trentontelge.gamemanagerfx.prototypes;

@SuppressWarnings("unused")
public class Preferences {
    private String libraryHome;
    private boolean moveGames;

    public Preferences(){}

    public Preferences(String libraryHome, boolean moveGames) {
        this.libraryHome = libraryHome;
        this.moveGames = moveGames;
    }

    public String getLibraryHome() {
        return libraryHome;
    }

    public void setLibraryHome(String libraryHome) {
        this.libraryHome = libraryHome;
    }

    public boolean isMoveGames() {
        return moveGames;
    }

    public void setMoveGames(boolean moveGames) {
        this.moveGames = moveGames;
    }
}
