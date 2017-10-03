package sdp.journalpro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class JE_Entry_History extends JE_Base_Activity {

    // Entries list layout items
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    JE_Entry_History_Adapter adapter;

    HashMap<String, Object> dataset;

    // vars for firebase database & authentication
    private FirebaseAuth mAuth;
    private FirebaseUser mVerifiedUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.je_entry_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mVerifiedUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        // recylcerview of main
        recyclerView = findViewById(R.id.history_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        dataset = new HashMap<>();

        adapter = new JE_Entry_History_Adapter(dataset);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        final String uuid = intent.getStringExtra("uuid");

        mReference.child(mAuth.getCurrentUser().getUid()).child("user_history_date").child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> historyDate = (ArrayList<String>) dataSnapshot.getValue();

                mReference.child(mAuth.getCurrentUser().getUid()).child("user_history_detail").child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> historyDetail = (ArrayList<String>) dataSnapshot.getValue();
                        int dateCount = historyDate.size(); //get all keys
                        int detailCount = historyDetail.size(); //get all keys
                        if (dateCount == detailCount) {
                            for (int i = 0; i < dateCount; i++) {
//                                System.out.println("date_value => " + historyDate.get(i));
//                                System.out.println("detail_value => " + historyDetail.get(i));
                                String time = historyDate.get(i);
                                String description = historyDetail.get(i);
                                HashMap<String, String> temp = new HashMap<>();
                                temp.put("time", time);
                                temp.put("description", description);
                                dataset.put(String.valueOf(i), temp);
                            }

                            System.out.println("dataset => " + dataset);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
