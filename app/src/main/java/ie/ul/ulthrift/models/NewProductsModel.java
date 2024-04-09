package ie.ul.ulthrift.models;

import java.io.Serializable;

public class NewProductsModel implements Serializable {
    String description, name, img_url, showAllDocId;
    int price;
    //needed for user seeing own product
    private String userId;

    public NewProductsModel() {
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public int getPrice() {
        return price;
    }
    //Added in as now add these fields
    public String getShowAllDocId() {
        return showAllDocId;
    }

    //getter for userId
    public String getUserId() {
        return userId;
    }


}
