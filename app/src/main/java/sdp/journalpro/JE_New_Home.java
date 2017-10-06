package sdp.journalpro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class JE_New_Home extends JE_Base_Activity implements View.OnClickListener, Serializable {

    // Layout Variables
    EditText name;
    EditText date;
    EditText description;
    Button donebutton;

    // FireBase Variables
    private FirebaseAuth mAuth;
    //    private FirebaseUser mVerifiedUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.je_new_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        donebutton = findViewById(R.id.new_home_done_button);
        donebutton.setOnClickListener(this);

        name = findViewById(R.id.new_home_name_edit);
        date = findViewById(R.id.new_home_date_edit);
        description = findViewById(R.id.new_home_description_edit);

        date.setFocusable(false);
        date.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //go back to je_main_home activity and give up the new page
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.new_home_done_button) {
            if (!validateForm()) {
                Toast.makeText(JE_New_Home.this, "validate form failed", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(JE_New_Home.this, "wirte successed", Toast.LENGTH_SHORT).show();
                String uuidString = UUID.randomUUID().toString();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    writeDataOfUser(currentUser.getUid().toString(), uuidString);
                } else {
                    Toast.makeText(this.getApplicationContext(), "Your account not through verify", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (i == R.id.new_home_date_edit) {
            Intent intent = new Intent(this, JE_Calendar_Activity.class);
            startActivityForResult(intent, 1);
            Log.e("Touched Done Button", "new_home_date_edit");
        }
    }

    // Verifies that field data is valid
    private boolean validateForm() {
        boolean valid = true;

        String _name = name.getText().toString();
        if (TextUtils.isEmpty(_name)) {
            name.setError("Required.");
            valid = false;
        } else {
            name.setError(null);
        }

        String _date = date.getText().toString();
        if (TextUtils.isEmpty(_date)) {
            date.setError("Required.");
            valid = false;
        } else {
            date.setError(null);
        }

        String _description = description.getText().toString();
        if (TextUtils.isEmpty(_description)) {
            description.setError("Required.");
            valid = false;
        } else {
            description.setError(null);
        }

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                date.setText(strEditText);
            }
        }
    }

    private void writeDataOfUser(String userId, String uuid) {
        // get the intent is JE_Main_Home
        Intent mJE_Main_Home = getIntent();
        // get HashMap user_date;
        HashMap<String, Object> jsonData = (HashMap<String, Object>) mJE_Main_Home.getSerializableExtra("JSONDATA");

        // get data by User Id
        HashMap<String, Object> jsonUserDateById = (HashMap<String, Object>) jsonData.get(userId);

        if (jsonUserDateById == null) {
            jsonUserDateById = new HashMap<>();
        }

        // get UserDate and UserDetail Hashmap
        HashMap<String, Object> jsonUserDate = (HashMap<String, Object>) jsonUserDateById.get("user_date");
        HashMap<String, Object> jsonUserDetail = (HashMap<String, Object>) jsonUserDateById.get("user_detail");
        HashMap<String, Object> jsonUserHistoryDate = (HashMap<String, Object>) jsonUserDateById.get("user_history_date");
        HashMap<String, Object> jsonUserHistoryDetail = (HashMap<String, Object>) jsonUserDateById.get("user_history_detail");

        if (jsonUserDate == null) {
            jsonUserDate = new HashMap<>();
        }

        if (jsonUserDetail == null) {
            jsonUserDetail = new HashMap<>();
        }

        if (jsonUserHistoryDate == null) {
            jsonUserHistoryDate = new HashMap<>();
        }

        if (jsonUserHistoryDetail == null) {
            jsonUserHistoryDetail = new HashMap<>();
        }

        // jsonUserDate
        // check date same, if same date write uuid to under the user_date
        String dateString = date.getText().toString();
        Set<String> keys = jsonUserDate.keySet(); //get all keys
        if (keys.size() > 0) {

            boolean newguy = true;
            for (String i : keys) {
                boolean result = i.equals(dateString);
                if (result) {
                    ArrayList<String> dateMap = (ArrayList<String>) jsonUserDate.get(dateString);
                    int dateMapCount = dateMap.size();
                    dateMap.add(dateMapCount, uuid);
                    newguy = false;
                    break;
                }
            }

            if (newguy) {
                ArrayList<String> dateMap = new ArrayList<>();
                dateMap.add(uuid);
                jsonUserDate.put(dateString, dateMap);
            }

        } else {
            ArrayList<String> dateMap = new ArrayList<>();
            dateMap.add(uuid);
            jsonUserDate.put(dateString, dateMap);
        }

        // jsonUserHistoryDate
        // check uuid same, if same uuid write time to under the user_history_date
        Set<String> historyDateKeys = jsonUserHistoryDate.keySet(); //get all keys
        if (historyDateKeys.size() > 0) {
            for (String i : historyDateKeys) {
                boolean result = i.equals(uuid);
                if (result) {
                    ArrayList<String> dataList = (ArrayList<String>) jsonUserHistoryDate.get(uuid);
                    int count = dataList.size();
                    Date currentTime = Calendar.getInstance().getTime();
                    dataList.add(count, currentTime.toString());
                } else {
                    ArrayList<String> dataList = new ArrayList<>();
                    Date currentTime = Calendar.getInstance().getTime();
                    dataList.add(currentTime.toString());
                    jsonUserHistoryDate.put(uuid, dataList);
                }
                break;
            }
        } else {
            ArrayList<String> dataList = new ArrayList<>();
            Date currentTime = Calendar.getInstance().getTime();
            dataList.add(currentTime.toString());
            jsonUserHistoryDate.put(uuid, dataList);
        }

        // jsonUserHistoryDetail
        // check uuid same, if same uuid write time to under the user_history_detail
        Set<String> historyDetailKeys = jsonUserHistoryDetail.keySet(); //get all keys
        if (historyDetailKeys.size() > 0) {
            for (String i : historyDetailKeys) {
                boolean result = i.equals(uuid);
                if (result) {
                    ArrayList<String> dataList = (ArrayList<String>) jsonUserHistoryDetail.get(uuid);
                    int count = dataList.size();
                    String history_description = "Modify_test1234567890";
                    dataList.add(count, history_description);
                } else {
                    ArrayList<String> dataList = new ArrayList<>();
                    String history_description = "New_Row_test1234567890";
                    dataList.add(history_description);
                    jsonUserHistoryDetail.put(uuid, dataList);
                }
                break;
            }
        } else {
            ArrayList<String> dataList = new ArrayList<>();
            String history_description = "New_Row_test1234567890";
            dataList.add(history_description);
            jsonUserHistoryDetail.put(uuid, dataList);
        }


        String nameString = name.getText().toString();
        String descriptionString = description.getText().toString();
        JE_Detail user_detail = new JE_Detail(uuid, nameString, descriptionString, dateString);
        jsonUserDetail.put(uuid, user_detail);

        mReference.child(userId).child("user_date").setValue(jsonUserDate);
        mReference.child(userId).child("user_detail").setValue(jsonUserDetail);
        mReference.child(userId).child("user_history_date").setValue(jsonUserHistoryDate);
        mReference.child(userId).child("user_history_detail").setValue(jsonUserHistoryDetail);


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(user_detail);
            out.flush();
            byte[] itemBytes = bos.toByteArray();
            System.out.println("itemBytes => " + itemBytes);
            Intent intent = new Intent();
            intent.putExtra("itemBytes", itemBytes);
            setResult(RESULT_OK, intent);
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}




