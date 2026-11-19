package shopping.product;

public record ProductName(String value) {

    private static final int MAX_LENGTH = 15;

    public ProductName {
        validateNotBlank(value);
        validateLength(value);
    }

    private static void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 비어 있을 수 없습니다.");
        }
    }

    private static void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "상품 이름은 공백 포함 최대 " + MAX_LENGTH + "자까지 입력할 수 있습니다.");
        }
    }
}
