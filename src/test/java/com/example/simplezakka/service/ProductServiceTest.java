package com.example.simplezakka.service;

import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections; // 空のリスト用
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple; // tupleを使った検証用
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;
    private Product productWithNullFields; // nullフィールドを持つテストデータ

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setProductId(1);
        product1.setName("商品1");
        product1.setPrice(100);
        product1.setImageUrl("/img1.png");
        product1.setDescription("説明1");
        product1.setStock(10);
        // createdAt, updatedAt はエンティティ側で自動設定される想定

        product2 = new Product();
        product2.setProductId(2);
        product2.setName("商品2");
        product2.setPrice(200);
        product2.setImageUrl("/img2.png");
        product2.setDescription("説明2");
        product2.setStock(5);

        productWithNullFields = new Product();
        productWithNullFields.setProductId(3);
        productWithNullFields.setName("商品3（Nullあり）");
        productWithNullFields.setPrice(300);
        productWithNullFields.setStock(8);
        productWithNullFields.setDescription(null); // descriptionがnull
        productWithNullFields.setImageUrl(null);    // imageUrlがnull
    }

    // === findAllProducts のテスト ===

    @Test
    @DisplayName("findAllProducts: リポジトリから複数の商品が返される場合、ProductListItemのリストを返す")
    void findAllProducts_ShouldReturnListOfProductListItems() {
        // Arrange: モックの設定
        List<Product> productsFromRepo = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(productsFromRepo);

        // Act: テスト対象メソッドの実行
        List<ProductListItem> result = productService.findAllProducts();

        // Assert: 結果の検証
        assertThat(result).hasSize(2);
        // 各要素の全フィールドが正しくマッピングされているか検証 (tupleを使うと便利)
        assertThat(result)
            .extracting(ProductListItem::getProductId, ProductListItem::getName, ProductListItem::getPrice, ProductListItem::getImageUrl)
            .containsExactlyInAnyOrder(
                tuple(product1.getProductId(), product1.getName(), product1.getPrice(), product1.getImageUrl()),
                tuple(product2.getProductId(), product2.getName(), product2.getPrice(), product2.getImageUrl())
            );

        // Verify: メソッド呼び出し検証
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository); // 他のメソッドが呼ばれていないこと
    }

    @Test
    @DisplayName("findAllProducts: リポジトリから空のリストが返される場合、空のリストを返す")
    void findAllProducts_WhenRepositoryReturnsEmptyList_ShouldReturnEmptyList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ProductListItem> result = productService.findAllProducts();

        // Assert
        assertThat(result).isEmpty();

        // Verify
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("findAllProducts: 商品エンティティにnullフィールドが含まれる場合、DTOにもnullがマッピングされる")
    void findAllProducts_WhenProductHasNullFields_ShouldMapNullToDto() {
        // Arrange
        List<Product> productsFromRepo = List.of(productWithNullFields);
        when(productRepository.findAll()).thenReturn(productsFromRepo);

        // Act
        List<ProductListItem> result = productService.findAllProducts();

        // Assert
        assertThat(result).hasSize(1);
        ProductListItem dto = result.get(0);
        assertThat(dto.getProductId()).isEqualTo(productWithNullFields.getProductId());
        assertThat(dto.getName()).isEqualTo(productWithNullFields.getName());
        assertThat(dto.getPrice()).isEqualTo(productWithNullFields.getPrice());
        assertThat(dto.getImageUrl()).isNull(); // imageUrlがnullであることを確認

        // Verify
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository);
    }

    // === findProductById のテスト ===

    @Test
    @DisplayName("findProductById: 存在するIDで検索した場合、ProductDetailを返す")
    void findProductById_WhenProductExists_ShouldReturnProductDetail() {
        // Arrange
        Integer productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        // Act
        ProductDetail result = productService.findProductById(productId);

        // Assert
        assertThat(result).isNotNull();
        // 全フィールドが正しくマッピングされているか検証
        assertThat(result.getProductId()).isEqualTo(product1.getProductId());
        assertThat(result.getName()).isEqualTo(product1.getName());
        assertThat(result.getPrice()).isEqualTo(product1.getPrice());
        assertThat(result.getDescription()).isEqualTo(product1.getDescription());
        assertThat(result.getStock()).isEqualTo(product1.getStock());
        assertThat(result.getImageUrl()).isEqualTo(product1.getImageUrl());

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("findProductById: 存在しないIDで検索した場合、nullを返す")
    void findProductById_WhenProductNotExists_ShouldReturnNull() {
        // Arrange
        Integer productId = 99;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        ProductDetail result = productService.findProductById(productId);

        // Assert
        assertThat(result).isNull();

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("findProductById: 商品エンティティにnullフィールドが含まれる場合、DTOにもnullがマッピングされる")
    void findProductById_WhenProductHasNullFields_ShouldMapNullToDto() {
        // Arrange
        Integer productId = 3;
        when(productRepository.findById(productId)).thenReturn(Optional.of(productWithNullFields));

        // Act
        ProductDetail result = productService.findProductById(productId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productWithNullFields.getProductId());
        assertThat(result.getName()).isEqualTo(productWithNullFields.getName());
        assertThat(result.getPrice()).isEqualTo(productWithNullFields.getPrice());
        assertThat(result.getStock()).isEqualTo(productWithNullFields.getStock());
        assertThat(result.getDescription()).isNull(); // descriptionがnullであることを確認
        assertThat(result.getImageUrl()).isNull();    // imageUrlがnullであることを確認

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("findProductById: 引数productIdがnullの場合、リポジトリのfindById(null)が呼ばれ、結果的に例外またはnullが返る")
    void findProductById_WhenProductIdIsNull_ShouldDelegateToRepositoryAndPotentiallyFail() {
        // Arrange
        Integer nullProductId = null;
        // findById(null) がどのように振る舞うかはリポジトリの実装やJPAプロバイダに依存する。
        // ここでは、MockitoがfindById(null)を受け付け、Optional.empty() を返すように定義してみる。
        // (実際には NullPointerException や IllegalArgumentException が発生する可能性もある)
        when(productRepository.findById(nullProductId)).thenReturn(Optional.empty());
        // もし例外を期待する場合は以下のように書く
        // when(productRepository.findById(nullProductId)).thenThrow(new IllegalArgumentException("ID cannot be null"));

        // Act
        ProductDetail result = productService.findProductById(nullProductId);

        // Assert
        // Optional.empty() を返すように定義したので、結果は null になるはず
        assertThat(result).isNull();
        // 例外を期待する場合は以下のように書く
        // assertThatThrownBy(() -> productService.findProductById(nullProductId))
        //     .isInstanceOf(IllegalArgumentException.class)
        //     .hasMessage("ID cannot be null");

        // Verify
        verify(productRepository, times(1)).findById(nullProductId);
        verifyNoMoreInteractions(productRepository);
    }

    // 以下木村追加分（Excel黄色の網掛け部分から開始）
    // Excel10行目
    @Test
    @DisplayName("createProduct: 正常なProductDetailを保存し、返却値が入力内容と一致する")
    void createProduct_ShouldSaveAndReturnProductDetail() {
    // Arrange
    // 1. 入力用ProductDetail（テストデータ）を作成
    ProductDetail inputDetail = new ProductDetail(
            null, // IDは新規なのでnull（または0でも可）
            "新商品",
            "新商品の説明",
            1234,
            20,
            "/img_new.png",
            true,
            "キッチン",
            "木"
    );

    // 2. saveの返却値となるProduct（IDが採番されて返る想定）
    Product savedProduct = new Product();
    savedProduct.setProductId(100); // DBで採番されたID
    savedProduct.setName("新商品");
    savedProduct.setDescription("新商品の説明");
    savedProduct.setPrice(1234);
    savedProduct.setStock(20);
    savedProduct.setImageUrl("/img_new.png");
    savedProduct.setIsRecommended(true);
    savedProduct.setCategory("キッチン");
    savedProduct.setMaterial("木");

    // モックの設定：saveでsavedProductが返る
    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

    // Act
    ProductDetail result = productService.createProduct(inputDetail);

    // Assert
    // saveが1回呼ばれている
    verify(productRepository, times(1)).save(any(Product.class));
    // 他のメソッドは呼ばれていない
    verifyNoMoreInteractions(productRepository);

    // 返り値（ProductDetail）が正しくマッピングされているか
    assertThat(result).isNotNull();
    assertThat(result.getProductId()).isEqualTo(100); // 採番ID
    assertThat(result.getName()).isEqualTo("新商品");
    assertThat(result.getDescription()).isEqualTo("新商品の説明");
    assertThat(result.getPrice()).isEqualTo(1234);
    assertThat(result.getStock()).isEqualTo(20);
    assertThat(result.getImageUrl()).isEqualTo("/img_new.png");
    assertThat(result.getIsRecommended()).isTrue();
    assertThat(result.getCategory()).isEqualTo("キッチン");
    assertThat(result.getMaterial()).isEqualTo("木");
}

    // Excel11行目
    @Test
    @DisplayName("updateProduct: 指定IDの商品が存在し、更新できる場合は内容を反映したProductDetailを返す")
    void updateProduct_ShouldUpdateAndReturnProductDetail() {
    // Arrange
    Integer productId = 1;

    // 1. もともとのProduct（DBに存在する想定）
    Product existingProduct = new Product();
    existingProduct.setProductId(productId);
    existingProduct.setName("古い商品名");
    existingProduct.setDescription("古い説明");
    existingProduct.setPrice(500);
    existingProduct.setStock(3);
    existingProduct.setImageUrl("/old.png");
    existingProduct.setIsRecommended(false);
    existingProduct.setCategory("リビング");
    existingProduct.setMaterial("プラスチック");

    // 2. 更新内容となるProductDetail
    ProductDetail updateDetail = new ProductDetail(
            productId,
            "新しい商品名",
            "新しい説明",
            1500,
            8,
            "/new.png",
            true,
            "キッチン",
            "木"
    );

    // 3. save後のProduct（update後の内容を反映）
    Product updatedProduct = new Product();
    updatedProduct.setProductId(productId);
    updatedProduct.setName("新しい商品名");
    updatedProduct.setDescription("新しい説明");
    updatedProduct.setPrice(1500);
    updatedProduct.setStock(8);
    updatedProduct.setImageUrl("/new.png");
    updatedProduct.setIsRecommended(true);
    updatedProduct.setCategory("キッチン");
    updatedProduct.setMaterial("木");

    // モック設定
    when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

    // Act
    ProductDetail result = productService.updateProduct(productId, updateDetail);

    // Assert
    // findByIdとsaveが1回ずつ呼ばれている
    verify(productRepository, times(1)).findById(productId);
    verify(productRepository, times(1)).save(any(Product.class));
    verifyNoMoreInteractions(productRepository);

    // 返却値の内容が更新内容を反映しているか
    assertThat(result).isNotNull();
    assertThat(result.getProductId()).isEqualTo(productId);
    assertThat(result.getName()).isEqualTo("新しい商品名");
    assertThat(result.getDescription()).isEqualTo("新しい説明");
    assertThat(result.getPrice()).isEqualTo(1500);
    assertThat(result.getStock()).isEqualTo(8);
    assertThat(result.getImageUrl()).isEqualTo("/new.png");
    assertThat(result.getIsRecommended()).isTrue();
    assertThat(result.getCategory()).isEqualTo("キッチン");
    assertThat(result.getMaterial()).isEqualTo("木");
}

// 20行目
@Test
@DisplayName("updateProduct: 存在しないIDを指定した場合、saveは呼ばれずnullを返す")
void updateProduct_WhenProductNotFound_ShouldReturnNull() {
    // Arrange
    Integer notFoundProductId = 999;
    ProductDetail updateDetail = new ProductDetail(
            notFoundProductId,
            "存在しない商品",
            "説明",
            100,
            1,
            "/none.png",
            false,
            "カテゴリ",
            "素材"
    );

    // findByIdがOptional.empty()を返すようモック設定
    when(productRepository.findById(notFoundProductId)).thenReturn(Optional.empty());

    // Act
    ProductDetail result = productService.updateProduct(notFoundProductId, updateDetail);

    // Assert
    // saveは一度も呼ばれていない
    verify(productRepository, times(1)).findById(notFoundProductId);
    verify(productRepository, never()).save(any(Product.class));
    verifyNoMoreInteractions(productRepository);

    // 戻り値はnull
    assertThat(result).isNull();
}

// 21行目
@Test
@DisplayName("deleteProduct: 指定IDの商品が存在する場合、削除しtrueを返す")
void deleteProduct_WhenExists_ShouldDeleteAndReturnTrue() {
    // Arrange
    Integer productId = 1;

    // existsByIdがtrueを返すようにモック
    when(productRepository.existsById(productId)).thenReturn(true);
    // deleteByIdの副作用は不要（voidなのでdoNothingでOK）
    doNothing().when(productRepository).deleteById(productId);

    // Act
    boolean result = productService.deleteProduct(productId);

    // Assert
    // existsById, deleteByIdが1回ずつ呼ばれている
    verify(productRepository, times(1)).existsById(productId);
    verify(productRepository, times(1)).deleteById(productId);
    verifyNoMoreInteractions(productRepository);

    // 戻り値はtrue
    assertThat(result).isTrue();
}

    @Test
    @DisplayName("deleteProduct: 指定IDの商品が存在しない場合、削除されずfalseを返す")
    void deleteProduct_WhenExists_ShouldDeleteAndReturnFalse() {
    // Arrange
    Integer notFoundProductId = 999;

    // existsByIdがfalseを返すようにモック
    when(productRepository.existsById(notFoundProductId)).thenReturn(false);

    // Act
    boolean result = productService.deleteProduct(notFoundProductId);

    // Assert
    // existsByIdは1回呼ばれ、deleteByIdは呼ばれない
    verify(productRepository, times(1)).existsById(notFoundProductId);
    verify(productRepository, never()).deleteById(any());
    verifyNoMoreInteractions(productRepository);

    // 戻り値はfalse
    assertThat(result).isFalse();
}

// 23行目
    @Test
    @DisplayName("createProduct: Repository.saveが例外をスローした場合、RuntimeExceptionがスローされる")
    void createProduct_WhenRepositoryThrowsException_ShouldHandleOrPropagate() {
    // Arrange
    ProductDetail inputDetail = new ProductDetail(
            null,
            "例外商品",
            "例外説明",
            111,
            5,
            "/error.png",
            false,
            "カテゴリ",
            "素材"
    );

    // saveがRuntimeExceptionを投げるようモック
    when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThatThrownBy(() -> productService.createProduct(inputDetail))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("DB error");

    // saveが1回だけ呼ばれている
    verify(productRepository, times(1)).save(any(Product.class));
    verifyNoMoreInteractions(productRepository);
}

// 24行目
@Test
@DisplayName("updateProduct: saveが例外をスローした場合、RuntimeExceptionがスローされる")
void updateProduct_WhenSaveFails_ShouldHandleOrPropagate() {
    // Arrange
    Integer productId = 1;

    // findByIdは正常に返す
    Product existingProduct = new Product();
    existingProduct.setProductId(productId);
    existingProduct.setName("古い商品");
    // ...他フィールドも必要ならセット

    ProductDetail updateDetail = new ProductDetail(
            productId,
            "更新商品",
            "更新説明",
            500,
            3,
            "/update.png",
            false,
            "カテゴリ",
            "素材"
    );

    // findByIdはOptional.of(existingProduct)を返す
    when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
    // saveがRuntimeExceptionを投げる
    when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("DB save error"));

    // Act & Assert
    assertThatThrownBy(() -> productService.updateProduct(productId, updateDetail))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("DB save error");

    // 呼び出し回数検証
    verify(productRepository, times(1)).findById(productId);
    verify(productRepository, times(1)).save(any(Product.class));
    verifyNoMoreInteractions(productRepository);
}

// 25行目
@Test
@DisplayName("deleteProduct: deleteByIdが例外をスローした場合、RuntimeExceptionがスローされる")
void deleteProduct_WhenDeleteFails_ShouldHandleOrReturnFalse() {
    // Arrange
    Integer productId = 1;

    // existsByIdはtrueを返す
    when(productRepository.existsById(productId)).thenReturn(true);
    // deleteByIdが例外を投げる
    doThrow(new RuntimeException("DB delete error")).when(productRepository).deleteById(productId);

    // Act & Assert
    assertThatThrownBy(() -> productService.deleteProduct(productId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("DB delete error");

    // 呼び出し回数検証
    verify(productRepository, times(1)).existsById(productId);
    verify(productRepository, times(1)).deleteById(productId);
    verifyNoMoreInteractions(productRepository);
}


}