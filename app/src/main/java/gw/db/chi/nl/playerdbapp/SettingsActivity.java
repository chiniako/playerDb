package gw.db.chi.nl.playerdbapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    final static int PICKFILE_RESULT_CODE = 100;

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
                Log.e("sma", "SettingsActivity.onClick invoked on view: "+view.getId()+ ", before bg task");
                //LoadDbTask task = new LoadDbTask(SettingsActivity.this);
                //task.execute();
                //DbWrapper.daoAccess(SettingsActivity.this).importDb();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                SettingsActivity.this.startActivityForResult(intent, PICKFILE_RESULT_CODE);
                Log.e("sma", "SettingsActivity.onClick invoked on view: "+view.getId()+ ", after bg task");
            }
        });

    }

    // Catch import file pick result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("sma", "SettingsActivity.onActivityResult invoked with requestCode/resultCode: "+requestCode+":"+resultCode);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {
            try {
                if (data == null) {
                    Log.e("sma", "SettingsActivity.onActivityResult exception: no data received");
                }
                else {
                    Log.i("sma", "SettingsActivity.onActivityResult data received: "+data.getData().toString());
                    DbWrapper.daoAccess(this).initDbFromUri(data.getData(), getContentResolver());
                    Log.i("sma", "SettingsActivity.onActivityResult dbStore filled successfully");
                    TextView settingsTextView = this.findViewById(R.id.settingsTextView);
                    settingsTextView.setText("Import from db file successful");
                    Log.i("sma", "SettingsActivity.onActivityResult InfoText updated");
                }
            }
            catch (Exception e) {
                Log.e("sma", "SettingsActivity.onActivityResult exception: "+e.getLocalizedMessage());
            }

        }
    }


    private class LoadDbTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        public LoadDbTask(AppCompatActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Doing something, please wait.");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                Log.e("sma", "LoadDbTask.doInBackground before importDb()");
                //DbWrapper.daoAccess(SettingsActivity.this).importDb();
                Log.e("sma", "LoadDbTask.doInBackground after importDb()");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e("sma", "LoadDbTask.doInBackground exception: "+e.getLocalizedMessage());
            }

            return null;
        }

    }


}
