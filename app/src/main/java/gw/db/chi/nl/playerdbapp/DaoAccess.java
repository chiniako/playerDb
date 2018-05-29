package gw.db.chi.nl.playerdbapp;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public interface DaoAccess {

    void initDbFromUri(Uri dataFileUri, ContentResolver cr);

    void insertSinglePlayer(Player Player);

    void insertOrUpdateSinglePlayer(Player Player);

    void insertMultiplePlayer (List<Player> playerList);

    List<Player> fetchAllPlayers();

    List<Player> findPlayers(Player filters);

    Player fetchOnePlayerById(int playerId);

    void updatePlayer(Player player);

    void deletePlayer(Player player);
}
