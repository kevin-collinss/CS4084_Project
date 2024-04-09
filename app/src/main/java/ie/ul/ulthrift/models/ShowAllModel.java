package ie.ul.ulthrift.models;

import java.io.Serializable;

public class ShowAllModel implements Serializable {

    String description, name, img_url;
    //Needs to be private
    private String documentId;

    //Document id for newProduct
    private String newProductDocId;

    //Needed for user seeing own product
    private String userId;
    int price;

    public ShowAllModel() {
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

    public String getDocumentId() {
        return documentId;
    }

    //setting the documentid
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    //get documentId
    public String getNewProductDocId() {
        return newProductDocId;
    }

    //getter for userId
    public String getUserId() {
        return userId;
    }
}
