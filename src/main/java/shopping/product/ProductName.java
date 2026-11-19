package shopping.product;

public record ProductName(String value) {

    public ProductName {
        validateNotBlank(value);
    }

    private static void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 비어 있을 수 없습니다.");
        }
    }
}
