package com.example.simplezakka.repository;

import com.example.simplezakka.entity.Product;
import jakarta.persistence.PersistenceException; // JPA標準の例外
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException; // Spring Data JPAの例外

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest // JPAリポジトリテストに特化した設定
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // テストデータの準備や永続化の検証に使用

    @Autowired
    private ProductRepository productRepository; // テスト対象

    private Product product1;
    private Product product2;

    // テストデータ準備用のヘルパーメソッド
    private Product createProduct(String name, int price, int stock) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setDescription(name + "の説明です。");
        product.setImageUrl("/images/" + name.toLowerCase() + ".jpg");
        // isRecommendedなどは必要に応じて設定
        return product;
    }

    @BeforeEach
    void setUp() {
        // 各テストメソッド実行前に共通のデータを準備
        product1 = createProduct("商品A", 1000, 10);
        product2 = createProduct("商品B", 2000, 5);
        entityManager.persist(product1); // TestEntityManagerで永続化
        entityManager.persist(product2);
        entityManager.flush(); // DBに即時反映
        entityManager.clear(); // 永続化コンテキストキャッシュをクリアし、後続のテストがDBから取得するようにする
    }

    @Test
    @DisplayName("商品を正常に保存し、IDで検索できる")
    void saveAndFindById_Success() {
        // Arrange
        Product newProduct = createProduct("新商品C", 3000, 20);

        // Act
        Product savedProduct = productRepository.save(newProduct); // リポジトリ経由で保存
        entityManager.flush(); // DBへ反映
        Integer savedId = savedProduct.getProductId(); // 生成されたIDを取得
        entityManager.clear(); // キャッシュクリア

        Optional<Product> foundProductOpt = productRepository.findById(savedId); // 保存したIDで検索

        // Assert
        assertThat(foundProductOpt).isPresent(); // Optionalが空でない
        Product foundProduct = foundProductOpt.get();
        assertThat(foundProduct.getProductId()).isEqualTo(savedId); // IDが一致する
        assertThat(foundProduct.getName()).isEqualTo(newProduct.getName()); // 名前が一致する
        assertThat(foundProduct.getPrice()).isEqualTo(newProduct.getPrice()); // 価格が一致する
        assertThat(foundProduct.getStock()).isEqualTo(newProduct.getStock()); // 在庫が一致する
        assertThat(foundProduct.getCreatedAt()).isNotNull(); // @PrePersist で createdAt が設定されている
        assertThat(foundProduct.getUpdatedAt()).isEqualTo(foundProduct.getCreatedAt()); // 作成時は updatedAt も createdAt と同じ
    }

    @Test
    @DisplayName("存在するIDで商品を検索できる")
    void findById_WhenProductExists_ShouldReturnProduct() {
        // Arrange: setUpでproduct1が保存されている。IDは product1.getProductId() で取得可能。

        // Act
        Optional<Product> foundProductOpt = productRepository.findById(product1.getProductId());

        // Assert
        assertThat(foundProductOpt).isPresent();
        assertThat(foundProductOpt.get().getName()).isEqualTo(product1.getName());
    }

    @Test
    @DisplayName("存在しないIDで商品を検索するとOptional.emptyが返る")
    void findById_WhenProductNotExists_ShouldReturnEmpty() {
        // Arrange
        Integer nonExistingId = 999; // 存在しないであろうID

        // Act
        Optional<Product> foundProductOpt = productRepository.findById(nonExistingId);

        // Assert
        assertThat(foundProductOpt).isNotPresent(); // Optionalが空であること
    }

    @Test
    @DisplayName("すべての商品を取得できる")
    void findAll_ShouldReturnAllProducts() {
        // Arrange: setUpで2件の商品が保存されている

        // Act
        List<Product> products = productRepository.findAll(); // 全件取得

        // Assert
        assertThat(products).hasSize(2); // 件数が正しいか
        // 内容も確認（例: 商品名）
        assertThat(products).extracting(Product::getName)
                         .containsExactlyInAnyOrder(product1.getName(), product2.getName());
    }

    @Test
    @DisplayName("商品が存在しない場合findAllは空のリストを返す")
    void findAll_WhenNoProducts_ShouldReturnEmptyList() {
        // Arrange
        // 既存のデータを削除 (テストの独立性を保つため)
        entityManager.clear(); // まずクリア
        entityManager.getEntityManager().createQuery("DELETE FROM Product").executeUpdate(); // JPQLで全削除
        entityManager.flush(); // DBに反映

        // Act
        List<Product> products = productRepository.findAll();

        // Assert
        assertThat(products).isEmpty(); // 空のリストであること
    }

    @Test
    @DisplayName("商品を更新できる")
    void updateProduct_ShouldReflectChanges() {
        // Arrange
        Integer productId = product1.getProductId(); // 更新対象のID
        LocalDateTime initialUpdatedAt = product1.getUpdatedAt(); // 更新前のタイムスタンプ
        entityManager.detach(product1); // 一度コンテキストから切り離し、取得→更新のシナリオを模倣

        // Act
        Product productToUpdate = productRepository.findById(productId).orElseThrow(); // 更新対象を取得
        String newName = "更新された商品A";
        int newPrice = 1200;
        productToUpdate.setName(newName); // 名前を変更
        productToUpdate.setPrice(newPrice); // 価格を変更
        Product updatedProductResult = productRepository.save(productToUpdate); // 更新実行 (IDが存在するためUPDATE)
        entityManager.flush(); // DBへ反映
        entityManager.clear(); // キャッシュクリア

        // Assert
        Product foundAfterUpdate = entityManager.find(Product.class, productId); // DBから再取得して確認
        assertThat(foundAfterUpdate).isNotNull();
        assertThat(foundAfterUpdate.getName()).isEqualTo(newName); // 名前が更新されている
        assertThat(foundAfterUpdate.getPrice()).isEqualTo(newPrice); // 価格が更新されている
        assertThat(foundAfterUpdate.getStock()).isEqualTo(product1.getStock()); // 変更していない在庫は元のまま
        assertThat(foundAfterUpdate.getUpdatedAt()).isAfter(initialUpdatedAt); // @PreUpdateにより更新日時が変更されている
        // assertThat(updatedProductResult.getUpdatedAt()).isEqualTo(foundAfterUpdate.getUpdatedAt()); // saveの戻り値はナノ秒の違いがあるためテストしない
    }

    @Test
    @DisplayName("商品を削除できる")
    void deleteProduct_ShouldRemoveFromDatabase() {
        // Arrange
        Integer productId = product1.getProductId();
        // 削除前に存在することを確認
        assertThat(productRepository.findById(productId)).isPresent();

        // Act
        productRepository.deleteById(productId); // IDで削除
        entityManager.flush(); // DBへ反映
        entityManager.clear(); // キャッシュクリア

        // Assert
        // 削除されたことを確認
        assertThat(productRepository.findById(productId)).isNotPresent();
        // entityManager.findを使っても確認できる
        assertThat(entityManager.find(Product.class, productId)).isNull();
    }


    // --- decreaseStock のテスト ---

    @Test
    @DisplayName("decreaseStock: 正常に在庫を減らせる")
    void decreaseStock_Success() {
        // Arrange
        Integer productId = product1.getProductId();
        Integer initialStock = product1.getStock(); // 10
        Integer decreaseQuantity = 3;

        // Act
        int updatedRows = productRepository.decreaseStock(productId, decreaseQuantity);
        entityManager.flush(); // 更新クエリをDBに反映
        entityManager.clear(); // キャッシュをクリアしてDBの状態を確認

        // Assert
        assertThat(updatedRows).isEqualTo(1); // 1行更新されたことを確認
        Product updatedProduct = entityManager.find(Product.class, productId); // DBから再取得
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getStock()).isEqualTo(initialStock - decreaseQuantity); // 在庫が正しく減っている
    }

    @Test
    @DisplayName("decreaseStock: 在庫不足の場合、在庫は減らず更新行数は0")
    void decreaseStock_Fail_InsufficientStock() {
        // Arrange
        Integer productId = product2.getProductId();
        Integer initialStock = product2.getStock(); // 5
        Integer decreaseQuantity = 6; // 在庫(5)よりも多い数を減らそうとする

        // Act
        int updatedRows = productRepository.decreaseStock(productId, decreaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(updatedRows).isEqualTo(0); // 更新行数は0 (WHERE句の p.stock >= ?2 条件に合致しない)
        Product notUpdatedProduct = entityManager.find(Product.class, productId);
        assertThat(notUpdatedProduct).isNotNull();
        assertThat(notUpdatedProduct.getStock()).isEqualTo(initialStock); // 在庫は変化していない
    }

    @Test
    @DisplayName("decreaseStock: 存在しない商品IDの場合、更新行数は0")
    void decreaseStock_Fail_ProductNotFound() {
        // Arrange
        Integer nonExistingId = 999;
        Integer decreaseQuantity = 1;

        // Act
        int updatedRows = productRepository.decreaseStock(nonExistingId, decreaseQuantity);
        entityManager.flush(); // 念のため

        // Assert
        assertThat(updatedRows).isEqualTo(0); // 更新対象が見つからないため、更新行数は0
    }

    @Test
    @DisplayName("decreaseStock: 減らす数量が0の場合、更新行数は1、在庫は不変")
    void decreaseStock_WithZeroQuantity() {
        // Arrange
        Integer productId = product1.getProductId();
        Integer initialStock = product1.getStock(); // 10
        Integer decreaseQuantity = 0;

        // Act
        int updatedRows = productRepository.decreaseStock(productId, decreaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(updatedRows).isEqualTo(1); // 条件(p.stock >= 0)は満たすので更新は試みられる
        Product updatedProduct = entityManager.find(Product.class, productId);
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getStock()).isEqualTo(initialStock); // 在庫は変わらない
    }

    @Test
    @DisplayName("decreaseStock: 減らす数量が負数の場合、更新行数は1、在庫は増加する（現在のクエリの挙動）")
    void decreaseStock_WithNegativeQuantity_ShouldIncreaseStockBasedOnCurrentQuery() {
        // Arrange
        Integer productId = product1.getProductId();
        Integer initialStock = product1.getStock(); // 10
        Integer decreaseQuantity = -2; // 負の数

        // Act
        int updatedRows = productRepository.decreaseStock(productId, decreaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        // 現在のクエリ "UPDATE Product p SET p.stock = p.stock - ?2 WHERE p.productId = ?1 AND p.stock >= ?2"
        // では、p.stock >= -2 は通常trueとなり、p.stock = p.stock - (-2) = p.stock + 2 が実行される。
        assertThat(updatedRows).isEqualTo(1); // 条件を満たすため更新される
        Product updatedProduct = entityManager.find(Product.class, productId);
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getStock()).isEqualTo(initialStock + 2); // 在庫が増加している
        // 注意: この挙動が意図しない場合、Service層で負の数量をバリデーションするか、リポジトリのクエリを修正する必要がある。
        // このテストは、現在のリポジトリのクエリがどう動くかを確認するもの。
    }

    // --- 制約違反のテスト ---

    @Test
    @DisplayName("必須項目(name)がnullで保存しようとすると例外発生")
    void saveProduct_WithNullName_ShouldThrowException() {
        // Arrange
        // Product product = createProduct(null, 500, 1); // ここでNPEが発生するため、ヘルパーメソッドを使わない
    
        // Productオブジェクトを直接生成し、nameにnullを設定する
        Product product = new Product();
        product.setName(null); // 名前をnullに設定
        product.setPrice(500);
        product.setStock(1);
        // description や imageUrl は、nullを許容するフィールドなので、
        // 設定しなくても良いか、テストに必要な場合は適切に設定します。
        // product.setDescription("テスト用説明");
        // product.setImageUrl("/images/test.jpg");
    
        // Act & Assert
        assertThatThrownBy(() -> {
            productRepository.save(product);
            entityManager.flush(); // DBへの反映時に制約違反が発生
        })
        .isInstanceOf(DataIntegrityViolationException.class) // Spring Data JPAがラップした例外
        .hasCauseInstanceOf(PersistenceException.class); // JPAレイヤの例外が原因
    }
    
    @Test
    @DisplayName("必須項目(price)がnullで保存しようとすると例外発生")
    void saveProduct_WithNullPrice_ShouldThrowException() {
        // Arrange
        Product product = createProduct("価格Null商品", 0, 1); // 一旦ダミーで作成
        product.setPrice(null); // 価格をnullに

        // Act & Assert
        assertThatThrownBy(() -> {
            productRepository.save(product);
            entityManager.flush();
        })
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasCauseInstanceOf(PersistenceException.class);
    }

     @Test
    @DisplayName("必須項目(stock)がnullで保存しようとすると例外発生")
    void saveProduct_WithNullStock_ShouldThrowException() {
        // Arrange
        Product product = createProduct("在庫Null商品", 500, 0); // 一旦ダミーで作成
        product.setStock(null); // 在庫をnullに

        // Act & Assert
        assertThatThrownBy(() -> {
            productRepository.save(product);
            entityManager.flush();
        })
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasCauseInstanceOf(PersistenceException.class);
    }
}