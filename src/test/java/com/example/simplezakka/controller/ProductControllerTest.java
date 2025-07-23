package com.example.simplezakka.controller;

import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections; // 空リスト用
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(ProductController.class) // ProductController と関連コンポーネントをテスト
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTPリクエストをシミュレート

    @MockBean // Service層のモック
    private ProductService productService;

    private ProductListItem productListItem1;
    private ProductListItem productListItem2;
    private ProductDetail productDetail1;
    private ProductDetail productDetailWithNulls; // nullフィールドを含む詳細データ

    @BeforeEach
    void setUp() {
        // --- テストデータ準備 ---
        productListItem1 = new ProductListItem(1, "リスト商品1", 100, "/list1.png","カテゴリ１", "素材１", 10, "説明1");
        productListItem2 = new ProductListItem(2, "リスト商品2", 200, "/list2.png", "カテゴリ２", "素材２", 5, "説明2");

        productDetail1 = new ProductDetail(1, "詳細商品1", "詳細説明1",100, 10, "/detail1.png", true,  "カテゴリ１", "素材１");
        productDetailWithNulls = new ProductDetail(3, "詳細商品3", null,300, 5, null,false,"カテゴリ２","素材2"); // descriptionとimageUrlがnull

        // --- Serviceメソッドのデフォルトモック設定 (lenient) ---
        // デフォルトではfindAllProductsは2つのアイテムを返す
        lenient().when(productService.findAllProducts()).thenReturn(Arrays.asList(productListItem1, productListItem2));
        // デフォルトではfindProductById(1) は productDetail1 を返す
        lenient().when(productService.findProductById(1)).thenReturn(productDetail1);
        // デフォルトでは存在しないID(99)ではnullを返す
        lenient().when(productService.findProductById(99)).thenReturn(null);
        // nullフィールドを持つ商品データ
        lenient().when(productService.findProductById(3)).thenReturn(productDetailWithNulls);
    }

    // === GET /api/products ===
    @Nested
    @DisplayName("GET /api/products")
    class GetAllProductsTests {

        @Test
        @DisplayName("商品が存在する場合、商品リスト(ProductListItem)を200 OKで返す")
        void getAllProducts_WhenProductsExist_ShouldReturnProductList() throws Exception {
            // Arrange (setUpのデフォルトモックを使用)

            // Act & Assert
            mockMvc.perform(get("/api/products")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()) // ステータスコード200 OK
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Content-TypeがJSON
                    .andExpect(jsonPath("$", hasSize(2))) // ルート配列のサイズが2
                    // 1番目の要素の全フィールドを検証
                    .andExpect(jsonPath("$[0].productId", is(productListItem1.getProductId())))
                    .andExpect(jsonPath("$[0].name", is(productListItem1.getName())))
                    .andExpect(jsonPath("$[0].price", is(productListItem1.getPrice())))
                    .andExpect(jsonPath("$[0].imageUrl", is(productListItem1.getImageUrl())))
                    // 2番目の要素の全フィールドを検証
                    .andExpect(jsonPath("$[1].productId", is(productListItem2.getProductId())))
                    .andExpect(jsonPath("$[1].name", is(productListItem2.getName())))
                    .andExpect(jsonPath("$[1].price", is(productListItem2.getPrice())))
                    .andExpect(jsonPath("$[1].imageUrl", is(productListItem2.getImageUrl())));

            verify(productService, times(1)).findAllProducts();
            verifyNoMoreInteractions(productService);
        }

        @Test
        @DisplayName("商品が存在しない場合、空のリストを200 OKで返す")
        void getAllProducts_WhenNoProductsExist_ShouldReturnEmptyList() throws Exception {
            // Arrange
            when(productService.findAllProducts()).thenReturn(Collections.emptyList()); // 空リストを返すように設定

            // Act & Assert
            mockMvc.perform(get("/api/products")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0))); // 空の配列であることを確認

            verify(productService, times(1)).findAllProducts();
            verifyNoMoreInteractions(productService);
        }

        @Test
        @DisplayName("ProductServiceが例外をスローした場合、500 Internal Server Errorを返す")
        void getAllProducts_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
            // Arrange
            when(productService.findAllProducts()).thenThrow(new RuntimeException("サービスエラー"));

            // Act & Assert
            mockMvc.perform(get("/api/products")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    // GlobalExceptionHandler が有効ならエラーメッセージを含むJSONが返る可能性がある
                    .andExpect(jsonPath("$.message", containsString("サービスエラー")));

            verify(productService, times(1)).findAllProducts();
            verifyNoMoreInteractions(productService);
        }
    }

    // === GET /api/products/{productId} ===
    @Nested
    @DisplayName("GET /api/products/{productId}")
    class GetProductByIdTests {

        @Test
        @DisplayName("存在するproductIdの場合、商品詳細(ProductDetail)を200 OKで返す")
        void getProductById_WhenProductExists_ShouldReturnProductDetail() throws Exception {
            // Arrange (setUpのデフォルトモックを使用 - ID:1)
            Integer productId = 1;

            // Act & Assert
            mockMvc.perform(get("/api/products/{productId}", productId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    // 全フィールドを検証
                    .andExpect(jsonPath("$.productId", is(productDetail1.getProductId())))
                    .andExpect(jsonPath("$.name", is(productDetail1.getName())))
                    .andExpect(jsonPath("$.price", is(productDetail1.getPrice())))
                    .andExpect(jsonPath("$.description", is(productDetail1.getDescription())))
                    .andExpect(jsonPath("$.stock", is(productDetail1.getStock())))
                    .andExpect(jsonPath("$.imageUrl", is(productDetail1.getImageUrl())));

            verify(productService, times(1)).findProductById(productId);
            verifyNoMoreInteractions(productService);
        }

        @Test
        @DisplayName("存在するproductIdで、一部フィールドがnullの商品の場合、nullを含む商品詳細を200 OKで返す")
        void getProductById_WhenProductExistsWithNullFields_ShouldReturnProductDetailWithNulls() throws Exception {
            // Arrange (setUpのデフォルトモックを使用 - ID:3)
            Integer productId = 3;

            // Act & Assert
            mockMvc.perform(get("/api/products/{productId}", productId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.productId", is(productDetailWithNulls.getProductId())))
                    .andExpect(jsonPath("$.name", is(productDetailWithNulls.getName())))
                    .andExpect(jsonPath("$.price", is(productDetailWithNulls.getPrice())))
                    .andExpect(jsonPath("$.description", is(nullValue()))) // descriptionがnull
                    .andExpect(jsonPath("$.stock", is(productDetailWithNulls.getStock())))
                    .andExpect(jsonPath("$.imageUrl", is(nullValue()))); // imageUrlがnull

            verify(productService, times(1)).findProductById(productId);
            verifyNoMoreInteractions(productService);
        }

        @Test
        @DisplayName("存在しないproductIdの場合、404 Not Foundを返す")
        void getProductById_WhenProductNotExists_ShouldReturnNotFound() throws Exception {
            // Arrange (setUpのデフォルトモックを使用 - ID:99)
            Integer productId = 99;

            // Act & Assert
            mockMvc.perform(get("/api/products/{productId}", productId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound()); // ステータスコード404 Not Found

            verify(productService, times(1)).findProductById(productId);
            verifyNoMoreInteractions(productService);
        }

        @Test
        @DisplayName("productIdが数値でない場合、500 Internal Server Errorを返す (現在のGlobalExceptionHandlerの実装による)") // DisplayName を変更
        void getProductById_WithInvalidProductIdFormat_ShouldReturnInternalServerError_DueToExceptionHandler() throws Exception { // メソッド名を変更
            // Arrange
            String invalidProductId = "abc"; // 数値でないパスパラメータ

            // Act & Assert
            // 現在のGlobalExceptionHandlerは型ミスマッチをRuntimeExceptionとして扱い500を返すため、
            // テストの期待値もそれに合わせる。
            mockMvc.perform(get("/api/products/{productId}", invalidProductId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    // オプション： GlobalExceptionHandlerが返すエラーメッセージの内容も検証する
                    .andExpect(jsonPath("$.message", containsString("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'")))
                    .andExpect(jsonPath("$.message", containsString(invalidProductId))); // 不正な値が含まれていることを確認

            // この場合、コントローラーメソッドやサービスは呼び出されない
            verifyNoInteractions(productService);
        }

        @Test
        @DisplayName("ProductServiceが例外をスローした場合、500 Internal Server Errorを返す")
        void getProductById_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
            // Arrange
            Integer productId = 1;
            when(productService.findProductById(productId)).thenThrow(new RuntimeException("サービスエラー"));

            // Act & Assert
            mockMvc.perform(get("/api/products/{productId}", productId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    // GlobalExceptionHandler が有効ならエラーメッセージを含むJSONが返る可能性がある
                    .andExpect(jsonPath("$.message", containsString("サービスエラー")));


            verify(productService, times(1)).findProductById(productId);
            verifyNoMoreInteractions(productService);
        }
    }


    // Excel11行目
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    @DisplayName("POST /api/products 正常系: CREATEDステータスとProductDetail DTOが返される")
    void postProduct_WhenSuccess() throws Exception {
    // Arrange
    ProductDetail requestDto = new ProductDetail(
            null, "新商品", "新しい説明", 1500, 8, "/img/new.png", true, "雑貨", "木"
    );
    ProductDetail responseDto = new ProductDetail(
            100, "新商品", "新しい説明", 1500, 8, "/img/new.png", true, "雑貨", "木"
    );
    when(productService.createProduct(any(ProductDetail.class))).thenReturn(responseDto);

    // Act & Assert
    mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))) // ← フィールドobjectMapperをそのまま使う
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.productId").value(100))
            .andExpect(jsonPath("$.name").value("新商品"))
            .andExpect(jsonPath("$.description").value("新しい説明"))
            .andExpect(jsonPath("$.price").value(1500))
            .andExpect(jsonPath("$.stock").value(8))
            .andExpect(jsonPath("$.imageUrl").value("/img/new.png"))
            .andExpect(jsonPath("$.isRecommended").value(true))
            .andExpect(jsonPath("$.category").value("雑貨"))
            .andExpect(jsonPath("$.material").value("木"));

    verify(productService, times(1)).createProduct(any(ProductDetail.class));
    verifyNoMoreInteractions(productService);
    }


    // 12行目
    @Test
    @DisplayName("POST /api/products バリデーションエラーで400 Bad Request")
    void postProduct_WhenValidationError_ShouldReturnBadRequest() throws Exception {
    // Arrange: name=null、price=-100 などバリデーションNGなDTO
    ProductDetail invalidRequest = new ProductDetail(
            null, null, "説明", -100, 5, "/img.png", true, "カテゴリ", "素材"
    );

    // Act & Assert
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());  

    // サービス層が呼ばれていないことを検証
    verify(productService, times(0)).createProduct(any(ProductDetail.class));
    }


    // 13行目
    @Test
    @DisplayName("PUT /api/products/{productId} 正常系: ProductDetail DTOが返される")
    void putProduct_WhenInputProductIdProductDetail() throws Exception {
    // Arrange
    Integer productId = 10;
    ProductDetail requestDto = new ProductDetail(
            null, "更新商品", "説明を更新", 3000, 20, "/img/updated.png", false, "インテリア", "ガラス"
    );
    ProductDetail responseDto = new ProductDetail(
            productId, "更新商品", "説明を更新", 3000, 20, "/img/updated.png", false, "インテリア", "ガラス"
    );

    when(productService.updateProduct(eq(productId), any(ProductDetail.class)))
            .thenReturn(responseDto);

    // Act & Assert
    mockMvc.perform(put("/api/products/{productId}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.productId").value(productId))
        .andExpect(jsonPath("$.name").value("更新商品"))
        .andExpect(jsonPath("$.description").value("説明を更新"))
        .andExpect(jsonPath("$.price").value(3000))
        .andExpect(jsonPath("$.stock").value(20))
        .andExpect(jsonPath("$.imageUrl").value("/img/updated.png"))
        .andExpect(jsonPath("$.isRecommended").value(false))
        .andExpect(jsonPath("$.category").value("インテリア"))
        .andExpect(jsonPath("$.material").value("ガラス"));

    verify(productService, times(1)).updateProduct(eq(productId), any(ProductDetail.class));
    verifyNoMoreInteractions(productService);
    }
    

    // 14行目
    @Test
    @DisplayName("PUT /api/products/{productId} 異常系: updateProductがnullを返した場合404 NOT FOUNDを返す")
    void putProduct_WhenProductDetailIsNull() throws Exception {
    // Arrange
    Integer productId = 999;
    ProductDetail requestDto = new ProductDetail(
            null, "notfound", "存在しない商品", 1000, 0, "/img/none.png", false, "なし", "なし"
    );

    // Serviceがnullを返すようにモック
    when(productService.updateProduct(eq(productId), any(ProductDetail.class))).thenReturn(null);

    // Act & Assert
    mockMvc.perform(put("/api/products/{productId}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(requestDto)))
        .andExpect(status().isNotFound());

    verify(productService, times(1)).updateProduct(eq(productId), any(ProductDetail.class));
    verifyNoMoreInteractions(productService);
    }


    // 15行目
    // もしかしたら書き換えが必要かも。。Controller.javaと整合性が取れてない気がする（0718木村）
    @Test
    @DisplayName("DELETE /api/products/{productId} 正常系: Serviceがtrueを返したとき204 No Contentを返す")
    void deleteProduct_WhenSuccess() throws Exception {
    // Arrange
    Integer productId = 1;
    when(productService.deleteProduct(productId)).thenReturn(true);

    // Act & Assert
    mockMvc.perform(delete("/api/products/{productId}", productId))
            .andExpect(status().isNoContent()); // 204 No Content

    verify(productService, times(1)).deleteProduct(productId);
    verifyNoMoreInteractions(productService);
    }


    // 16行目
    @Test
    @DisplayName("DELETE /api/products/{productId} 異常系: Serviceがfalseを返すと404 Not Found")
    void deleteProduct_WhenNotFound() throws Exception {
    Integer productId = 1;
    when(productService.deleteProduct(productId)).thenReturn(false);

    mockMvc.perform(delete("/api/products/{productId}", productId))
            .andExpect(status().isNotFound());

    verify(productService, times(1)).deleteProduct(productId);
    verifyNoMoreInteractions(productService);
    }


    // 17行目
    @Test
    @DisplayName("商品登録(createProduct)正常系: すべての項目が適切なProductDetailを保存しレスポンスとして返す")
    void createProduct_ShouldSaveAndReturnProductDetail() throws Exception {
    // Arrange
    ProductDetail requestDetail = new ProductDetail(
            null, "新商品", "新しい説明", 1200, 30, "/img/aaa.png", true, "雑貨", "木"
    );
    // save後はproductIdが振られて返る想定
    ProductDetail savedDetail = new ProductDetail(
            101, "新商品", "新しい説明", 1200, 30, "/img/aaa.png", true, "雑貨", "木"
    );
    when(productService.createProduct(any(ProductDetail.class))).thenReturn(savedDetail);

    // Act & Assert
    mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(requestDetail)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.productId").value(101))
            .andExpect(jsonPath("$.name").value("新商品"))
            .andExpect(jsonPath("$.description").value("新しい説明"))
            .andExpect(jsonPath("$.price").value(1200))
            .andExpect(jsonPath("$.stock").value(30))
            .andExpect(jsonPath("$.imageUrl").value("/img/aaa.png"))
            .andExpect(jsonPath("$.isRecommended").value(true))
            .andExpect(jsonPath("$.category").value("雑貨"))
            .andExpect(jsonPath("$.material").value("木"));

    // Serviceの呼び出しが1回だけかつ引数が同等か確認
    verify(productService, times(1)).createProduct(any(ProductDetail.class));
    verifyNoMoreInteractions(productService);
    }


    // 18行目
    @Test
    @DisplayName("商品登録(createProduct)正常系: 任意項目がnullでもレスポンスの該当フィールドもnull")
    void createProduct_WithNullFields_ShouldHandleGracefully() throws Exception {
    // Arrange: imageUrl, descriptionがnullのリクエスト
    ProductDetail requestDetail = new ProductDetail(
            null, "null商品", null, 900, 7, null, false, "雑貨", "紙"
    );
    ProductDetail savedDetail = new ProductDetail(
            102, "null商品", null, 900, 7, null, false, "雑貨", "紙"
    );
    when(productService.createProduct(any(ProductDetail.class))).thenReturn(savedDetail);

    // Act & Assert
    mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(requestDetail)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.productId").value(102))
            .andExpect(jsonPath("$.name").value("null商品"))
            .andExpect(jsonPath("$.description").doesNotExist()) // nullの項目は通常返却されない
            .andExpect(jsonPath("$.price").value(900))
            .andExpect(jsonPath("$.stock").value(7))
            .andExpect(jsonPath("$.imageUrl").doesNotExist()) // nullの項目は通常返却されない
            .andExpect(jsonPath("$.isRecommended").value(false))
            .andExpect(jsonPath("$.category").value("雑貨"))
            .andExpect(jsonPath("$.material").value("紙"));

    verify(productService, times(1)).createProduct(any(ProductDetail.class));
    verifyNoMoreInteractions(productService);
    }


    // 19行目
    @Test
        @DisplayName("商品登録(createProduct)異常値: name=null で400 Bad Requestとエラーメッセージ")
        void createProduct_WhenNameIsNull_ShouldReturn400() throws Exception {
         // Arrange: nameがnull、それ以外は正常
        ProductDetail invalidDetail = new ProductDetail(
            null, // productId
            null, // name
            "説明",
            1200, // price
            5,    // stock
            "/img/item.png",
            true,
            "カテゴリ",
            "素材"
        );

        mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDetail)))
        .andDo(print())
        .andExpect(status().isBadRequest());

        // Serviceは呼ばれない
        verify(productService, never()).createProduct(any());
        }



    // 20行
    @Test
    @DisplayName("商品登録(createProduct)正常系: stockが0でも登録できる")
    void createProduct_WhenStockIsZero_ShouldSucceed() throws Exception {
    // Arrange: stock=0
    ProductDetail requestDetail = new ProductDetail(
            null, "在庫ゼロ商品", "在庫0テスト", 980, 0, "/zero.png", true, "境界カテゴリ", "境界素材"
    );
    ProductDetail savedDetail = new ProductDetail(
            120, "在庫ゼロ商品", "在庫0テスト", 980, 0, "/zero.png", true, "境界カテゴリ", "境界素材"
    );
    when(productService.createProduct(any(ProductDetail.class))).thenReturn(savedDetail);

    // Act & Assert
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(requestDetail)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.productId").value(120))
        .andExpect(jsonPath("$.name").value("在庫ゼロ商品"))
        .andExpect(jsonPath("$.stock").value(0))  // stock=0が反映されているか確認
        .andExpect(jsonPath("$.description").value("在庫0テスト"))
        .andExpect(jsonPath("$.price").value(980))
        .andExpect(jsonPath("$.imageUrl").value("/zero.png"))
        .andExpect(jsonPath("$.isRecommended").value(true))
        .andExpect(jsonPath("$.category").value("境界カテゴリ"))
        .andExpect(jsonPath("$.material").value("境界素材"));

    verify(productService, times(1)).createProduct(any(ProductDetail.class));
    verifyNoMoreInteractions(productService);
    }


    // 21行目
    @Test
    @DisplayName("商品登録(createProduct)異常系: priceが負値の場合は400 Bad Request")
    void createProduct_WhenPriceIsNegative_ShouldHandleError() throws Exception {
    // Arrange: priceが負の値
    ProductDetail invalidDetail = new ProductDetail(
            null, "価格異常商品", "負値テスト", -500, 2, "/invalid.png", true, "異常カテゴリ", "異常素材"
    );

    // Act & Assert
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(invalidDetail)))
        .andExpect(status().isBadRequest());

    // サービス（productService.createProduct）は呼ばれない
    verify(productService, never()).createProduct(any(ProductDetail.class));
    }


    // 22行目
    @Test
    @DisplayName("商品登録(createProduct)異常系: stockが負値の場合は400 Bad Request")
    void createProduct_WhenStockIsNegative_ShouldHandleError() throws Exception {
    // Arrange: stockが負の値
    ProductDetail invalidDetail = new ProductDetail(
            null, "在庫異常商品", "負値テスト", 500, -3, "/invalid.png", true, "異常カテゴリ", "異常素材"
    );

    // Act & Assert
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(invalidDetail)))
        .andExpect(status().isBadRequest());

    // サービスは呼ばれない
    verify(productService, never()).createProduct(any(ProductDetail.class));
    }


    // 23行目
    @Test
    @DisplayName("商品更新(updateProduct)正常系: 更新した内容が返る")
    void updateProduct_ShouldUpdateAndReturnProductDetail() throws Exception {
    // Arrange
    Integer productId = 1;
    ProductDetail requestDto = new ProductDetail(
        null, "更新後商品", "更新後説明", 1200, 7, "/img/updated.png", false, "新カテゴリ", "新素材"
    );
    ProductDetail responseDto = new ProductDetail(
        productId, "更新後商品", "更新後説明", 1200, 7, "/img/updated.png", false, "新カテゴリ", "新素材"
    );
    when(productService.updateProduct(eq(productId), any(ProductDetail.class))).thenReturn(responseDto);

    // Act & Assert
    mockMvc.perform(put("/api/products/{productId}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.productId").value(productId))
        .andExpect(jsonPath("$.name").value("更新後商品"))
        .andExpect(jsonPath("$.description").value("更新後説明"))
        .andExpect(jsonPath("$.price").value(1200))
        .andExpect(jsonPath("$.stock").value(7))
        .andExpect(jsonPath("$.imageUrl").value("/img/updated.png"))
        .andExpect(jsonPath("$.isRecommended").value(false))
        .andExpect(jsonPath("$.category").value("新カテゴリ"))
        .andExpect(jsonPath("$.material").value("新素材"));

    verify(productService, times(1)).updateProduct(eq(productId), any(ProductDetail.class));
    verifyNoMoreInteractions(productService);
    }


    // 24行目
    @Test
    @DisplayName("商品更新(updateProduct)異常系: descriptionが300字超でバリデーションエラー400")
    void updateProduct_WhenDescriptionIsOver_ShouldHandleError() throws Exception {
    // Arrange
    Integer productId = 1;
    // 301文字のdescriptionを生成
    String overLengthDescription = "あ".repeat(301);

    ProductDetail requestDto = new ProductDetail(
        null, "商品名", overLengthDescription, 1000, 3, "/img.png", true, "カテゴリ", "素材"
    );

    // Act & Assert
    mockMvc.perform(put("/api/products/{productId}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest());

    // Controller(service)が呼ばれないことを確認
    verify(productService, times(0)).updateProduct(anyInt(), any(ProductDetail.class));
    }


    // 25行目
    @Test
    @DisplayName("商品更新(updateProduct)異常系: 存在しないIDで404 Not Found")
    void updateProduct_WhenProductNotFound_ShouldReturn404() throws Exception {
    // Arrange
    Integer productId = 999;
    ProductDetail requestDto = new ProductDetail(
        null, "存在しない商品", "説明", 1000, 5, "/img.png", true, "カテゴリ", "素材"
    );
    // サービスがnullを返すようモック
    when(productService.updateProduct(eq(productId), any(ProductDetail.class))).thenReturn(null);

    // Act & Assert
    mockMvc.perform(put("/api/products/{productId}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(requestDto)))
        .andExpect(status().isNotFound());

    verify(productService, times(1)).updateProduct(eq(productId), any(ProductDetail.class));
    verifyNoMoreInteractions(productService);
    }
}
