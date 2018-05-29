package gw.db.chi.nl.playerdbapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static int COLOR_BG_DEFAULT =-1;
    private TableLayout searchResultTableLayout;
    private boolean idFieldEmpty =false, nameFieldEmpty =false, dateFieldEmpty =false, emailFieldEmpty =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initUi();

        // setup the table
        searchResultTableLayout =(TableLayout) findViewById(R.id.tableSearchResults);
        searchResultTableLayout.setStretchAllColumns(true);

        COLOR_BG_DEFAULT =findViewById(R.id.editTextId).getDrawingCacheBackgroundColor();

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button saveBtn = findViewById(R.id.btnSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView infoField =(TextView) findViewById(R.id.textView);
                if (!allFieldFilled()) {
                    infoField.setText("Record cannot be added/modified, all fields must be filled in!");
                } else {
                    DbWrapper.daoAccess(MainActivity.this).storeDbInLocalFile();
                    EditText idField =(EditText) findViewById(R.id.editTextId);
                    EditText nameField =(EditText) findViewById(R.id.editTextName);
                    EditText dateField =(EditText) findViewById(R.id.editDate);
                    EditText emailField =(EditText) findViewById(R.id.editEmail);

                    Player newPlayer =new Player();
                    newPlayer.setPlayerId(
                            Integer.valueOf(idField.getText().toString())
                    );
                    newPlayer.setPlayerName(
                            nameField.getText().toString()
                    );
                    newPlayer.setJoinedDate(
                            Long.valueOf(dateField.getText().toString())
                    );
                    newPlayer.setEmail(
                            emailField.getText().toString()
                    );
                    DbWrapper.daoAccess(MainActivity.this).insertOrUpdateSinglePlayer(newPlayer);

                    infoField.setText("Player record added/updated succesfully");

                }
            }
        });

        Button settingsBtn = findViewById(R.id.btnSettings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                    new Intent(MainActivity.this, SettingsActivity.class)
                );
                //DbWrapper.daoAccess(MainActivity.this).exportDb();
            }
        });

        FloatingActionButton showAllBtn =(FloatingActionButton) findViewById(R.id.btnShowAll);
        showAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Player> dbData =DbWrapper.daoAccess(MainActivity.this).fetchAllPlayers();
                if (dbData.size() > 0) {
                    startActivity(
                        new Intent(MainActivity.this, ShowAllActivity.class)
                    );
                }
                else {
                    Snackbar.make(view, "Ooops, empty database?", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        Button resetBtn = findViewById(R.id.btnReset);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText idField =(EditText)findViewById(R.id.editTextId);
                EditText nameField =(EditText)findViewById(R.id.editTextName);
                EditText dateField =(EditText)findViewById(R.id.editDate);
                EditText emailField =(EditText)findViewById(R.id.editEmail);

                idField.setText("");
                idField.setBackgroundColor(COLOR_BG_DEFAULT);
                nameField.setText("");
                nameField.setBackgroundColor(COLOR_BG_DEFAULT);
                dateField.setText("");
                dateField.setBackgroundColor(COLOR_BG_DEFAULT);
                emailField.setText("");
                emailField.setBackgroundColor(COLOR_BG_DEFAULT);
            }
        });


        Button findBtn = findViewById(R.id.btnFind);
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText idField =(EditText)findViewById(R.id.editTextId);
                EditText nameField =(EditText)findViewById(R.id.editTextName);
                EditText dateField =(EditText)findViewById(R.id.editDate);
                EditText emailField =(EditText)findViewById(R.id.editEmail);

                Player newPlayer =new Player();
                newPlayer.setPlayerId(idField.getText().length() > 0 ?
                        Integer.valueOf(idField.getText().toString()) :
                        0
                );
                newPlayer.setPlayerName(nameField.getText().length() > 0 ?
                        nameField.getText().toString() :
                        ""
                );
                newPlayer.setJoinedDate(dateField.getText().length() > 0 ?
                        Long.valueOf(dateField.getText().toString()) :
                        0
                );
                newPlayer.setEmail(emailField.getText().length() > 0 ?
                        emailField.getText().toString() :
                        ""
                );

                List<Player> searchResult =DbWrapper.daoAccess(MainActivity.this).findPlayers(newPlayer);

                if (searchResult.size() ==1) {

                    idField.setText( Integer.toString(searchResult.get(0).getPlayerId()) );
                    idField.setBackgroundColor(Color.GRAY);
                    nameField.setText(searchResult.get(0).getPlayerName());
                    nameField.setBackgroundColor(Color.GRAY);
                    dateField.setText(Long.toString(searchResult.get(0).getJoinedDate()) );
                    dateField.setBackgroundColor(Color.GRAY);
                    emailField.setText(searchResult.get(0).getEmail());
                    emailField.setBackgroundColor(Color.GRAY);

                }
                else {
                    loadSearchResultsInTable(searchResult);
                }
            }
        });

    }


    private void initUi() {
        //setFindFunction(false);
        idFieldEmpty =true;
        nameFieldEmpty =true;
        dateFieldEmpty =true;
        emailFieldEmpty =true;
        setSaveFunction(false);
        addTextWatchersToForm();

    }

    private boolean allFieldFilled() {
        return idFieldEmpty && nameFieldEmpty && dateFieldEmpty && emailFieldEmpty;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id =item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id ==R.id.action_settings) {
            //return true;
            /*
            startActivity(
                new Intent(MainActivity.this, SettingsActivity.class)
            );
            */
        }

        return super.onOptionsItemSelected(item);
    }


    private void loadSearchResultsInTable(List<Player> data) {

        int leftRowMargin=0;
        int topRowMargin=0;
        int rightRowMargin=0;
        int bottomRowMargin =0;
        int textSize =0, smallTextSize =0, mediumTextSize =0;

        textSize =(int) getResources().getDimension(R.dimen.font_size_verysmall);
        smallTextSize =(int) getResources().getDimension(R.dimen.font_size_small);
        mediumTextSize =(int) getResources().getDimension(R.dimen.font_size_medium);

        SimpleDateFormat dateFormat =new SimpleDateFormat("dd MMM, yyyy");
        DecimalFormat decimalFormat =new DecimalFormat("0.00");

        int rows =data.size();
        getSupportActionBar().setTitle("Spelers (" + String.valueOf(rows) + ")");
        TextView textSpacer =null;

        searchResultTableLayout.removeAllViews();

        Log.e("sma", "invoking ShowAllActivity.loadData 3");

        // -1 means heading row
        for(int i =-1; i < rows; i ++) {
            Player row =null;
            if (i > -1)
                row =data.get(i);
            else {
                Log.e("sma", "invoking ShowAllActivity.loadData sub 1");
                textSpacer =new TextView(this);
                textSpacer.setText("");
                Log.e("sma", "invoking ShowAllActivity.loadData sub 2");

            }

            // data columns
            TextView tv =new TextView(this);
            Log.e("sma", "invoking ShowAllActivity.loadData sub 3");
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            Log.e("sma", "invoking ShowAllActivity.loadData sub 4");
            tv.setGravity(Gravity.LEFT);

            Log.e("sma", "invoking ShowAllActivity.loadData sub 5");
            tv.setPadding(5, 15, 0, 15);
            if (i ==-1) {
                tv.setText("Lidnr.");
                tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv.setText(String.valueOf(row.getPlayerId()));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            Log.e("sma", "invoking ShowAllActivity.loadData sub 6");
            final TextView tv2 =new TextView(this);
            if (i ==-1) {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv2.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            Log.e("sma", "invoking ShowAllActivity.loadData sub 7");
            tv2.setGravity(Gravity.LEFT);

            tv2.setPadding(5, 15, 0, 15);
            if (i ==-1) {
                tv2.setText("Geboren");
                tv2.setBackgroundColor(Color.parseColor("#f7f7f7"));
            }else {
                tv2.setBackgroundColor(Color.parseColor("#ffffff"));
                tv2.setTextColor(Color.parseColor("#000000"));
                tv2.setText(dateFormat.format(row.getJoinedDate()));
            }

            Log.e("sma", "invoking ShowAllActivity.loadData sub 8");

            final LinearLayout layCustomer =new LinearLayout(this);
            Log.e("sma", "invoking ShowAllActivity.loadData sub 9");
            layCustomer.setOrientation(LinearLayout.VERTICAL);
            Log.e("sma", "invoking ShowAllActivity.loadData sub 10");
            layCustomer.setPadding(0, 10, 0, 10);
            Log.e("sma", "invoking ShowAllActivity.loadData sub 11");
            layCustomer.setBackgroundColor(Color.parseColor("#f8f8f8"));

            final TextView tv3 =new TextView(this);
            if (i ==-1) {
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

            if (i ==-1) {
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
                final TextView tv3b =new TextView(this);
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

            final LinearLayout layAmounts =new LinearLayout(this);
            layAmounts.setOrientation(LinearLayout.VERTICAL);
            layAmounts.setGravity(Gravity.RIGHT);
            layAmounts.setPadding(0, 10, 0, 10);
            layAmounts.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

            Log.e("sma", "invoking ShowAllActivity.loadData sub 15");

            final TextView tv4 =new TextView(this);
            Log.e("sma", "invoking ShowAllActivity.loadData sub 15A");
            if (i ==-1) {
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

            if (i ==-1) {
                tv4.setText("Email");
                tv4.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv4.setBackgroundColor(Color.parseColor("#ffffff"));
                tv4.setTextColor(Color.parseColor("#000000"));
                tv4.setText(row.getEmail());
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            Log.e("sma", "invoking ShowAllActivity.loadData sub 16");

            layAmounts.addView(tv4);


            if (i > -1) {
                final TextView tv4b =new TextView(this);
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
            final TableRow tr =new TableRow(this);
            tr.setId(i + 1);
            TableLayout.LayoutParams trParams =new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
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
                        TableRow tr =(TableRow) v;
                        if (tr.getChildCount() ==4) {
                            TextView idField =(TextView) tr.getChildAt(0);
                            String idValueStr =idField.getText().toString();

                            Player resultRecord =DbWrapper.daoAccess(MainActivity.this).fetchOnePlayerById(
                                    Integer.valueOf(idValueStr));

                            ((EditText) findViewById(R.id.editTextId)).
                                    setText(idValueStr);
                            ((EditText)findViewById(R.id.editTextName)).
                                    setText( resultRecord.getPlayerName() );
                            ((EditText)findViewById(R.id.editDate)).
                                    setText( Long.toString(resultRecord.getJoinedDate()) );
                            ((EditText)findViewById(R.id.editEmail)).
                                    setText( resultRecord.getEmail() );
                            //Log.e("sma", "Table button pressed: "+text);
                        }
                    }
                });

            }

            Log.e("sma", "invoking ShowAllActivity.loadData sub 20");

            searchResultTableLayout.addView(tr, trParams);

            Log.e("sma", "invoking ShowAllActivity.loadData sub 21");

            if (i > -1) {

                Log.e("sma", "invoking ShowAllActivity.loadData sub 22a");

                // add separator row
                final TableRow trSep =new TableRow(this);
                TableLayout.LayoutParams trParamsSep =new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);

                trSep.setLayoutParams(trParamsSep);
                TextView tvSep =new TextView(this);
                TableRow.LayoutParams tvSepLay =new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                tvSepLay.span =4;
                tvSep.setLayoutParams(tvSepLay);
                tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                tvSep.setHeight(1);

                trSep.addView(tvSep);
                searchResultTableLayout.addView(trSep, trParamsSep);
            }

        }
    }

    private void addTextWatchersToForm() {
        EditText idField =(EditText) findViewById(R.id.editTextId);
        idField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() ==0) {
                    idFieldEmpty =true;
                    setSaveFunction(false);
//                    setFindFunction(false);
                }
                else {
                    setSaveFunction(true);
//                    setFindFunction(true);
                }
            }
        });
        EditText nameField =(EditText) findViewById(R.id.editTextName);
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() ==0) {
                    nameFieldEmpty =true;
                    setSaveFunction(false);
//                    setFindFunction(false);
                }
                else {
                    setSaveFunction(true);
//                    setFindFunction(true);
                }
            }
        });
        EditText dateField =(EditText) findViewById(R.id.editDate);
        dateField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() ==0) {
                    dateFieldEmpty =true;
                    setSaveFunction(false);
//                    setFindFunction(false);
                }
                else {
                    setSaveFunction(true);
//                    setFindFunction(true);
                }
            }
        });
        EditText emailField =(EditText) findViewById(R.id.editEmail);
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() ==0) {
                    emailFieldEmpty =true;
                    setSaveFunction(false);
//                    setFindFunction(false);
                }
                else {
                    setSaveFunction(true);
//                    setFindFunction(true);
                }
            }
        });
    }


    private void setFindFunction(boolean on) {
        Button findBtn = findViewById(R.id.btnFind);
        if (findBtn !=null && (findBtn.isEnabled() !=on)) {
            findBtn.setEnabled(on);
        }
    }
    private void setSaveFunction(boolean on) {
        Button saveBtn = findViewById(R.id.btnSave);
        if (saveBtn !=null && (saveBtn.isEnabled() !=on)) {
            saveBtn.setEnabled(on);
        }
    }

}