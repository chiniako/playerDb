package gw.db.chi.nl.playerdbapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
            try {
                DbWrapper.daoAccess(this).initDbFromUri(data.getData(), getContentResolver());
                TextView settingsTextView = this.findViewById(R.id.settingsTextView);
                settingsTextView.setText("Import from db file successful");
            }
            catch (Exception e) {
                Log.e("sma", "onActivityResult exception: "+e.getLocalizedMessage());
            }

        }
    }

}
