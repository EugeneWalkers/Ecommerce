package ew.ecommerce.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ew.ecommerce.model.Order;
import ew.ecommerce.utilities.DataAndReferenceKeeper;

public class SendInformationViewModel extends ViewModel {

    private MutableLiveData<String> state;
    private MutableLiveData<ArrayList<String>> orders;
    public static final String WAITING = "waiting";
    public static final String LOADING = "loading";
    public static final String FAILED = "failed";
    public static final String SUCCESSFUL = "successful";

    public SendInformationViewModel() {
        super();
        state = new MutableLiveData<>();
        orders = new MutableLiveData<>();
        state.postValue(WAITING);
    }

    public void updateOrderListBase(String orderName) {
        state.postValue(LOADING);
        DocumentReference reference = FirebaseFirestore.getInstance().collection(DataAndReferenceKeeper.ORDERS).document(DataAndReferenceKeeper.METADATA);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()){
                    ArrayList<String> existingOrders = (ArrayList<String>)documentSnapshot.get(DataAndReferenceKeeper.ORDERS);
                    existingOrders.add(orderName);
                    orders.postValue(existingOrders);
                }
            }
        });

    }

    public void addOrderDocument(Order order){
        Map<String, Object> updater = new HashMap<>();
        updater.put(DataAndReferenceKeeper.ORDERS, orders.getValue());
        String orderName = order.getOrderName();
        FirebaseFirestore.getInstance()
                .collection(DataAndReferenceKeeper.ORDERS)
                .document(DataAndReferenceKeeper.METADATA).update(updater).addOnCompleteListener(task -> {
            FirebaseFirestore.getInstance().collection(DataAndReferenceKeeper.ORDERS).document(orderName).set(order.toMap()).addOnSuccessListener(task1->{
                state.postValue(SUCCESSFUL);
            }).addOnFailureListener(task1->{
                state.postValue(FAILED);
                state.postValue(WAITING);
            });
        }).addOnFailureListener(task -> {
            state.postValue(FAILED);
            state.postValue(WAITING);
        });
    }

    public LiveData<String> getStateForDialog() {
        return state;
    }

    public LiveData<ArrayList<String>> getOrders(){
        return orders;
    }
}
