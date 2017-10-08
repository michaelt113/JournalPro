package sdp.journalpro;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class JE_Entry_Home extends JE_Base_Activity implements View.OnClickListener {

    // Layout variables
    Button modifyBtn;
    Button historyBtn;
    boolean isModify = false;

    // Layout text variables
    EditText name;
    EditText date;
    EditText description;

    // User ID for Firebase
    String uuid;

    // Firebase initiation and authentication var
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    // ?
    String old_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.je_entry_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize and connect to FireBase Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        // Modify Entry button
        modifyBtn = findViewById(R.id.entry_home_modify_button);
        modifyBtn.setOnClickListener(this);

        // View history button
        historyBtn = findViewById(R.id.entry_home_history_button);
        historyBtn.setOnClickListener(this);

        // Edit entry title
        Intent intent = getIntent();
        String _name = intent.getStringExtra("name");
        name = findViewById(R.id.entry_home_name_edit);
        name.setText(_name);

        // update date
        String _date = intent.getStringExtra("date");
        date = findViewById(R.id.entry_home_date_edit);
        date.setText(_date);
        date.setFocusable(false);
        date.setOnClickListener(this);

        // Edit entry content
        String _description = intent.getStringExtra("description");
        description = findViewById(R.id.entry_home_description_edit);
        description.setText(_description);

        uuid = intent.getStringExtra("uuid");

        intoModify(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //go back to je_main_home activity and give up the new page
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    // Click handler
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.entry_home_modify_button) {
            showProgressDialog();
            if (modifyBtn.getText() == "Done") {
                // when taped Done btn update data by the uuid of row
                updateDataBy(uuid);
                intoModify(false);
                modifyBtn.setText("Modify");
            } else {
                old_date = date.getText().toString();
                intoModify(true);
                modifyBtn.setText("Done");
            }
            hideProgressDialog();
        }

        if (i == R.id.entry_home_date_edit) {
            intoCalendarActivity();
        }

        if (i == R.id.entry_home_history_button) {
            Intent intent = new Intent();
            intent.setClass(this, JE_Entry_History.class);
            intent.putExtra("uuid", uuid);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                date.setText(strEditText);
            }
        }
    }

    private void intoModify(boolean isOpen) {
        if (isOpen) {
            isModify = true;
            name.setFocusableInTouchMode(true);
            description.setFocusableInTouchMode(true);
        } else {
            isModify = false;
            name.setFocusable(isOpen);
            description.setFocusable(isOpen);
        }
    }

    // Open calendar activity
    private void intoCalendarActivity() {
        if (isModify) {
            Intent intent = new Intent(this, JE_Calendar_Activity.class);
            startActivityForResult(intent, 123);
        }
    }

    private void updateDataBy(String uuid) {
        Toast.makeText(this.getApplicationContext(), "You update data by uuid of row" + uuid, Toast.LENGTH_SHORT).show();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            writeDataOfUser(user.getUid(), uuid);
        } else {
            Toast.makeText(this.getApplicationContext(), "Your account needs to be verified", Toast.LENGTH_SHORT).show();
        }
    }

    // Write user data to firebase (update)
    private void writeDataOfUser(String userId, String uuid) {

        // Generate user variables
        final String dateString = date.getText().toString();
        final String _uuid = uuid;
        final String _userId = userId;
        mReference.child(userId).child("user_date").addListenerForSingleValueEvent(new ValueEventListener() {

            // Generate user hashmap and data for firebase
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> jsonUserDate = (HashMap<String, Object>) dataSnapshot.getValue();

                if (jsonUserDate == null) {
                    jsonUserDate = new HashMap<>();
                }

                Set<String> keyset = jsonUserDate.keySet();
                ArrayList<String> dateMap = new ArrayList<>();
                if (keyset.size() > 0) {
                    for (String s : keyset) {
                        if (s.equals(old_date)) {
                            System.out.println("s" + s);
                            System.out.println("old_date" + old_date);
                            ArrayList<String> _dateMap = (ArrayList<String>) jsonUserDate.get(s);
                            if (_dateMap.size() > 0) {
                                for (int i = 0; i < _dateMap.size(); i++) {
                                    if (_dateMap.get(i).equals(_uuid)) {
                                        _dateMap.remove(i);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    for (String s : keyset) {
                        if (s.equals(dateString)) {
                            dateMap = (ArrayList<String>) jsonUserDate.get(s);
                            break;
                        }
                    }

                    dateMap.add(_uuid);
                    System.out.println("dateString" + dateString);
                    jsonUserDate.put(dateString, dateMap);
                    mReference.child(_userId).child("user_date").setValue(jsonUserDate);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mReference.child(userId).child("user_history_date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // jsonUserHistoryDate
                // check uuid same, if same uuid write time to under the user_history_date
                HashMap<String, Object> jsonUserHistoryDate = (HashMap<String, Object>) dataSnapshot.getValue();
                Set<String> historyDateKeys = jsonUserHistoryDate.keySet(); //get all keys
                if (historyDateKeys.size() > 0) {
                    for (String i : historyDateKeys) {
                        boolean result = i.equals(_uuid);
                        if (result) {
                            ArrayList<String> dataList = (ArrayList<String>) jsonUserHistoryDate.get(_uuid);
                            int count = dataList.size();
                            Date currentTime = Calendar.getInstance().getTime();
                            dataList.add(count, currentTime.toString());
                            break;
                        }
                    }

                    mReference.child(_userId).child("user_history_date").setValue(jsonUserHistoryDate);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mReference.child(userId).child("user_history_detail").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // jsonUserHistoryDetail
                // check uuid same, if same uuid write time to under the user_history_detail
                HashMap<String, Object> jsonUserHistoryDetail = (HashMap<String, Object>) dataSnapshot.getValue();
                Set<String> historyDetailKeys = jsonUserHistoryDetail.keySet(); //get all keys
                if (historyDetailKeys.size() > 0) {
                    for (String i : historyDetailKeys) {
                        boolean result = i.equals(_uuid);
                        if (result) {
                            ArrayList<String> dataList = (ArrayList<String>) jsonUserHistoryDetail.get(_uuid);
                            int count = dataList.size();
                            String history_description = "Modify_test1234567890";
                            dataList.add(count, history_description);
                            break;
                        }
                    }

                    mReference.child(_userId).child("user_history_detail").setValue(jsonUserHistoryDetail);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mReference.child(userId).child("user_detail").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> jsonUserDetail = (HashMap<String, Object>) dataSnapshot.getValue();

                Set<String> keyset = jsonUserDetail.keySet();
                if (keyset.size() > 0) {
                    for (String s : keyset) {
                        if (s.equals(_uuid)) {
                            jsonUserDetail.remove(s);
                            break;
                        }
                    }
                    String nameString = name.getText().toString();
                    String descriptionString = description.getText().toString();
                    JE_Detail user_detail = new JE_Detail(_uuid, nameString, descriptionString, dateString);
                    jsonUserDetail.put(_uuid, user_detail);
                    mReference.child(_userId).child("user_detail").setValue(jsonUserDetail);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    try {
                        ObjectOutput out = new ObjectOutputStream(bos);
                        out.writeObject(user_detail);
                        out.flush();
                        byte[] itemBytes = bos.toByteArray();
                        System.out.println("itemBytes => " + itemBytes);
                        Intent forntIntent = getIntent();
                        int row = forntIntent.getIntExtra("row", 0);
                        Intent intent = new Intent();
                        intent.putExtra("itemBytes", itemBytes);
                        intent.putExtra("row", row);
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}