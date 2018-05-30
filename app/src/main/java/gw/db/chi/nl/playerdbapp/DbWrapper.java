package gw.db.chi.nl.playerdbapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbWrapper implements DaoAccess {

    final static String dbLocation = "";
    final static String dbFileName = "players.db";
    final static String dbFileColumnSeperator = ";";
    private static DbWrapper pl = null;
    private static Map<Integer, Player> dbStore = null;
    private static AppCompatActivity currentActivity = null;

    private DbWrapper() {
        //readFromStub();
        //initDb();
        readFromFile();
    }

    public static DbWrapper daoAccess(AppCompatActivity activity) {
        if (pl == null) {
            dbStore = new HashMap<Integer, Player>();
            currentActivity = activity;
            pl = new DbWrapper();
        }
        return pl;
    }

    protected synchronized Map<Integer, Player> getDbStore() {
        return dbStore;
    }


    private void readFromFile() {
        try {
            readDbFromLocalFile();
        } catch (Exception e) {
            Log.e("sma", "DbWrapper exception, general exception at reading or writing db file: "+e.getLocalizedMessage());
        }
    }

    public void readDbFromLocalFile() throws Exception {

        if (currentActivity == null) {
            Log.e("sma", "Unexpected error at reading db file: Current activity undefined!");
        }
        else {
            File directory = currentActivity.getFilesDir();
            File file = new File(directory, dbFileName);
            Log.println(Log.ERROR, "ccc", "File found: " + file.exists());

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                Player nextPlayer;
                while ((line = br.readLine()) != null) {
                    if (line.indexOf(dbFileColumnSeperator) != -1) {
                        nextPlayer = new Player();
                        String[] columns = line.split(dbFileColumnSeperator);
                        if (columns.length == 4) {
                            nextPlayer.setPlayerId(Integer.valueOf(columns[0]));
                            nextPlayer.setPlayerName(columns[1]);
                            nextPlayer.setJoinedDate(Long.valueOf(columns[2]));
                            nextPlayer.setEmail(columns[3]);
                            getDbStore().put(nextPlayer.getPlayerId(), nextPlayer);
                        } else {
                            Log.e("sma", "DbWrapper exception - readDbFromFile, reading db file, unexpected amount of columns found: : " + columns.length);
                        }
                    }
                }

                br.close();
            } catch (IOException e) {
                Log.e("sma", "DbWrapper exception, reading db file: " + e.getLocalizedMessage());
            }

        }
    }

    public void storeDbInLocalFile(){

        StringWriter fileContentStream = new StringWriter(0);
        FileOutputStream outputStream;

        Player nextPlayer;
        try{

            if (currentActivity != null) {

                outputStream = currentActivity.openFileOutput(dbFileName, Context.MODE_PRIVATE);

                for (Integer nextId : getDbStore().keySet()) {
                    nextPlayer = getDbStore().get(nextId);
                    fileContentStream.append(nextPlayer.toRecordString(dbFileColumnSeperator));
                    fileContentStream.append("\n");
                }
                outputStream.write(fileContentStream.toString().getBytes());
                outputStream.close();
            }
            else {
                Log.e("sma", "Unexpected error at storing db file: Current activity undefined!");
            }

        }catch (Exception e){
            Log.e("sma", "DbWrapper exception, writing db file: "+e.getLocalizedMessage());
        }
    }


    public void exportDb(){

        if (currentActivity != null) {

            // make folder accessible outside this app

            File internalFile = new File(currentActivity.getFilesDir(), dbFileName);

            Uri uri = FileProvider.getUriForFile(currentActivity, "gw.db.chi.nl.fileprovider", internalFile);

            Intent intent = ShareCompat.IntentBuilder.from(currentActivity)
                    .setStream(uri) // uri from FileProvider
                    .setType("text/plain")
                    .getIntent()
//                        .setAction(Intent.ACTION_VIEW) //Change if needed
//                        .setDataAndType(uri, "text/*")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            currentActivity.startActivity(intent);

        }
        else {
            Log.e("sma", "Unexpected error at storing db file: Current activity undefined!");
        }

    }

    @Override
    public boolean initDbFromUri(Uri dataFileUri, ContentResolver cr) {
        getDbStore().clear();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            cr.openInputStream(dataFileUri)));
            String line;

            Player nextPlayer;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(dbFileColumnSeperator) != -1) {
                    nextPlayer = new Player();
                    Log.e("sma", "initDbFromUri: next line to be imported: "+line);
                    String[] columns = line.split(dbFileColumnSeperator);
                    if (columns.length == 4) {
                        nextPlayer.setPlayerId(Integer.valueOf(columns[0]));
                        nextPlayer.setPlayerName(columns[1]);
                        nextPlayer.setJoinedDate(Long.valueOf(columns[2]));
                        nextPlayer.setEmail(columns[3]);
                        getDbStore().put(nextPlayer.getPlayerId(), nextPlayer);
                    } else {
                        Log.e("sma", "DbWrapper exception - initDbFromUri, reading db file, unexpected amount of columns found: : " + columns.length);
                        return false;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            Log.e("sma", "DbWrapper exception - FileNotFound");
        } catch (IOException e) {
            Log.e("sma", "DbWrapper exception - IO");
        }

        return true;
    }

    @Override
    public void insertSinglePlayer(Player player) {
        if (getDbStore().containsKey(player.getPlayerId())) {
            Log.e("sma", "DbWrapper exception, adding player record failed, unique constraint!");
        }
        else {
            getDbStore().put(player.getPlayerId(), player);
        }
    }
    @Override
    public void insertOrUpdateSinglePlayer(Player player) {
        if (getDbStore().containsKey(player.getPlayerId())) {
            Log.e("sma", "Old player record: "+getDbStore().get(player.getPlayerId()).toRecordString());
            Log.e("sma", "New player record: "+player.toRecordString());
            getDbStore().remove(player.getPlayerId());
            getDbStore().put(player.getPlayerId(), player);
            Log.e("sma", "Old player record overwritten with new one");
        }
        else {
            getDbStore().put(player.getPlayerId(), player);
        }
    }

    @Override
    public void insertMultiplePlayer(List<Player> playerList) {

    }

    @Override
    public List<Player> fetchAllPlayers() {
        List<Player> playerList = new ArrayList<Player>();

        Player nextPlayer;
        Player resultRow;
        for (Integer nextId : getDbStore().keySet()) {
            resultRow = new Player();
            nextPlayer = getDbStore().get(nextId);
            resultRow.setPlayerId(nextId);
            resultRow.setPlayerName(nextPlayer.getPlayerName());
            resultRow.setJoinedDate(nextPlayer.getJoinedDate());
            resultRow.setEmail(nextPlayer.getEmail());
            playerList.add(resultRow);
        }
        return playerList;
    }

    @Override
    public List<Player> findPlayers(Player filter) {
        List<Player> playerList = new ArrayList<Player>();

        Player nextPlayer;
        Player resultRow;
        for (Integer nextId : getDbStore().keySet()) {
            if (isMatch(getDbStore().get(nextId), filter)) {
                resultRow = new Player();
                nextPlayer = getDbStore().get(nextId);
                resultRow.setPlayerId(nextId);
                resultRow.setPlayerName(nextPlayer.getPlayerName());
                resultRow.setJoinedDate(nextPlayer.getJoinedDate());
                resultRow.setEmail(nextPlayer.getEmail());
                playerList.add(resultRow);
            }
        }
        return playerList;
    }


    @Override
    public Player fetchOnePlayerById(int playerId) {
        return getDbStore().get(playerId);
    }

    @Override
    public boolean playerRecordExists(int playerId) {
        return getDbStore().containsKey(playerId);
    }

    @Override
    public void updatePlayer(Player player) {

    }

    @Override
    public void deletePlayer(Integer recordId) {
        if (!getDbStore().containsKey(recordId)) {
            showTextInInfoField("Deletion failed: player record not found!");
        }
        else {
            String toBeRemoved = getDbStore().get(recordId).toRecordString();
            getDbStore().remove(recordId);
            Log.e("sma", "Player record removed from database: "+toBeRemoved);
        }
    }

    private boolean isMatch(Player record, Player filter) {
        boolean matchId = true, matchName = true, matchDate = true, matchEmail = true;

        if (filter.getPlayerId() > 0 && record.getPlayerId() != filter.getPlayerId()) {
            matchId = false;
        }
        if (!filter.getPlayerName().isEmpty() && !record.getPlayerName().equals(filter.getPlayerName())) {
            matchName = false;
        }
        if (filter.getJoinedDate() > 0 && record.getJoinedDate() != filter.getJoinedDate()) {
            matchDate = false;
        }
        if (!filter.getEmail().isEmpty() && !record.getEmail().equals(filter.getEmail())) {
            matchEmail = false;
        }

        return matchId && matchName && matchDate && matchEmail;
    }


    private void showTextInInfoField(String text) {
        TextView infoField = currentActivity.findViewById(R.id.textView);
        infoField.setText(text);
    }

}