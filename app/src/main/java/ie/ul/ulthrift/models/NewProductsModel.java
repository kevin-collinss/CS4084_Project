package ie.ul.ulthrift.models;

import java.io.Serializable;

public class NewProductsModel implements Serializable {

    String description, name, img_url;
    int price;

    public NewProductsModel() {

    }


    public NewProductsModel(String description, String name,  String img_url, int price) {
        this.description = description;
        this.name = name;
        this.img_url = img_url;
        this.price = price;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
