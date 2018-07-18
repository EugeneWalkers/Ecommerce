package ew.ecommerce.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ew.ecommerce.R;
import ew.ecommerce.model.Product;
import ew.ecommerce.utilities.Downloader;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.ViewHolder> {

    private static ArrayList<Product> products;
    private static ClickerListener clicker;
    private static Boolean isCeller;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            LinearLayout firstLevel = itemView.findViewById(R.id.firstLevel);
            ConstraintLayout secondPartOfFirstLevel = firstLevel.findViewById(R.id.textlevel);
            ConstraintLayout secondLevel = itemView.findViewById(R.id.secondLevel);
            Button buy = secondLevel.findViewById(R.id.buy);
            secondPartOfFirstLevel.setOnClickListener(this);
            ImageView photo = firstLevel.findViewById(R.id.photo);
            photo.setOnClickListener(this);
            secondPartOfFirstLevel.setOnClickListener(this);
            buy.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clicker.onItemClick(view, getAdapterPosition());
        }

    }

    public interface ClickerListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(ClickerListener clickerListener) {
        ProductsListAdapter.clicker = clickerListener;
    }

    public ProductsListAdapter(ArrayList<Product> products, Boolean isCeller) {
        ProductsListAdapter.products = products;
        if (isCeller == null){
            ProductsListAdapter.isCeller = false;
        }
        else{
            ProductsListAdapter.isCeller = isCeller;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        View view = holder.itemView;
        LinearLayout firstLevel = view.findViewById(R.id.firstLevel);
        ConstraintLayout secondPartOfFirstLevel = firstLevel.findViewById(R.id.textlevel);
        ConstraintLayout secondLevel = view.findViewById(R.id.secondLevel);
        ImageView photo = firstLevel.findViewById(R.id.photo);
        Product product = products.get(position);
        new Downloader(photo, true).execute(product.getImageUrl());
        ((TextView) secondPartOfFirstLevel.findViewById(R.id.name)).setText(product.getName());
        ((TextView) secondPartOfFirstLevel.findViewById(R.id.cost)).setText(product.getCost());
        ((TextView) secondPartOfFirstLevel.findViewById(R.id.category)).setText(product.getCategory());
        ((TextView) secondLevel.findViewById(R.id.description)).setText(product.getDescription());
        if (isCeller){
            ((Button)secondLevel.findViewById(R.id.buy)).setText(R.string.edit);
        }

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}
