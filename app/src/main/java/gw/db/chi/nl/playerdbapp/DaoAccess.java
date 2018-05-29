package gw.db.chi.nl.playerdbapp;

import java.util.List;

public interface DaoAccess {

    void initDb();

    void insertSinglePlayer(Player Player);

    void insertOrUpdateSinglePlayer(Player Player);

    void insertMultiplePlayer (List<Player> playerList);

    List<Player> fetchAllPlayers();

    List<Player> findPlayers(Player filters);

    Player fetchOnePlayerById(int playerId);

    void updatePlayer(Player player);

    void deletePlayer(Player player);
}
