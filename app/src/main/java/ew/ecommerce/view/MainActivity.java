package ew.ecommerce.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import ew.ecommerce.R;
import ew.ecommerce.adapters.OrdersListAdapter;
import ew.ecommerce.adapters.ProductsListAdapter;
import ew.ecommerce.model.Order;
import ew.ecommerce.model.Product;
import ew.ecommerce.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    public static final String LOADING_DIALOG_TAG = "loading_dialog_tag";
    public static final String PHOTO_DIALOG_TAG = "photo_dialog_tag";
    public static final String PRODUCT_INFO = "product_info";


    private MainViewModel viewModel;
    private RecyclerView recyclerView;
    private ProductsListAdapter productAdapter;
    private OrdersListAdapter orderAdapter;
    private LoadingDialog dialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        dialog = LoadingDialog.newInstance(this);
        toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Online shop");
        setObservers();
        setSupportActionBar(toolbar);
        setRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.orders).setTitle(null);
        menu.findItem(R.id.key).setTitle(null);
        menu.findItem(R.id.refresh).setTitle(null);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.key:
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Input password:");
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModel.checkPassword(input.getText().toString());
                    }
                });
                alert.show();
                break;
            case R.id.refresh:
                if (viewModel.getIsCeller().getValue() == null || !viewModel.getIsCeller().getValue()) {
                    viewModel.readProductsFromBase();
                } else {
                    viewModel.readOrdersFromBase();
                }
                break;
            case R.id.orders:

                break;
            default:
                return false;
        }
        return true;
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setObservers() {
        Observer<ArrayList<Product>> productsObserver = products -> {
            if (products != null) {

                productAdapter = viewModel.getProductAdapterInstance();
                productAdapter.setOnItemClickListener((View view, int position) -> {
                    switch (view.getId()) {
                        case R.id.photo:
                            PhotoDialog dialogPhoto = PhotoDialog.newInstance();
                            Bundle arguments = new Bundle();
                            arguments.putString(PhotoDialog.URL, products.get(position).getImageUrl());
                            dialogPhoto.setArguments(arguments);
                            dialogPhoto.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
                            dialogPhoto.show(getSupportFragmentManager(), PHOTO_DIALOG_TAG);
                            break;
                        case R.id.textlevel:
                            ConstraintLayout secondLevel = recyclerView.getChildAt(position).findViewById(R.id.secondLevel);
                            if (secondLevel.getVisibility() == ConstraintLayout.GONE) {
                                secondLevel.setVisibility(LinearLayout.VISIBLE);
                            } else {
                                secondLevel.setVisibility(LinearLayout.GONE);
                            }
                            break;
                        case R.id.buy:
                            Intent intent = new Intent(this, SendInformationActivity.class);
                            intent.putExtra(PRODUCT_INFO, products.get(position));
                            recyclerView.getChildAt(position).findViewById(R.id.secondLevel).setVisibility(ConstraintLayout.GONE);
                            startActivity(intent);
                            break;
                        default:
                            break;
                    }
                });
                recyclerView.setAdapter(productAdapter);

            }
        };
        viewModel.getProducts().observe(this, productsObserver);
        viewModel.readProductsFromBase();
        Observer<Boolean> isDialogShow = aBoolean -> {
            if (aBoolean) {
                dialog.show(getSupportFragmentManager(), LOADING_DIALOG_TAG);
            } else {
                dialog.dismiss();
            }
        };
        viewModel.getIsDialogShow().observe(this, isDialogShow);
        Observer<ArrayList<String>> productListObserver = products -> {
            for (String product : products) {
                viewModel.readAndWriteProduct(product);
            }
        };
        viewModel.getProductsList().observe(this, productListObserver);
        Observer<Boolean> isCellerObserver = aBoolean -> {
            if (aBoolean != null) {
                if (aBoolean) {
                    toolbar.getMenu().findItem(R.id.key).setVisible(false);
                    toolbar.getMenu().findItem(R.id.orders).setVisible(true);
                    toolbar.setTitle("Orders");
                } else {
                    Toast.makeText(this, "Wrong password!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        viewModel.getIsCeller().observe(this, isCellerObserver);
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
    }
}
