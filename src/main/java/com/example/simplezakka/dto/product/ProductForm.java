package com.example.simplezakka.dto.product;

public class ProductForm {
    private Integer product_id;
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private String image_URL;
    private Boolean is_recommended;
    private String category;
    private String material;

    // GetterとSetter
    public Integer getProduct_id() {
        return product_id;
    }
    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
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

    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImage_URL() {
        return image_URL;
    }
    public void setImage_URL(String image_URL) {
        this.image_URL = image_URL;
    }

    public Boolean getIs_recommended() {
        return is_recommended;
    }
    public void setIs_recommended(Boolean is_recommended) {
        this.is_recommended = is_recommended;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getMaterial() {
        return material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }
}
