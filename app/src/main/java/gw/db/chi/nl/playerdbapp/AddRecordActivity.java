package gw.db.chi.nl.playerdbapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gw.db.chi.nl.playerdbapp.dao.entities.Player;
import gw.db.chi.nl.playerdbapp.utils.OrmUtils;

public class AddRecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        Button saveBtn = findViewById(R.id.btnSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView infoField = findViewById(R.id.textViewAdd);
                if (!allFieldsFilled()) {
                    infoField.setText("The new record cannot be saved, all fields must be filled in!");
                } else {

                    DbWrapper.daoAccess(AddRecordActivity.this).storeDbInLocalFile();

                    List<EditText> allFields = getAllFormFields();

                    Player newPlayer =new Player();
                    newPlayer.setPlayerId(
                            Integer.valueOf(allFields.get(0).getText().toString())
                    );
                    newPlayer.setPlayerName(
                            allFields.get(1).getText().toString()
                    );
                    newPlayer.setJoinedDate(
                            OrmUtils.dateStringToLong(allFields.get(2).getText().toString())
                    );
                    newPlayer.setEmail(
                            allFields.get(3).getText().toString()
                    );
                    DbWrapper.daoAccess(AddRecordActivity.this).insertOrUpdateSinglePlayer(newPlayer);

                    infoField.setText("Player record added/updated succesfully");
                }
            }
        });


        Button cancelBtn = findViewById(R.id.btnCancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            AddRecordActivity.this.onBackPressed();
            }
        });

    }

    private boolean allFieldsFilled() {
        for (EditText fld : getAllFormFields()) {
            if (fld.getText().length() == 0) {
                return false;
            }
        }
        return true;
    }

    private List<EditText> getAllFormFields() {
        EditText idField = findViewById(R.id.editTextId);
        EditText nameField = findViewById(R.id.editTextName);
        EditText dateField = findViewById(R.id.editTextDate);
        EditText emailField = findViewById(R.id.editTextEmail);
        List<EditText> formFields = new ArrayList<EditText>();
        formFields.add(idField);
        formFields.add(nameField);
        formFields.add(dateField);
        formFields.add(emailField);
        return formFields;
    }

}
