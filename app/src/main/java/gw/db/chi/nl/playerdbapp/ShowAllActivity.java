package gw.db.chi.nl.playerdbapp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ShowAllActivity extends AppCompatActivity {

//    private static final String DATABASE_NAME = "player-db";
//    private PlayerDatabase playerDb;
//    private List<Player> queryResult;

    private TableLayout mTableLayout;
    ProgressDialog mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("sma", "invoking ShowAllActivity.onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_showall);

        Log.e("sma", "invoking ShowAllActivity.onCreate 1");
        mProgressBar = new ProgressDialog(this);
        Log.e("sma", "invoking ShowAllActivity.onCreate 2");
        mTableLayout = (TableLayout) findViewById(R.id.tableMembers);

        Object testObj = findViewById(R.id.tableMembers);
        if (testObj == null) {
            Log.e("sma", "ShowAllActivity: Unexpected situation :(");
        }

        Log.e("sma", "invoking ShowAllActivity.onCreate 3");
        mTableLayout.setStretchAllColumns(true);

        Log.e("sma", "invoking ShowAllActivity.onCreate 4");
        startLoadData();

        Log.e("sma", "invoking ShowAllActivity.onCreate 5");

    }

    public void startLoadData() {
        Log.e("sma", "invoking ShowAllActivity.startLoadData");
        mProgressBar.setCancelable(false);
        mProgressBar.setMessage("Getting player data..");
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.show();
        new LoadDataTask().execute(0);
    }

    public void loadData() {

        Log.e("sma", "invoking ShowAllActivity.loadData");

        int leftRowMargin=0;
        int topRowMargin=0;
        int rightRowMargin=0;
        int bottomRowMargin = 0;
        int textSize = 0, smallTextSize =0, mediumTextSize = 0;

        Log.e("sma", "invoking ShowAllActivity.loadData - get fonts");
        textSize = (int) getResources().getDimension(R.dimen.font_size_verysmall);
        smallTextSize = (int) getResources().getDimension(R.dimen.font_size_small);
        mediumTextSize = (int) getResources().getDimension(R.dimen.font_size_medium);
        Log.e("sma", "invoking ShowAllActivity.loadData - got fonts");

        //List<Player> allPlayers = players.getPlayersStub();
        List<Player> allPlayers = DbWrapper.daoAccess(this).fetchAllPlayers();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        int rows = allPlayers.size();
        Log.e("sma", "invoking ShowAllActivity.loadData 1");
        getSupportActionBar().setTitle("Players (" + String.valueOf(rows) + ")");
        Log.e("sma", "invoking ShowAllActivity.loadData 2");

        TextView textSpacer = null;

        mTableLayout.removeAllViews();
        Log.e("sma", "invoking ShowAllActivity.loadData 3");

        // -1 means heading row
        for(int i = -1; i < rows; i ++) {
            Player row = null;
            if (i > -1)
                row = allPlayers.get(i);
            else {
                Log.e("sma", "invoking ShowAllActivity.loadData sub 1");
                textSpacer = new TextView(this);
                textSpacer.setText("");
                Log.e("sma", "invoking ShowAllActivity.loadData sub 2");

            }
            // data columns
            final TextView tv = new TextView(this);
            Log.e("sma", "invoking ShowAllActivity.loadData sub 3");
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            Log.e("sma", "invoking ShowAllActivity.loadData sub 4");
            tv.setGravity(Gravity.LEFT);

            Log.e("sma", "invoking ShowAllActivity.loadData sub 5");
            tv.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv.setText("Lidnr.");
                tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv.setText(String.valueOf(row.getPlayerId()));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            Log.e("sma", "invoking ShowAllActivity.loadData sub 6");
            final TextView tv2 = new TextView(this);
            if (i == -1) {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            Log.e("sma", "invoking ShowAllActivity.loadData sub 7");
            tv2.setGravity(Gravity.LEFT);

            tv2.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv2.setText("Geboren");
                tv2.setBackgroundColor(Color.parseColor("#f7f7f7"));
            }else {
                tv2.setBackgroundColor(Color.parseColor("#ffffff"));
                tv2.setTextColor(Color.parseColor("#000000"));
                tv2.setText(dateFormat.format(row.getJoinedDate()));
            }

            Log.e("sma", "invoking ShowAllActivity.loadData sub 8");

            final LinearLayout layCustomer = new LinearLayout(this);
            Log.e("sma", "invoking ShowAllActivity.loadData sub 9");
            layCustomer.setOrientation(LinearLayout.VERTICAL);
            Log.e("sma", "invoking ShowAllActivity.loadData sub 10");
            layCustomer.setPadding(0, 10, 0, 10);
            Log.e("sma", "invoking ShowAllActivity.loadData sub 11");
            layCustomer.setBackgroundColor(Color.parseColor("#f8f8f8"));

            final TextView tv3 = new TextView(this);
            if (i == -1) {
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv3.setPadding(5, 5, 0, 5);
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv3.setPadding(5, 0, 0, 5);
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            tv3.setGravity(Gravity.TOP);

            Log.e("sma", "invoking ShowAllActivity.loadData sub 12");

            if (i == -1) {
                tv3.setText("Spelernaam");
                tv3.setBackgroundColor(Color.parseColor("#f0f0f0"));
            } else {
                tv3.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv3.setTextColor(Color.parseColor("#000000"));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tv3.setText(row.getPlayerName());
            }
            layCustomer.addView(tv3);

            Log.e("sma", "invoking ShowAllActivity.loadData sub 13");

            if (i > -1) {
                final TextView tv3b = new TextView(this);
                tv3b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                tv3b.setGravity(Gravity.RIGHT);
                tv3b.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv3b.setPadding(5, 1, 0, 5);
                tv3b.setTextColor(Color.parseColor("#aaaaaa"));
                tv3b.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv3b.setText("...");
                layCustomer.addView(tv3b);
            }

            Log.e("sma", "invoking ShowAllActivity.loadData sub 14");

            final LinearLayout layAmounts = new LinearLayout(this);
            layAmounts.setOrientation(LinearLayout.VERTICAL);
            layAmounts.setGravity(Gravity.RIGHT);
            layAmounts.setPadding(0, 10, 0, 10);
            layAmounts.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

            Log.e("sma", "invoking ShowAllActivity.loadData sub 15");

            final TextView tv4 = new TextView(this);
            Log.e("sma", "invoking ShowAllActivity.loadData sub 15A");
            if (i == -1) {
                Log.e("sma", "invoking ShowAllActivity.loadData sub 15B");
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv4.setPadding(5, 5, 1, 5);
                layAmounts.setBackgroundColor(Color.parseColor("#f7f7f7"));
            } else {
                Log.e("sma", "invoking ShowAllActivity.loadData sub 15C");
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv4.setPadding(5, 0, 1, 5);
                layAmounts.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            Log.e("sma", "invoking ShowAllActivity.loadData sub 15d");
            tv4.setGravity(Gravity.RIGHT);

            if (i == -1) {
                tv4.setText("Email");
                tv4.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv4.setBackgroundColor(Color.parseColor("#ffffff"));
                tv4.setTextColor(Color.parseColor("#000000"));
                //tv4.setText(decimalFormat.format(row.invoiceAmount));
                tv4.setText(row.getEmail());
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            Log.e("sma", "invoking ShowAllActivity.loadData sub 16");

            layAmounts.addView(tv4);


            if (i > -1) {
                final TextView tv4b = new TextView(this);
                tv4b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                tv4b.setGravity(Gravity.RIGHT);
                tv4b.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv4b.setPadding(2, 2, 1, 5);
                tv4b.setTextColor(Color.parseColor("#00afff"));
                tv4b.setBackgroundColor(Color.parseColor("#ffffff"));

                layAmounts.addView(tv4b);
            }

            Log.e("sma", "invoking ShowAllActivity.loadData sub 17");

            // add table row
            final TableRow tr = new TableRow(this);
            tr.setId(i + 1);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
            tr.setPadding(0,0,0,0);
            tr.setLayoutParams(trParams);

            Log.e("sma", "invoking ShowAllActivity.loadData sub 18");

            tr.addView(tv);
            tr.addView(tv2);
            tr.addView(layCustomer);
            tr.addView(layAmounts);

            Log.e("sma", "invoking ShowAllActivity.loadData sub 19");

            if (i > -1) {

                tr.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        TableRow tr = (TableRow) v;
                        //do whatever action is needed
                    }
                });

            }

            Log.e("sma", "invoking ShowAllActivity.loadData sub 20");

            mTableLayout.addView(tr, trParams);

            Log.e("sma", "invoking ShowAllActivity.loadData sub 21");

            if (i > -1) {

                Log.e("sma", "invoking ShowAllActivity.loadData sub 22a");

                // add separator row
                final TableRow trSep = new TableRow(this);
                TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);

                trSep.setLayoutParams(trParamsSep);
                TextView tvSep = new TextView(this);
                TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                tvSepLay.span = 4;
                tvSep.setLayoutParams(tvSepLay);
                tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                tvSep.setHeight(1);

                trSep.addView(tvSep);
                mTableLayout.addView(trSep, trParamsSep);
            }

        }
    }



    //////////////////////////////////////////////////////////////////////////////

    //
    // The params are dummy and not used
    //
    class LoadDataTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {

            Log.e("sma", "invoking ShowAllActivity.LoadDataTask.doInBackground");
            try {
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e("sma", "invoking ShowAllActivity.LoadDataTask.onPostExecute");
            mProgressBar.hide();
            loadData();
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

}
