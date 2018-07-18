package ew.ecommerce.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import ew.ecommerce.R;
import ew.ecommerce.model.Order;
import ew.ecommerce.model.Product;
import ew.ecommerce.utilities.OrderAcceptUtility;
import ew.ecommerce.viewmodel.SendInformationViewModel;

public class SendInformationActivity extends AppCompatActivity {

    private Product product;
    private SendInformationViewModel viewModel;
    public static final String LOADING_DIALOG_TAG = "loading_dialog_tag";
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_information);
        viewModel = ViewModelProviders.of(this).get(SendInformationViewModel.class);
        Intent intent = getIntent();
        LoadingDialog dialog = LoadingDialog.newInstance(this);
        product = intent.getParcelableExtra(MainActivity.PRODUCT_INFO);
        ((TextView) findViewById(R.id.orderName)).setText(product.getName());
        Button buyButton = findViewById(R.id.accept_order);
        buyButton.setOnClickListener(view -> {
            String customerName = ((EditText) findViewById(R.id.customerName)).getText().toString();
            String email = ((EditText) findViewById(R.id.email)).getText().toString();
            String phone = ((EditText) findViewById(R.id.phone)).getText().toString();
            String comment = ((EditText) findViewById(R.id.comment)).getText().toString();
            if (OrderAcceptUtility.isEmailValid(email) && OrderAcceptUtility.isPhoneValid(phone)) {
                order = new Order(product.getName(), customerName, email, phone, comment, Calendar.getInstance().getTime().toString());
                viewModel.updateOrderListBase(order.getOrderName());
            }
            else{
                Toast.makeText(this, "Error! Check input data", Toast.LENGTH_SHORT).show();
            }
        });
        Observer<String> stateObserver = state -> {
            switch (state) {
                case SendInformationViewModel.WAITING:

                    break;
                case SendInformationViewModel.LOADING:
                    dialog.show(getSupportFragmentManager(), LOADING_DIALOG_TAG);
                    break;
                case SendInformationViewModel.FAILED:
                    dialog.dismiss();
                    Toast.makeText(this, "Error! Try again later", Toast.LENGTH_SHORT).show();
                    break;
                case SendInformationViewModel.SUCCESSFUL:
                    dialog.dismiss();
                    Toast.makeText(this, "Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    break;
            }
        };
        viewModel.getStateForDialog().observe(this, stateObserver);


        Observer<ArrayList<String>> ordersObserver = orderList->{
            viewModel.addOrderDocument(order);
        };

        viewModel.getOrders().observe(this, ordersObserver);
    }
}
