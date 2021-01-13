package com.example.shoppingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    /**
     * Adapter for item class in order to pass through to rvList
     *
     */

    //placeholder data container, could change in future
    ArrayList<Item> items;

    public ItemAdapter(Context context, ArrayList<Item> list) {
        items=list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvItemName,tvItemDesc, tvItemPrice;
        ImageView ivItemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemDesc = itemView.findViewById(R.id.tvItemDesc);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);

            ivItemImage = itemView.findViewById(R.id.ivItemImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout,parent,false);
        return new ItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.tvItemName.setText(items.get(position).getName());
        holder.tvItemDesc.setText(items.get(position).getDescription()); //optional, maybe remove later
        holder.tvItemPrice.setText(items.get(position).getPrice());

        //TODO if we use images, set the image resource here
    }



    @Override
    public int getItemCount() {
        return items.size();
    }
}
