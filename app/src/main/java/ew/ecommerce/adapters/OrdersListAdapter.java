package ew.ecommerce.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ew.ecommerce.R;
import ew.ecommerce.model.Order;

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {

    private static ArrayList<Order> orders;
    private static OrdersListAdapter.ClickerListener clicker;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            Button deleter = itemView.findViewById(R.id.delete);
            deleter.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clicker.onItemClick(view, getAdapterPosition());
        }



    }

    public interface ClickerListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OrdersListAdapter.ClickerListener clickerListener) {
        OrdersListAdapter.clicker = clickerListener;
    }

    public OrdersListAdapter(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrdersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        OrdersListAdapter.ViewHolder viewHolder = new OrdersListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersListAdapter.ViewHolder holder, int position) {
        View view = holder.itemView;
        ((TextView)view.findViewById(R.id.product_name)).setText(orders.get(position).getProduct());
        ((TextView)view.findViewById(R.id.email_view)).setText(orders.get(position).getEmail());
        ((TextView)view.findViewById(R.id.customer_name)).setText(orders.get(position).getName());
        ((TextView)view.findViewById(R.id.phone_view)).setText(orders.get(position).getPhone());
        ((TextView)view.findViewById(R.id.comment_view)).setText(orders.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }
}
