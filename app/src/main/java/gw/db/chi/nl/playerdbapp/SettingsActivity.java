package gw.db.chi.nl.playerdbapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button exportBtn = findViewById(R.id.btnExportDb);
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbWrapper.daoAccess(SettingsActivity.this).exportDb();
            }
        });

        Button importBtn = findViewById(R.id.btnImportDb);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbWrapper.daoAccess(SettingsActivity.this).importDb();
            }
        });

    }

    // Catch import file pick result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DbWrapper.PICKFILE_RESULT_CODE) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            try {
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                        getContentResolver().openInputStream(uri)));
                StringWriter strW = new StringWriter();
                String line;
                while ((line = br.readLine()) != null) {
                    strW.append(line.getBytes().toString()+"\n");
                }
                br.close();

                TextView settingsTextView = findViewById(R.id.settingsTextView);
                settingsTextView.setText(strW.toString());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //LaterFunction(filePathName);
        }
    }


}
