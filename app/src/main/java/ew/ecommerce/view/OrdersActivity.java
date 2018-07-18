package ew.ecommerce.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import ew.ecommerce.R;
import ew.ecommerce.adapters.OrdersListAdapter;
import ew.ecommerce.model.Order;
import ew.ecommerce.viewmodel.OrdersViewModel;

public class OrdersActivity extends AppCompatActivity {

    public static final String LOADING_DIALOG_TAG = "loading_dialog_tag";

    private OrdersViewModel viewModel;
    private OrdersListAdapter orderAdapter;
    private RecyclerView recyclerView;
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        viewModel = ViewModelProviders.of(this).get(OrdersViewModel.class);
        dialog = LoadingDialog.newInstance(this);
        setRecyclerView();
        Toolbar toolbar = findViewById(R.id.my_toolbar_orders);
        toolbar.setTitle("Orders");
        setSupportActionBar(toolbar);
        setObservers();
        viewModel.readOrdersFromBase();
    }

    private void setRecyclerView(){
        recyclerView = findViewById(R.id.recycler_view_orders);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_order_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.refresh_orders).setTitle(null);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_orders:
                viewModel.readOrdersFromBase();
                break;
            default:
                return false;
        }
        return true;
    }

    private void setObservers(){
        Observer<ArrayList<Order>> ordersObserver = (ArrayList<Order> orders) -> {
            if (orders != null) {
                orderAdapter = viewModel.getOrderAdapterInstance();
                orderAdapter.setOnItemClickListener((view, position) -> {
                    switch (view.getId()) {
                        case R.id.delete:
                            viewModel.deleteOrder(orders.get(position).getOrderName());
                            break;
                        default:
                            break;

                    }
                });
                recyclerView.setAdapter(orderAdapter);
            }
        };
        viewModel.getOrders().observe(this, ordersObserver);
        Observer<ArrayList<String>> orderListObserver = orders -> {
            for (String order : orders) {
                viewModel.readAndWriteOrder(order);
            }
        };
        viewModel.getOrdersList().observe(this, orderListObserver);
        Observer<Boolean> isDialogShow = aBoolean -> {
            if (aBoolean) {
                dialog.show(getSupportFragmentManager(), LOADING_DIALOG_TAG);
            } else {
                dialog.dismiss();
            }
        };
        viewModel.getIsDialogShow().observe(this, isDialogShow);
    }
}
