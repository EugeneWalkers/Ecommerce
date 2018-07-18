package ew.ecommerce.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ew.ecommerce.model.Product;
import ew.ecommerce.utilities.DataAndReferenceKeeper;

public class EditViewModel extends ViewModel {

    private MutableLiveData<Boolean> isSuccessful;
    private CollectionReference productsReference;

    public EditViewModel() {
        super();
        isSuccessful = new MutableLiveData<>();
        isSuccessful.postValue(null);
        productsReference = FirebaseFirestore.getInstance().collection(DataAndReferenceKeeper.PRODUCTS);
    }

    public LiveData<Boolean> getIsSuccessful() {
        return isSuccessful;
    }

    public void onAcceptClicked(String oldName, Product product, boolean isEdit) {

        if (isEdit){
            productsReference.document(oldName).delete();
        }
        productsReference
                .document(product.getName())
                .set(product.toMap());
        FirebaseFirestore.getInstance()
                .collection(DataAndReferenceKeeper.PRODUCTS)
                .document(DataAndReferenceKeeper.METADATA).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<String> products = (ArrayList<String>) document.get(DataAndReferenceKeeper.PRODUCTS);
                    if (!isEdit){
                        products.add(product.getName());
                    }
                    else {
                        for (int i=0; i<products.size(); i++){
                            if (products.get(i).equals(oldName)){
                                products.set(i, product.getName());
                            }
                        }
                    }
                    Map<String, Object> data = new HashMap<>();
                    data.put(DataAndReferenceKeeper.PRODUCTS, products);
                    FirebaseFirestore.getInstance()
                            .collection(DataAndReferenceKeeper.PRODUCTS)
                            .document(DataAndReferenceKeeper.METADATA)
                            .update(data);
                    isSuccessful.postValue(true);
                }
            }
        });
    }

    public void deleteDocument(String docName) {
        productsReference.document(DataAndReferenceKeeper.METADATA).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<String> documents = (ArrayList<String>) document.get(DataAndReferenceKeeper.PRODUCTS);
                    documents.remove(docName);
                    Map<String, Object> newData = new HashMap<>();
                    newData.put(DataAndReferenceKeeper.PRODUCTS, documents);
                    productsReference.document(DataAndReferenceKeeper.METADATA).update(newData);
                    productsReference.document(docName).delete();
                    isSuccessful.postValue(true);
                }
            }
        }).addOnFailureListener(task -> {
            isSuccessful.postValue(false);
        });
    }

}
