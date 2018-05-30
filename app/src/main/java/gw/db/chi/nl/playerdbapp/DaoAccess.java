package gw.db.chi.nl.playerdbapp;

import android.content.ContentResolver;
import android.net.Uri;

import java.util.List;

public interface DaoAccess {

    boolean initDbFromUri(Uri dataFileUri, ContentResolver cr);

    void insertSinglePlayer(Player Player);

    void insertOrUpdateSinglePlayer(Player Player);

    void insertMultiplePlayer (List<Player> playerList);

    List<Player> fetchAllPlayers();

    List<Player> findPlayers(Player filters);

    Player fetchOnePlayerById(int playerId);

    void updatePlayer(Player player);

    void deletePlayer(Integer recordId);
}
