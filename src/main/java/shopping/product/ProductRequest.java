package shopping.product;

public record ProductRequest(String name, int price, String imageUrl) {

    public Product toProduct() {
        return new Product(new ProductName(name), price, imageUrl);
    }
}
