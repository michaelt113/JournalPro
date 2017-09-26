package com.journalpro.sdp.journalpro;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

class JE_Entry_History_Adapter extends RecyclerView.Adapter<JE_Entry_History_Adapter.ViewHolder> {

    private HashMap<String, Object> dataset;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView currentTime;
        TextView description;

        ViewHolder(View item) {
            super(item);
            this.currentTime = item.findViewById(R.id.history_item_time);
            this.description = item.findViewById(R.id.history_item_description);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    JE_Entry_History_Adapter(HashMap<String, Object> _dataset) {
        dataset = _dataset;
    }

    @Override
    public JE_Entry_History_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // create a new view
        Context context = viewGroup.getContext();
        View contactView = LayoutInflater.from(context).inflate(R.layout.je_entry_history_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        Log.e("onCreateViewHolder", "456");
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(JE_Entry_History_Adapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (dataset.size() > 0) {
            HashMap<String, String> item = (HashMap<String, String>) dataset.get(String.valueOf(position));
            System.out.println(item);
            final String _time = item.get("time");
            final String _description = item.get("description");
//            final String _date = item.get("date");
//            final String _uuid = item.get("uuid");
//
//            final Context context = holder.context;
            holder.currentTime.setText(_time);
            holder.description.setText(_description);
//            holder.date.setText(_date);
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent();
//                    intent.setClass(context, JE_Entry_Home.class);
//                    intent.putExtra("name", _name);
//                    intent.putExtra("description", _description);
//                    intent.putExtra("date", _date);
//                    intent.putExtra("uuid", _uuid);
//                    context.startActivity(intent);
//                    Log.d("position", String.valueOf(row));
//                }
//            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }


//    void remove(int position) {
//        notifyItemRemoved(position);
//        dataset.remove(String.valueOf(position));
//        System.out.println(dataset.toString());
//        notifyDataSetChanged();
//    }
}
