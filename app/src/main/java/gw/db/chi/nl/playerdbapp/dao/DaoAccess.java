package gw.db.chi.nl.playerdbapp.dao;

import android.content.ContentResolver;
import android.net.Uri;

import java.util.List;

import gw.db.chi.nl.playerdbapp.dao.entities.Player;

public interface DaoAccess {

    boolean playerRecordExists(int playerId);

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
