package ew.ecommerce.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ew.ecommerce.R;
import ew.ecommerce.model.Product;
import ew.ecommerce.utilities.DataAndReferenceKeeper;
import ew.ecommerce.viewmodel.EditViewModel;

public class EditActivity extends AppCompatActivity {

    private EditViewModel viewModel;
    private Product product;
    private boolean isEdit;
    private String oldName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        viewModel = ViewModelProviders.of(this).get(EditViewModel.class);
        Toolbar toolbar = findViewById(R.id.my_toolbar_edit);
        toolbar.setTitle(R.string.edit);
        Intent data = getIntent();
        if (data.getExtras() != null) {
            isEdit = true;
            product = data.getParcelableExtra(DataAndReferenceKeeper.PRODUCT);
            oldName = product.getName();
            ((EditText) findViewById(R.id.product_name_edit)).setText(product.getName());
            ((EditText) findViewById(R.id.cost_edit)).setText(product.getCost());
            ((EditText) findViewById(R.id.description_edit)).setText(product.getDescription());
            ((EditText) findViewById(R.id.category_edit)).setText(product.getCategory());
            ((EditText) findViewById(R.id.image_url_edit)).setText(product.getImageUrl());
        }
        else{
            isEdit = false;
        }
        setButtonAccept();
        setButtonDelete();
        setObservers();

    }

    private void setButtonAccept(){
        Button accept = findViewById(R.id.accept_product);
        accept.setOnClickListener(view -> {
            String name = ((EditText) findViewById(R.id.product_name_edit)).getText().toString();
            String description = ((EditText) findViewById(R.id.description_edit)).getText().toString();
            String cost = ((EditText) findViewById(R.id.cost_edit)).getText().toString();
            String category = ((EditText) findViewById(R.id.category_edit)).getText().toString();
            String imageURL = ((EditText) findViewById(R.id.image_url_edit)).getText().toString();
            viewModel.onAcceptClicked(oldName, new Product(name, description, cost, imageURL, category), isEdit);
        });
    }

    private void setButtonDelete(){
        Button delete = findViewById(R.id.delete_product);
        if (!isEdit){
            delete.setEnabled(false);
        }
        else{
            delete.setOnClickListener(view->{
                viewModel.deleteDocument(product.getName());
            });
        }
    }

    private void setObservers(){
        Observer<Boolean> isSuccessfulObserver = aBoolean -> {
            if (aBoolean != null) {
                if (aBoolean) {
                    Toast.makeText(this, "Successfully!", Toast.LENGTH_SHORT).show();
                    setResult(MainActivity.OK);
                    finish();
                } else {
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        viewModel.getIsSuccessful().observe(this, isSuccessfulObserver);
    }
}
