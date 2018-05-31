package gw.db.chi.nl.playerdbapp.dao.entities;

import android.support.annotation.NonNull;

public class Player {
    @NonNull
    private int playerId;

    private String playerName;

    private long joinedDate;

    private String email;

    public Player() {
    }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName (String playerName) { this.playerName = playerName; }

    public long getJoinedDate() { return joinedDate; }
    public void setJoinedDate (long joinedDate) { this.joinedDate = joinedDate; }

    public String getEmail() { return email; }
    public void setEmail (String email) { this.email = email; }

    public String toRecordString() {
        return getPlayerId() + ";" + getPlayerName() + ";" + getJoinedDate() + ";" + getEmail();
    }
    public String toRecordString(String seperator) {
        return getPlayerId() + seperator + getPlayerName() + seperator + getJoinedDate() + seperator + getEmail();
    }

}