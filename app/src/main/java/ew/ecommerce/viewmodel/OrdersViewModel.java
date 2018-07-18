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
import ew.ecommerce.model.Order;
import ew.ecommerce.utilities.DataAndReferenceKeeper;

public class OrdersViewModel extends ViewModel {


    private MutableLiveData<ArrayList<String>> ordersList;
    private MutableLiveData<ArrayList<Order>> orders;
    private MutableLiveData<Boolean> isDialogShow;

    private CollectionReference orderReference;
    private OrdersListAdapter orderAdapter;

    public OrdersViewModel(){
        super();
        isDialogShow = new MutableLiveData<>();
        //isDialogShow.postValue(false);
        orders = new MutableLiveData<>();
        orders.postValue(new ArrayList<>());
        ordersList = new MutableLiveData<>();
        ordersList.postValue(new ArrayList<>());
        orderReference = FirebaseFirestore.getInstance().collection(DataAndReferenceKeeper.ORDERS);
    }

    public LiveData<Boolean> getIsDialogShow(){
        return isDialogShow;
    }

    public LiveData<ArrayList<String>> getOrdersList() {
        return ordersList;
    }

    public LiveData<ArrayList<Order>> getOrders() {
        return orders;
    }

    public void readOrdersFromBase() {
        isDialogShow.postValue(true);
        ordersList.postValue(new ArrayList<>());
        orderReference.document(DataAndReferenceKeeper.METADATA).get().addOnCompleteListener((Task<DocumentSnapshot> task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentReference = task.getResult();
                if (documentReference.exists()) {
                    ArrayList<String> ordersArray = ((ArrayList<String>) documentReference.getData().get(DataAndReferenceKeeper.ORDERS));
                    ordersList.postValue(ordersArray);
                    orders.postValue(new ArrayList<>());
                    if (ordersArray.size() == 0){
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

    private void addOrder(Order order){
        orders.getValue().add(order);
        orders.postValue(orders.getValue());
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


    public OrdersListAdapter getOrderAdapterInstance() {
        orderAdapter = new OrdersListAdapter(orders.getValue());
        return orderAdapter;
    }
}
