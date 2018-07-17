package ew.ecommerce.model;

import java.util.HashMap;
import java.util.Map;

public class Order {
    public String getProduct() {
        return product;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getComment() {
        return comment;
    }

    public String getOrderName() {
        return orderName;
    }

    private String product;
    private String name;
    private String email;
    private String phone;
    private String comment;
    private String orderName;

    public Order(String product, String name, String email, String phone, String comment, String orderName) {
        this.product = product;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.comment = comment;
        this.orderName = orderName;
    }

    @Override
    public String toString() {
        return product + ":" + name + ":" + email + ":" + phone + ":" + comment;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("comment", comment);
        map.put("customerName", name);
        map.put("email", email);
        map.put("phone", phone);
        map.put("productName", product);
        return map;
    }
}
