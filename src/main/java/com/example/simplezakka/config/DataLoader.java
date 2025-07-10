package com.example.simplezakka.config;

import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Autowired
    public DataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        loadSampleProducts();
    }

    private void loadSampleProducts() {
        if (productRepository.count() > 0) {
            return; // すでにデータが存在する場合はスキップ
        }

        List<Product> products = Arrays.asList(
            createProduct(
                "シンプルデスクオーガナイザー", 
                "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。", 
                3500, 
                20, 
                "/images/desk-organizer.png", 
                true,
                "デスク周り",
                "木製"
                
                

                
            ),
            createProduct(
                "アロマディフューザー（ウッド）", 
                "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。", 
                4200, 
                15, 
                "/images/aroma-diffuser.png", 
                true,
                "インテリア・雑貨",
                "木製"
            ),
            createProduct(
                "コットンブランケット", 
                "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。", 
                5800, 
                10, 
                "/images/cotton-blanket.png", 
                false,
                "家具・寝具",
                "コットン"

            ),
            createProduct(
                "ステンレスタンブラー", 
                "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。", 
                2800, 
                30, 
                "/images/tumbler.png", 
                false,
                "キッチン用品",
                "ステンレス"
            ),
            createProduct(
                "ミニマルウォールクロック", 
                "余計な装飾のないシンプルな壁掛け時計。静音設計。", 
                3200, 
                25, 
                "/images/wall-clock.png", 
                false,
                "インテリア・雑貨",
                "木製"
            ),
            createProduct(
                "リネンクッションカバー", 
                "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。", 
                2500, 
                40, 
                "/images/cushion-cover.png", 
                true,
                "家具・寝具",
                "リネン"
            ),
            createProduct(
                "陶器フラワーベース", 
                "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。", 
                4000, 
                15, 
                "/images/flower-vase.png", 
                false,
                "インテリア・雑貨",
                "陶器"
            ),
            createProduct(
                "木製コースター（4枚セット）", 
                "天然木を使用したシンプルなデザインのコースター。4枚セット。", 
                1800, 
                50, 
                "/images/wooden-coaster.png", 
                false,
                "インテリア・雑貨",
                "木製"
            ),
            createProduct(
                "キャンバストートバッグ", 
                "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。", 
                3600, 
                35, 
                "/images/tote-bag.png", 
                true,
                "バッグ・トラベル",
                "キャンバス"
            ),
            createProduct(
                "ガラス保存容器セット", 
                "電子レンジ・食洗機対応のガラス製保存容器。3サイズセット。", 
                4500, 
                20, 
                "/images/glass-container.png", 
                false,
                "キッチン用品",
                "ガラス"
            )
        );
        

        productRepository.saveAll(products);
    }
    
    private Product createProduct(String name, String description, Integer price, Integer stock, String imageUrl, Boolean isRecommended,String category, String material) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl(imageUrl);
        product.setIsRecommended(isRecommended);
        product.setCategory(category); // Assuming a default category
        product.setMaterial(material); // Assuming a default material
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }
}