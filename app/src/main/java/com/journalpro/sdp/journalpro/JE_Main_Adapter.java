package com.journalpro.sdp.journalpro;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

class JE_Main_Adapter extends RecyclerView.Adapter<JE_Main_Adapter.ViewHolder> {

    Context mContext;

    // save origin data
    private ArrayList<Object> dataset;

    // save origin data and when search some data will return to dataset
    private ArrayList<Object> originally_dataset;

    // check which model main page
    private AdapterModel model;

    // save removed data
    private ArrayList<Object> removedDataset = new ArrayList<>();

    // save hidden data
    private ArrayList<Object> hiddenDataset = new ArrayList<>();

    enum AdapterModel {

        HOME("HOME", 0),
        DELETE("DELETE", 1),
        HIDE("HIDE", 2);

        private String stringValue;
        private int intValue;

        AdapterModel(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        ImageView image;
        TextView name;
        TextView description;
        TextView date;
        ConstraintLayout contentLayout;

        ViewHolder(View item) {
            super(item);
            image = item.findViewById(R.id.row_photo_image);
            name = item.findViewById(R.id.row_name);
            description = item.findViewById(R.id.row_description);
            date = item.findViewById(R.id.row_date);
            contentLayout = item.findViewById(R.id.row_content_layout);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    JE_Main_Adapter(ArrayList<Object> _dataset, AdapterModel model) {
        if (dataset == null) {
            dataset = _dataset;
        }

        if (originally_dataset == null) {
            originally_dataset = new ArrayList<>();
        }

        this.model = model;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public JE_Main_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // create a new view
        Context context = viewGroup.getContext();
        View contactView = LayoutInflater.from(context).inflate(R.layout.je_main_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        viewHolder.context = context;
        mContext = viewHolder.context;
        Log.e("onCreateViewHolder", "456");
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        System.out.println("onBindViewHolder");
        final int row = position;
        System.out.println("position => " + position);

        switch (this.model) {
            case HOME:
                System.out.println("HOME Model");
                if (dataset.size() > 0) {
                    HashMap<String, String> item = (HashMap<String, String>) dataset.get(row);
                    System.out.println("item => " + item);
                    final String _name = item.get("name");
                    final String _description = item.get("description");
                    final String _date = item.get("date");
                    final String _uuid = item.get("uuid");

                    final Context context = holder.context;
                    holder.name.setText(_name);
                    holder.description.setText(_description);
                    holder.date.setText(_date);
                    holder.contentLayout.setBackgroundResource(R.color.clear);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setClass(context, JE_Entry_Home.class);
                            intent.putExtra("name", _name);
                            intent.putExtra("description", _description);
                            intent.putExtra("date", _date);
                            intent.putExtra("uuid", _uuid);
                            intent.putExtra("row", row);
                            Activity activity = scanForActivity(context);
                            activity.startActivityForResult(intent, 6666);
                            Log.d("position", String.valueOf(row));
                        }
                    });
                }
                break;
            case DELETE:
                System.out.println("DELETE Model");
                if (removedDataset.size() > 0) {
                    HashMap<String, String> item = (HashMap<String, String>) removedDataset.get(row);

                    final String _name = item.get("name");
                    final String _description = item.get("description");
                    final String _date = item.get("date");
                    final String _uuid = item.get("uuid");

                    final Context context = holder.context;
                    holder.name.setText(_name);
                    holder.description.setText(_description);
                    holder.date.setText(_date);
                    holder.contentLayout.setBackgroundResource(R.color.light_red);
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = new Intent();
//                            intent.setClass(context, JE_Entry_Home.class);
//                            intent.putExtra("name", _name);
//                            intent.putExtra("description", _description);
//                            intent.putExtra("date", _date);
//                            intent.putExtra("uuid", _uuid);
//                            context.startActivity(intent);
//                            Log.d("position", String.valueOf(row));
//                        }
//                    });
                }
                break;
            case HIDE:
                System.out.println("HIDE Model");
                if (hiddenDataset.size() > 0) {
                    HashMap<String, String> item = (HashMap<String, String>) hiddenDataset.get(row);
                    System.out.println("item => " + item);
                    final String _name = item.get("name");
                    final String _description = item.get("description");
                    final String _date = item.get("date");
                    final String _uuid = item.get("uuid");

                    final Context context = holder.context;
                    holder.name.setText(_name);
                    holder.description.setText(_description);
                    holder.date.setText(_date);
                    holder.contentLayout.setBackgroundResource(R.color.colorPrimary);
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = new Intent();
//                            intent.setClass(context, JE_Entry_Home.class);
//                            intent.putExtra("name", _name);
//                            intent.putExtra("description", _description);
//                            intent.putExtra("date", _date);
//                            intent.putExtra("uuid", _uuid);
//                            context.startActivity(intent);
//                            Log.d("position", String.valueOf(row));
//                        }
//                    });
                }
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        System.out.println("getItemCount");
        switch (this.model) {
            case HOME:
                System.out.println("HOME Model");
                return dataset.size();
            case DELETE:
                System.out.println("DELETE Model");
                return removedDataset.size();
            case HIDE:
                System.out.println("HIDE Model");
                return hiddenDataset.size();
            default:
                return 0;
        }
    }

    int getHOMEDatasetCount() {
        return dataset.size();
    }

    int getDELETEDatasetCount() {
        return removedDataset.size();
    }

    int getHIDEDatasetCount() {
        return hiddenDataset.size();
    }


    void changeModel(AdapterModel model) {
        this.model = model;
    }

    void remove(int position) {
        System.out.println("position => " + position);
        System.out.println("REMOVE BEFORE SIZE => " + dataset.size());

        HashMap<String, String> item = (HashMap<String, String>) dataset.get(position);
        String uuid = item.get("uuid");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        removeRowDataOfUser(user.getUid(), uuid, position);
    }

    void hidden(int position) {
        System.out.println("position => " + position);
        System.out.println("HIDDEN BEFORE SIZE => " + dataset.size());
        notifyItemRemoved(position);

        //add hided data to hiddenDataset
        hiddenDataset.add(dataset.get(position));

        //save hidden dataset at local
        Activity activity = scanForActivity(mContext);
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(hiddenDataset);
        System.out.print("json => " + json);
        editor.putString("HIDDENGSON", json);
        editor.commit();

        //remove data from dataset
        dataset.remove(position);
        System.out.println("HIDDEN AFTER SIZE => " + dataset.size());
        resetOringinally_Dataset();
    }

    void resetOringinally_Dataset() {

        if (model == AdapterModel.HOME) {
            originally_dataset.clear();
            originally_dataset.addAll(dataset);
        }

        if (model == AdapterModel.DELETE) {
            removedDataset.clear();
            removedDataset.addAll(dataset);
        }

        System.out.println("originally_dataset => " + originally_dataset.size());
        notifyDataSetChanged();
    }

    void resetDataset() {
        dataset.clear();
        dataset.addAll(originally_dataset);
        System.out.println("originally_dataset => " + originally_dataset.size());
        notifyDataSetChanged();
    }

    // Filter Class
    void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        dataset.clear();

        if (charText.length() == 0) {

            //if no any char text to search on search bar, add originally data to dataset
            dataset.addAll(originally_dataset);

        } else {

            for (Object object : originally_dataset) {
                HashMap<String, String> item = (HashMap<String, String>) object;
                final String _name = item.get("name");
                if (_name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    System.out.println("item => " + item);
                    dataset.add(item);
                    System.out.println("dataset added item => " + dataset);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Filter Class
    void filterDate(ArrayList<String> selectedDates) {

        if (selectedDates.size() > 0) {

            dataset.clear();
            for (String date : selectedDates) {
                System.out.println("filter date1 => " + date);
                date = date.toLowerCase(Locale.getDefault());
                System.out.println("filter date2 => " + date);
                for (Object object : originally_dataset) {
                    HashMap<String, String> item = (HashMap<String, String>) object;
                    final String _date = item.get("date");
                    System.out.println("filter date3 => " + _date);
                    if (_date.toLowerCase(Locale.getDefault()).contains(date)) {
                        System.out.println("item => " + item);
                        dataset.add(item);
                        System.out.println("dataset added item => " + dataset);
                    }
                }
            }

            System.out.println("total dataset => " + dataset);
        }

        notifyDataSetChanged();
    }

    private static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }

    private void removeRowDataOfUser(String userId, String uuid, final int position) {

        final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference mReference = mDatabase.getReference();

        final String _userId = userId;
        final String _uuid = uuid;

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
                            String history_description = "Remove_99999";
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

                Object removed_user_detail = new Object();
                Set<String> keyset = jsonUserDetail.keySet();
                if (keyset.size() > 0) {
                    for (String s : keyset) {
                        if (s.equals(_uuid)) {
                            removed_user_detail = jsonUserDetail.get(s);
                            jsonUserDetail.remove(s);
                            break;
                        }
                    }

                    mReference.child(_userId).child("user_detail").setValue(jsonUserDetail);

                    final Object removed_detail = removed_user_detail;
                    mReference.child(_userId).child("user_removed").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, Object> removedJsonUserDetail = (HashMap<String, Object>) dataSnapshot.getValue();

                            if (removedJsonUserDetail == null) {
                                // initialize
                                removedJsonUserDetail = new HashMap<String, Object>();
                            }

                            removedJsonUserDetail.put(_uuid, removed_detail);

                            mReference.child(_userId).child("user_removed").setValue(removedJsonUserDetail);

                            notifyItemRemoved(position);

                            //add removed data to removedDataset
                            removedDataset.add(dataset.get(position));

                            //remove data from dataset
                            dataset.remove(position);

//                            System.out.println("REMOVE AFTER SIZE => " + dataset.size());
                            resetOringinally_Dataset();

//                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                            try {
//                                ObjectOutput out = new ObjectOutputStream(bos);
//                                out.writeObject(removed_user_detail);
//                                out.flush();
//                                byte[] itemBytes = bos.toByteArray();
//                                System.out.println("itemBytes => " + itemBytes);
//                                Intent forntIntent = getIntent();
//                                int row = forntIntent.getIntExtra("row", 0);
//                                Intent intent = new Intent();
//                                intent.putExtra("itemBytes", itemBytes);
//                                intent.putExtra("row", row);
//        //                        setResult(RESULT_OK, intent);
//        //                        finish();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            } finally {
//                                try {
//                                    bos.close();
//                                } catch (IOException ex) {
//                                    ex.printStackTrace();
//                                }
//                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

//    private String[] removeNullFrom(String[] arrays) {
//        String[] finalArray;
//
//        List<String> list = new ArrayList<>();
//
//        for(String s : arrays) {
//            if(s != null && s.length() > 0) {
//                list.add(s);
//            }
//        }
//
//        finalArray = list.toArray(new String[list.size()]);
//        return  finalArray;
//    }

}
