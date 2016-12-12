package com.tenilodev.lecturermaps.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tenilodev.lecturermaps.R;
import com.tenilodev.lecturermaps.model.Dosen;

import java.util.List;

public class DosenAdapter extends RecyclerView.Adapter<DosenAdapter.ViewHolder> {

    private List<Dosen> list;
    private ClickListener clickListener;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        TextView tv_title, tv_desc;
        View v;


        ViewHolder(View v) {

            super(v);
            this.v = v;

            tv_title = (TextView)v.findViewById(R.id.text_title_dosen);
            tv_desc = (TextView)v.findViewById(R.id.text_desc_dosen);


        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DosenAdapter(Context context, List<Dosen> myDataset) {
        this.context = context;
        list = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dosen, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tv_title.setText(list.get(position).getNAMA());
        holder.tv_desc.setText(list.get(position).getJABATANKADEMIK());


        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null)
                    clickListener.onClick(view, position);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ClickListener {
        void onClick(View v, int position);
    }

    public void setClickListener(ClickListener listener) {
        this.clickListener = listener;
    }

}