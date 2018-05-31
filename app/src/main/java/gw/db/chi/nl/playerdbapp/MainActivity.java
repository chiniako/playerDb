package gw.db.chi.nl.playerdbapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import gw.db.chi.nl.playerdbapp.dao.entities.Player;
import gw.db.chi.nl.playerdbapp.utils.OrmUtils;
import gw.db.chi.nl.playerdbapp.validation.DateTextWatcher;

public class MainActivity extends AppCompatActivity {

    static int COLOR_BG_DEFAULT =-1;
    private TableLayout searchResultTableLayout;

    @Override
    protected void onStart() {
        searchWithFilter();
        super.onStart();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private boolean idFieldEmpty =false, nameFieldEmpty =false, dateFieldEmpty =false, emailFieldEmpty =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initUi();
//        addTextWatchersToForm();

        // setup the table
        searchResultTableLayout = findViewById(R.id.tableSearchResults);
        searchResultTableLayout.setStretchAllColumns(true);

        COLOR_BG_DEFAULT =findViewById(R.id.editTextId).getDrawingCacheBackgroundColor();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button addBtn = findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                    new Intent(MainActivity.this, AddRecordActivity.class)
                );
            }
        });

        Button saveBtn = findViewById(R.id.btnSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                TextView infoField = findViewById(R.id.textView);

                if (allFieldsFilled()) {

                    EditText emailAddressField = MainActivity.this.findViewById(R.id.editEmail);
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddressField.getText()).matches()) {
                        infoField.setText("Error: invalid email address!");
                    }
                    else {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:

                                        //Yes button clicked
                                        List<EditText> formFields = getAllFormFields();
                                        Player currentPlayer = new Player();
                                        currentPlayer.setPlayerId(Integer.valueOf(formFields.get(0).getText().toString()));
                                        currentPlayer.setPlayerName(formFields.get(1).getText().toString());
                                        currentPlayer.setJoinedDate(OrmUtils.dateStringToLong(formFields.get(2).getText().toString()));
                                        currentPlayer.setEmail(formFields.get(3).getText().toString());

                                        DbWrapper.daoAccess(MainActivity.this).updatePlayer(currentPlayer);
                                        searchWithFilter();

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:

                                        //No button clicked
                                        // Dont't do anything, just close the dialogue

                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
                        builder.setMessage("Are you sure?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                }
                else {
                    infoField.setText("Error: all fields must be filled in!");
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


        Button showAllBtn = findViewById(R.id.btnShowAll);
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
                resetFields();
                Button findBtn = findViewById(R.id.btnFind);
                findBtn.performClick();
            }
        });


        Button findBtn = findViewById(R.id.btnFind);
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText idField = findViewById(R.id.editTextId);
                EditText nameField = findViewById(R.id.editTextName);
                EditText dateField = findViewById(R.id.editDate);
                EditText emailField = findViewById(R.id.editEmail);

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
                        OrmUtils.dateStringToLong(dateField.getText().toString()) :
                        0
                );
                newPlayer.setEmail(emailField.getText().length() > 0 ?
                        emailField.getText().toString() :
                        ""
                );

                List<Player> searchResult = DbWrapper.daoAccess(MainActivity.this).findPlayers(newPlayer);

                if (searchResult.size() ==1) {

                    idField.setText( Integer.toString(searchResult.get(0).getPlayerId()) );
                    idField.setBackgroundColor(Color.GRAY);
                    nameField.setText(searchResult.get(0).getPlayerName());
                    nameField.setBackgroundColor(Color.GRAY);
                    dateField.setText(OrmUtils.dateLongToString(searchResult.get(0).getJoinedDate()));
                    dateField.setBackgroundColor(Color.GRAY);
                    emailField.setText(searchResult.get(0).getEmail());
                    emailField.setBackgroundColor(Color.GRAY);
                }
                loadSearchResultsInTable(searchResult);
            }
        });

        Button deleteBtn = findViewById(R.id.btnDel);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                //Yes button clicked

                                EditText idField = findViewById(R.id.editTextId);
                                Integer currentPlayerId = idField.getText().length() > 0 ?
                                        Integer.valueOf(idField.getText().toString()) :
                                        0;

                                TextView infoField = findViewById(R.id.textView);

                                if (currentPlayerId == 0) {
                                    Log.e("sma", "MainActivity deletePlayer exception: player Id undefined");
                                    infoField.setText("Error: player could not be removed, player Id undefined!");
                                }
                                else {
                                    DbWrapper.daoAccess(MainActivity.this).deletePlayer(currentPlayerId);
                                    resetFields();
                                    searchWithFilter();
                                    infoField.setText("Player record deleted succesfully");
                                }

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                //No button clicked
                                // Dont't do anything, just close the dialogue

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
                builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

            }
        });

        EditText dateField = findViewById(R.id.editDate);
        dateField.addTextChangedListener(new DateTextWatcher());

    }


    private void initUi() {
        //setFindFunction(false);
        idFieldEmpty = true;
        nameFieldEmpty = true;
        dateFieldEmpty = true;
        emailFieldEmpty = true;
//        setSaveFunction(false);

    }

    private void resetFields() {
        EditText idField = findViewById(R.id.editTextId);
        EditText nameField = findViewById(R.id.editTextName);
        EditText dateField = findViewById(R.id.editDate);
        EditText emailField = findViewById(R.id.editEmail);

        idField.setText("");
        idField.setBackgroundColor(COLOR_BG_DEFAULT);
        nameField.setText("");
        nameField.setBackgroundColor(COLOR_BG_DEFAULT);
        dateField.setText("");
        dateField.setBackgroundColor(COLOR_BG_DEFAULT);
        emailField.setText("");
        emailField.setBackgroundColor(COLOR_BG_DEFAULT);
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
        //mediumTextSize =(int) getResources().getDimension(R.dimen.font_size_medium);

        SimpleDateFormat dateFormat =new SimpleDateFormat("dd MMM, yyyy");
        //DecimalFormat decimalFormat =new DecimalFormat("0.00");

        int rows =data.size();
        getSupportActionBar().setTitle("Spelers (" + String.valueOf(rows) + ")");
        TextView textSpacer;

        searchResultTableLayout.removeAllViews();

        // -1 means heading row
        for(int i =-1; i < rows; i ++) {
            Player row =null;
            if (i > -1)
                row =data.get(i);
            else {
                textSpacer =new TextView(this);
                textSpacer.setText("");
            }

            // data columns
            TextView tv =new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tv.setGravity(Gravity.LEFT);

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

            final LinearLayout layCustomer =new LinearLayout(this);
            layCustomer.setOrientation(LinearLayout.VERTICAL);
            layCustomer.setPadding(0, 10, 0, 10);
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

            final LinearLayout layAmounts =new LinearLayout(this);
            layAmounts.setOrientation(LinearLayout.VERTICAL);
            layAmounts.setGravity(Gravity.RIGHT);
            layAmounts.setPadding(0, 10, 0, 10);
            layAmounts.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

            final TextView tv4 =new TextView(this);
            if (i ==-1) {
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv4.setPadding(5, 5, 1, 5);
                layAmounts.setBackgroundColor(Color.parseColor("#f7f7f7"));
            } else {
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv4.setPadding(5, 0, 1, 5);
                layAmounts.setBackgroundColor(Color.parseColor("#ffffff"));
            }

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

            // add table row
            final TableRow tr =new TableRow(this);
            tr.setId(i + 1);
            TableLayout.LayoutParams trParams =new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
            tr.setPadding(0,0,0,0);
            tr.setLayoutParams(trParams);

            tr.addView(tv);
            tr.addView(tv2);
            tr.addView(layCustomer);
            tr.addView(layAmounts);

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
                                    setText( OrmUtils.dateLongToString(resultRecord.getJoinedDate()) );
                            ((EditText)findViewById(R.id.editEmail)).
                                    setText( resultRecord.getEmail() );
                            //Log.e("sma", "Table button pressed: "+text);
                        }
                    }
                });

            }

            searchResultTableLayout.addView(tr, trParams);

            if (i > -1) {

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

    private void setFindFunction(boolean on) {
        Button findBtn = findViewById(R.id.btnFind);
        if (findBtn !=null && (findBtn.isEnabled() !=on)) {
            findBtn.setEnabled(on);
        }
    }
    private void setAddFunction(boolean on) {
        Button addBtn = findViewById(R.id.btnAdd);
        if (addBtn !=null && (addBtn.isEnabled() !=on)) {
            addBtn.setEnabled(on);
        }
    }


    private void addTextWatchersToForm() {

        EditText idField = findViewById(R.id.editTextId);
        idField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                searchWithFilter();
            }
        });

        EditText nameField = findViewById(R.id.editTextName);
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                searchWithFilter();
            }
        });

        EditText dateField = findViewById(R.id.editDate);
        dateField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                searchWithFilter();
            }
        });

        EditText emailField = findViewById(R.id.editEmail);
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                searchWithFilter();
            }
        });

    }

    private void searchWithFilter() {
        Button findBtn = findViewById(R.id.btnFind);
        findBtn.performClick();
    }

    private List<EditText> getAllFormFields() {
        EditText idField = findViewById(R.id.editTextId);
        EditText nameField = findViewById(R.id.editTextName);
        EditText dateField = findViewById(R.id.editDate);
        EditText emailField = findViewById(R.id.editEmail);
        List<EditText> formFields = new ArrayList<EditText>();
        formFields.add(idField);
        formFields.add(nameField);
        formFields.add(dateField);
        formFields.add(emailField);
        return formFields;
    }

    private boolean allFieldsFilled() {
        for (EditText fld : getAllFormFields()) {
            if (fld.getText().length() == 0) {
                return false;
            }
        }
        return true;
    }

}
