package sdp.journalpro;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static android.widget.Toast.makeText;

public class JE_Main_Home
        extends
        JE_Base_Activity
        implements
        View.OnClickListener,
        FirebaseAuth.AuthStateListener,
        AdapterView.OnItemSelectedListener,
        SearchView.OnQueryTextListener {

    private static final String TAG = "JE_Main_Home";

    ItemTouchHelper.SimpleCallback simpleCallback;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;

    ArrayList<Object> dataset;
    String[] lunch;

    HashMap<String, Object> jsonData;
    JE_Main_Adapter adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    SearchView editsearch;

    FloatingActionButton calendar;
    boolean ChooseCalendar = false;

    JE_Main_Adapter.AdapterModel adapterModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.je_main_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // new button of entry
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        // new button of entry
        calendar = findViewById(R.id.calendarbtn);
        calendar.setOnClickListener(this);

        // recylcerview of main
        recyclerView = findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        // specify an adapter (see also next example)
        dataset = new ArrayList<>();
        adapterModel = JE_Main_Adapter.AdapterModel.HOME;
        adapter = new JE_Main_Adapter(dataset, adapterModel);
        recyclerView.setAdapter(adapter);


        // set swipe gesture in recylcerview
        simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                switch (adapterModel) {
                    case HOME:
                        return true;
                    case HIDE:
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                final JE_Main_Adapter _adapter = (JE_Main_Adapter) recyclerView.getAdapter();

                AlertDialog.Builder builder = new AlertDialog.Builder(JE_Main_Home.this);

                if (adapter.getModel() == JE_Main_Adapter.AdapterModel.HOME)
                {
                    switch (direction) {
                        case ItemTouchHelper.LEFT:

                            //alert for confirm to delete
                            builder.setMessage("Are you sure to delete?");    //set message

                            builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() { //when click on DELETE
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = viewHolder.getAdapterPosition();
                                    _adapter.remove(position);

                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = viewHolder.getAdapterPosition();
                                    _adapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                                    _adapter.notifyItemRangeChanged(position, _adapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                                }
                            }).show();  //show alert dialog

                            break;
                        case ItemTouchHelper.RIGHT:

//                            AlertDialog.Builder builder_right = new AlertDialog.Builder(JE_Main_Home.this); //alert for confirm to delete
                            builder.setMessage("Are you sure to hide?");    //set message

//                            final JE_Main_Adapter adapter_right = (JE_Main_Adapter) recyclerView.getAdapter();

                            builder.setPositiveButton("HIDE", new DialogInterface.OnClickListener() { //when click on DELETE
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = viewHolder.getAdapterPosition();
                                    _adapter.hidden(position);

                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = viewHolder.getAdapterPosition();
                                    _adapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                                    _adapter.notifyItemRangeChanged(position, _adapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                                }
                            }).show();  //show alert dialog

                            break;
                    }
                }

                if (adapter.getModel() == JE_Main_Adapter.AdapterModel.HIDE) {
                    //alert for confirm to delete
                    builder.setMessage("Are you sure to Unhide?");    //set message
                    builder.setPositiveButton("UNHIDEEN", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int position = viewHolder.getAdapterPosition();
                            _adapter.UnHidden(position);

                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int position = viewHolder.getAdapterPosition();
                            _adapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                            _adapter.notifyItemRangeChanged(position, _adapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                        }
                    }).show();  //show alert dialog
                    return;
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView); //set swipe to recylcerview

        // the spinner is drop menu on top bar
        Spinner spinner = findViewById(R.id.spinner);
        lunch = new String[]{"Home", "Hidden", "Deleted", "Sing Out"};
        ArrayAdapter<String> lunchList = new ArrayAdapter<>(this, R.layout.je_spinner_dropdown_item, lunch);
        spinner.setAdapter(lunchList);
        spinner.setOnItemSelectedListener(this);

        // Locate the EditText in listview_main.xml
        editsearch = findViewById(R.id.searchbar);
        editsearch.setOnQueryTextListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Gain Verified user initialize Firebase User
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        switch (position) {
            case 0:
                adapterModel = JE_Main_Adapter.AdapterModel.HOME;
                adapter.changeModel(adapterModel);
                if (adapter.getItemCount() > 0 || adapter.getHIDEDatasetCount() > 0) {
                    adapter.resetDataset();
                } else {
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            // add Listener for read from the database once
                            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                // when you implement add linstener for read data base, the result came back here
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // to get all data form database
                                    getAllData(dataSnapshot);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            makeText(this.getApplicationContext(), "Email Verified", Toast.LENGTH_SHORT).show();
                        } else {
                            signOut();
                            makeText(this.getApplicationContext(), "Not Email Verified", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case 1:
                adapterModel = JE_Main_Adapter.AdapterModel.HIDE;
                adapter.changeModel(adapterModel);
                adapter.resetDataset();
                break;
            case 2:
                adapterModel = JE_Main_Adapter.AdapterModel.DELETE;
                adapter.changeModel(adapterModel);
                if (adapter.getItemCount() > 0) {
                    adapter.resetDataset();
                } else {
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            // add Listener for read from the database once
                            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                // when you implement add linstener for read data base, the result came back here
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // to get all data form database
                                    getAllData(dataSnapshot);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
                break;
            case 3:
                if (user != null) {
                    signOut();
                }
                break;
            default:
                break;
        }
        Toast.makeText(getApplicationContext(), "You selected the page 「" + lunch[position] + "」", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        // taped add button
        if (i == R.id.fab) {

            // initialize Intent
            Intent intent = new Intent();
            intent.setClass(this, JE_New_Home.class);

            // send HashMap to JE_New_Home
            intent.putExtra("JSONDATA", jsonData);
            startActivityForResult(intent, 9999);
        }


        // taped calendar btn
        if (i == R.id.calendarbtn) {
            if (!ChooseCalendar) {
                Intent intent = new Intent(this, JE_Calendar_Activity.class);
                intent.putExtra("SearchDate", "SearchDate");
                startActivityForResult(intent, 5555);
            } else {
                ViewCompat.setBackgroundTintList(calendar, ColorStateList.valueOf(Color.rgb(8, 78, 149)));
                Toast.makeText(this, "Data All Reseted", Toast.LENGTH_LONG).show();
                adapter.changeModel(JE_Main_Adapter.AdapterModel.HOME);
                adapter.resetDataset();
                ChooseCalendar = false;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5555) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> stringDates = data.getStringArrayListExtra("dates");
                System.out.println("date => " + stringDates);
                adapter.filterDate(stringDates);
                ViewCompat.setBackgroundTintList(calendar, ColorStateList.valueOf(Color.rgb(255, 68, 68)));
                ChooseCalendar = true;
            }
        }


        if (requestCode == 6666) {
            if (resultCode == RESULT_OK) {
                System.out.println("RESULT_OK 6666");
                byte[] itemBytes = data.getByteArrayExtra("itemBytes");
                ByteArrayInputStream bis = new ByteArrayInputStream(itemBytes);
                ObjectInput input = null;
                try {
                    input = new ObjectInputStream(bis);
                    JE_Detail user_detail = (JE_Detail) input.readObject();
                    System.out.println("item => " + user_detail);
                    HashMap<String, String> item = user_detail.passingToHashMap();
                    int row = data.getIntExtra("row", 0);
                    dataset.set(row, item);
                    adapter.resetOringinally_Dataset();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                    } catch (IOException ex) {
                        // ignore close exception
                    }
                }
            }
        }


        if (requestCode == 9999) {
            if (resultCode == RESULT_OK) {
                System.out.println("RESULT_OK 9999");
                byte[] itemBytes = data.getByteArrayExtra("itemBytes");
                ByteArrayInputStream bis = new ByteArrayInputStream(itemBytes);
                ObjectInput input = null;
                try {
                    input = new ObjectInputStream(bis);
                    JE_Detail user_detail = (JE_Detail) input.readObject();
                    System.out.println("item => " + user_detail);
                    HashMap<String, String> item = user_detail.passingToHashMap();
                    dataset.add(item);
                    adapter.resetOringinally_Dataset();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                    } catch (IOException ex) {
                        // ignore close exception
                    }
                }
            }
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            // User is signed out
            hideProgressDialog();
            Log.d(TAG, "onAuthStateChanged:signed_out");
            this.finish();
        }
    }

    private void signOut() {
        showProgressDialog();
        mAuth.signOut();
    }

    private void getAllData(DataSnapshot dataSnapshot) {
        jsonData = (HashMap<String, Object>) dataSnapshot.getValue();
        if (jsonData == null) {
            jsonData = new HashMap<>();
        }

        // get data by User Id
        HashMap<String, Object> jsonUserDateById = (HashMap<String, Object>) jsonData.get(mAuth.getCurrentUser().getUid());

        if (jsonUserDateById == null) {
            jsonUserDateById = new HashMap<>();
        }

        HashMap<String, Object> jsonUserDetail = new HashMap<>();
        switch (adapterModel) {
            case HOME:
                // get UserDate and UserDetail of user_detail
                jsonUserDetail = (HashMap<String, Object>) jsonUserDateById.get("user_detail");
                break;
            case HIDE:
                break;
            case DELETE:
                // get UserDate and UserDetail of user_removed
                jsonUserDetail = (HashMap<String, Object>) jsonUserDateById.get("user_removed");
                break;
            default:
                break;
        }

        if (jsonUserDetail == null) {
            jsonUserDetail = new HashMap<>();
        }

        System.out.println("jsonUserDetail.size => " + jsonUserDetail.size());

        if (dataset.size() > 0) {
            dataset.clear();
        }

        Set<String> detailKeys = jsonUserDetail.keySet();
        for (String s : detailKeys) {
            HashMap<String, String> item = (HashMap<String, String>) jsonUserDetail.get(s);
            dataset.add(item);
        }

        if (adapterModel == JE_Main_Adapter.AdapterModel.HOME) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            Gson gson = new Gson();
            String json = sharedPrefs.getString("HIDDENGSON", null);
            System.out.println("json test => " + json);
            Type type = new TypeToken<List<Object>>() {}.getType();
            ArrayList<Object> arrayList = gson.fromJson(json, type);
            System.out.println("arrayList => " + arrayList);
        }

        adapter.resetOringinally_Dataset();
    }
}