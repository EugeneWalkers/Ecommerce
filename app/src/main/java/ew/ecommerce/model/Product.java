package ew.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable{
    private String name;
    private String description;
    private String cost;
    private String imageUrl;

    private String category;

    public Product(String name, String description, String cost, String imageUrl, String category){
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(cost);
        parcel.writeString(imageUrl);
        parcel.writeString(category);
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    private Product(Parcel in) {
        name = in.readString();
        description = in.readString();
        cost = in.readString();
        imageUrl = in.readString();
        category = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
