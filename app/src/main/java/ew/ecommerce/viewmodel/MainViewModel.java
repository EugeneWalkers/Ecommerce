package ew.ecommerce.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ew.ecommerce.adapters.OrdersListAdapter;
import ew.ecommerce.adapters.ProductsListAdapter;
import ew.ecommerce.model.Order;
import ew.ecommerce.model.Product;
import ew.ecommerce.utilities.DataAndReferenceKeeper;

public class MainViewModel extends ViewModel {


    private ProductsListAdapter productAdapter;
    private OrdersListAdapter orderAdapter;
    private MutableLiveData<ArrayList<Product>> products;
    private MutableLiveData<ArrayList<Order>> orders;
    private MutableLiveData<Boolean> isDialogShow;
    private MutableLiveData<ArrayList<String>> productsList;
    private MutableLiveData<ArrayList<String>> ordersList;
    private MutableLiveData<Boolean> isCeller;

    private CollectionReference productReference;
    private CollectionReference orderReference;

    public MainViewModel() {
        super();
        products = new MutableLiveData<>();
        products.postValue(new ArrayList<>());
        orders = new MutableLiveData<>();
        orders.postValue(null);
        productsList = new MutableLiveData<>();
        productsList.postValue(new ArrayList<>());
        ordersList = new MutableLiveData<>();
        ordersList.postValue(new ArrayList<>());
        isDialogShow = new MutableLiveData<>();
        isDialogShow.postValue(false);
        isCeller = new MutableLiveData<>();
        isCeller.postValue(null);
        productReference = FirebaseFirestore.getInstance().collection(DataAndReferenceKeeper.PRODUCTS);
        orderReference = FirebaseFirestore.getInstance().collection(DataAndReferenceKeeper.ORDERS);
    }

    public LiveData<ArrayList<Product>> getProducts() {
        return products;
    }

    public LiveData<ArrayList<Order>> getOrders() {
        return orders;
    }

    public LiveData<Boolean> getIsDialogShow() {
        return isDialogShow;
    }

    public LiveData<ArrayList<String>> getProductsList() {
        return productsList;
    }

    public LiveData<ArrayList<String>> getOrdersList() {
        return ordersList;
    }

    public LiveData<Boolean> getIsCeller() {
        return isCeller;
    }



    public ProductsListAdapter getProductAdapterInstance() {
        productAdapter = new ProductsListAdapter(products.getValue());
        return productAdapter;
    }

    public OrdersListAdapter getOrderAdapterInstance() {
        orderAdapter = new OrdersListAdapter(orders.getValue());
        return orderAdapter;
    }

    public void readProductsFromBase() {
        isDialogShow.postValue(true);
        productsList.postValue(new ArrayList<>());
        productReference.document(DataAndReferenceKeeper.METADATA).get().addOnCompleteListener((Task<DocumentSnapshot> task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentReference = task.getResult();
                if (documentReference.exists()) {
                    productsList.postValue((ArrayList<String>) documentReference.getData().get(DataAndReferenceKeeper.PRODUCTS));
                    products.postValue(new ArrayList<>());
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

    public void readOrdersFromBase() {
        isDialogShow.postValue(true);
        ordersList.postValue(new ArrayList<>());
        orderReference.document(DataAndReferenceKeeper.METADATA).get().addOnCompleteListener((Task<DocumentSnapshot> task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentReference = task.getResult();
                if (documentReference.exists()) {
                    ordersList.postValue((ArrayList<String>) documentReference.getData().get(DataAndReferenceKeeper.ORDERS));
                    orders.postValue(new ArrayList<>());
                    if (orders.getValue().size() == 0){
                        isDialogShow.postValue(false);
                    }
                }
            }
        });
    }

    public void readAndWriteOrder(String documentName) {
        orderReference.document(documentName).get().addOnCompleteListener((Task<DocumentSnapshot> task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentReference = task.getResult();
                if (documentReference.exists()) {
                    Map<String, Object> data = documentReference.getData();
                    String comment = data.get(DataAndReferenceKeeper.COMMENT).toString();
                    String customerName = data.get(DataAndReferenceKeeper.CUSTOMER_NAME).toString();
                    String email = data.get(DataAndReferenceKeeper.EMAIL).toString();
                    String phone = data.get(DataAndReferenceKeeper.PHONE).toString();
                    String productName = data.get(DataAndReferenceKeeper.PRODUCT_NAME).toString();
                    addOrder(new Order(productName, customerName, email, phone, comment, documentName));
                    if (isDialogShow.getValue()) {
                        isDialogShow.postValue(false);
                    }
                }
            }
        });

    }

    private void addProduct(Product product) {
        products.getValue().add(product);
        products.postValue(products.getValue());
    }

    private void addOrder(Order order){
        orders.getValue().add(order);
        orders.postValue(orders.getValue());
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
                        readOrdersFromBase();
                    }
                    else{
                        isCeller.postValue(false);
                    }
                    isDialogShow.postValue(false);
                }
            }
        });
    }

    public void deleteOrder(String orderName){
        orderReference.document(DataAndReferenceKeeper.METADATA).get().addOnCompleteListener(task->{
           if (task.isSuccessful()){
               DocumentSnapshot document = task.getResult();
               if (document.exists()){
                   ArrayList<String> orders = (ArrayList<String>)document.get(DataAndReferenceKeeper.ORDERS);
                   orders.remove(orderName);
                   Map<String, Object> newData = new HashMap<>();
                   newData.put(DataAndReferenceKeeper.ORDERS, orders);
                   orderReference.document(DataAndReferenceKeeper.METADATA).update(newData);
                   readOrdersFromBase();
               }
           }
        });
        orderReference.document(orderName).delete();
    }
}
