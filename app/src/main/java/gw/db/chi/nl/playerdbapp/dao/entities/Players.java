package gw.db.chi.nl.playerdbapp.dao.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Players {

    public List<Player> getPlayersStub() {
        List<Player> data = new ArrayList<Player>();

        for(int i = 0; i < 30; i ++) {
            Player row = new Player();
            row.setPlayerId(i+1);
            row.setPlayerName("Voornaam Achternaam "+i);
            row.setJoinedDate(new Date().getTime());
            row.setEmail("abc-"+i+"@defgh.com");
            data.add(row);
        }
        return data;
    }

}

