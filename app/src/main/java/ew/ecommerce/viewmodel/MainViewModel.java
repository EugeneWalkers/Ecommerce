package ew.ecommerce.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import ew.ecommerce.adapters.ProductsListAdapter;
import ew.ecommerce.model.Product;
import ew.ecommerce.utilities.DataAndReferenceKeeper;

public class MainViewModel extends ViewModel {


    private ProductsListAdapter productAdapter;
    private MutableLiveData<ArrayList<Product>> products;
    private MutableLiveData<Boolean> isDialogShow;
    private MutableLiveData<ArrayList<String>> productsList;
    private MutableLiveData<Boolean> isCeller;

    private CollectionReference productReference;

    public MainViewModel() {
        super();
        products = new MutableLiveData<>();
        products.postValue(new ArrayList<>());
        productsList = new MutableLiveData<>();
        productsList.postValue(new ArrayList<>());
        isDialogShow = new MutableLiveData<>();
        isDialogShow.postValue(false);
        isCeller = new MutableLiveData<>();
        isCeller.postValue(null);
        productReference = FirebaseFirestore.getInstance().collection(DataAndReferenceKeeper.PRODUCTS);
    }

    public LiveData<ArrayList<Product>> getProducts() {
        return products;
    }


    public LiveData<Boolean> getIsDialogShow() {
        return isDialogShow;
    }

    public LiveData<ArrayList<String>> getProductsList() {
        return productsList;
    }


    public LiveData<Boolean> getIsCeller() {
        return isCeller;
    }


    private void addProduct(Product product) {
        products.getValue().add(product);
        products.postValue(products.getValue());
    }


    public ProductsListAdapter getProductAdapterInstance() {
        productAdapter = new ProductsListAdapter(products.getValue(), isCeller.getValue());
        return productAdapter;
    }



    public void readProductsFromBase() {
        isDialogShow.postValue(true);
        productsList.postValue(new ArrayList<>());
        productReference.document(DataAndReferenceKeeper.METADATA).get().addOnCompleteListener((Task<DocumentSnapshot> task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentReference = task.getResult();
                if (documentReference.exists()) {
                    ArrayList<String> productsArray = (ArrayList<String>) documentReference.getData().get(DataAndReferenceKeeper.PRODUCTS);
                    productsList.postValue(productsArray);
                    products.postValue(new ArrayList<>());
                    if (productsArray.size() == 0){
                        isDialogShow.postValue(false);
                    }
                }
            }
        });
    }

    public void readAndWriteProduct(String documentName) {
        productReference.document(documentName).get().addOnCompleteListener((Task<DocumentSnapshot> task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentReference = task.getResult();
                if (documentReference.exists()) {
                    Map<String, Object> data = documentReference.getData();
                    String name = data.get(DataAndReferenceKeeper.NAME).toString();
                    String category = data.get(DataAndReferenceKeeper.CATEGORY).toString();
                    String cost = data.get(DataAndReferenceKeeper.COST).toString();
                    String imageURL = data.get(DataAndReferenceKeeper.IMAGE_URL).toString();
                    String description = data.get(DataAndReferenceKeeper.DESCRIPTION).toString();
                    addProduct(new Product(name, description, cost, imageURL, category));
                    if (isDialogShow.getValue()) {
                        isDialogShow.postValue(false);
                    }
                }
            }
        });

    }



    public void checkPassword(String password) {
        isDialogShow.postValue(true);
        FirebaseFirestore.getInstance().collection(DataAndReferenceKeeper.CELLER).document(DataAndReferenceKeeper.DATA).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String realPass = document.get(DataAndReferenceKeeper.PASSWORD).toString();
                    if (password.equals(realPass)) {
                        isCeller.postValue(true);
                        readProductsFromBase();
                    }
                    else{
                        isCeller.postValue(false);
                    }
                    isDialogShow.postValue(false);
                }
            }
        });
    }

}
