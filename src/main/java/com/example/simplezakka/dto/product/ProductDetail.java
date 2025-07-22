package com.example.simplezakka.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetail {
    private Integer productId;

    @NotBlank(message = "商品名は必須です")
    private String name;

    @Size(max = 300, message = "説明文は300字以内でなければなりません")  // 300字以内
    private String description;

    @NotNull(message = "価格は必須です")
    @Min(value = 1, message = "価格は1以上でなければなりません")  // ★ここで正の数を制限
    private Integer price;

    @NotNull(message = "在庫数は必須です")
    @Min(value = 0, message = "在庫数は0以上でなければなりません")  // 0はOK、負数はNG
    private Integer stock;

    private String imageUrl;

    private Boolean isRecommended;
    private String category; 
    private String material;

}
