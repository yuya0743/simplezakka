# 機能: 商品表示機能

## テスト対象API:

- `GET /api/products` (商品一覧取得)
- `GET /api/products/{productId}` (商品詳細取得)

## テストデータ準備方針:

- テストの独立性を保つため、各テストケース実行前にデータベースをクリーンな状態にし、テストケースごとに必要な商品データを投入することを想定します。
- 以下では、テストデータとして投入される商品情報を `Product(ID, Name, Price, Desc, Stock, ImageUrl, IsRecommended)` の形式で記述します。

## テストシナリオ

### No. 1-1

- テストケース名: 商品一覧表示（正常系 - 商品データが複数存在する場合）
- 前提条件:
  - データベースの `products` テーブルに以下の2件の商品データが存在する。
    - `Product(1, "商品A", 1000, "説明A", 10, "/imgA.png", true)`
    - `Product(2, "商品B", 2000, "説明B", 5, "/imgB.png", false)`
- 手順:
  1. HTTP GETリクエストを `/api/products` エンドポイントに送信する。
- 入力データ: なし (GETリクエストのためボディなし)
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であること。
  3. 返却されるJSON配列が2つの要素を持っていること。
  4. 各要素が `ProductListItem` DTOの形式（`productId`, `name`, `price`, `imageUrl` フィールドを持つ）であること。
  5. 1番目の要素の内容が `{"productId": 1, "name": "商品A", "price": 1000, "imageUrl": "/imgA.png"}` であること（順序不同でも可）。
  6. 2番目の要素の内容が `{"productId": 2, "name": "商品B", "price": 2000, "imageUrl": "/imgB.png"}` であること（順序不同でも可）。

### No. 1-2

- テストケース名: 商品詳細表示（正常系 - 存在する商品IDを指定）
- 前提条件:
  - データベースの `products` テーブルに以下の商品データが存在する。
    - `Product(1, "商品A", 1000, "詳細な説明A", 10, "/imgA.png", true)`
- 手順:
  1. HTTP GETリクエストを `/api/products/1` エンドポイントに送信する。
- 入力データ:
  - パスパラメータ: `productId = 1`
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であること。
  3. 返却されるJSONオブジェクトが `ProductDetail` DTOの形式（`productId`, `name`, `price`, `description`, `stock`, `imageUrl` フィールドを持つ）であること。
  4. JSONオブジェクトの内容が `{"productId": 1, "name": "商品A", "price": 1000, "description": "詳細な説明A", "stock": 10, "imageUrl": "/imgA.png"}` であること。

### No. 1-3

- テストケース名: 商品詳細表示（異常系 - 存在しない商品IDを指定）
- 前提条件:
  - データベースの `products` テーブルに `productId = 999` のデータが存在しない。
- 手順:
  1. HTTP GETリクエストを `/api/products/999` エンドポイントに送信する。
- 入力データ:
  - パスパラメータ: `productId = 999`
- 期待結果:
  1. HTTPステータスコードが `404 Not Found` であること。
  2. レスポンスボディが空であること（またはエラーを示すJSON）。

### No. 1-4

- テストケース名: 商品詳細表示（異常系 - 不正な形式の商品IDを指定）
- 前提条件:
  - 特になし（DBの状態によらない）。
- 手順:
  1. HTTP GETリクエストを `/api/products/abc` エンドポイントに送信する。
- 入力データ:
  - パスパラメータ: `productId = "abc"` (数値ではない)
- 期待結果:
  1. HTTPステータスコードが `400 Bad Request` または `500 Internal Server Error` であること（Spring Bootのデフォルトや `GlobalExceptionHandler` の実装に依存。今回は `GlobalExceptionHandler` が `RuntimeException` を500で処理するため、500になる可能性が高い）。
  2. レスポンスボディにエラー情報（例: 型変換エラーを示すメッセージ）が含まれること。
  3. `ProductController` の `getProductById` メソッド自体が呼び出される前に、Spring MVCのディスパッチャーサーブレットまたはパラメータバインディングの段階でエラーが発生する。
  4. `ProductService` や `ProductRepository` のメソッドは呼び出されないこと。

### No. 1-5

- テストケース名: 商品一覧表示（正常系 - 商品データが0件の場合）
- 前提条件:
  - データベースの `products` テーブルが空。
- 手順:
  1. HTTP GETリクエストを `/api/products` エンドポイントに送信する。
- 入力データ: なし
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式の空の配列 `[]` であること。

### No. 1-6

- テストケース名: 商品詳細表示（正常系 - descriptionやimageUrlがnullの商品）
- 前提条件:
  - データベースの `products` テーブルに以下の商品データが存在する。
    - `Product(3, "商品C", 3000, null, 8, null, false)`
- 手順:
  1. HTTP GETリクエストを `/api/products/3` エンドポイントに送信する。
- 入力データ:
  - パスパラメータ: `productId = 3`
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であること。
  3. 返却されるJSONオブジェクトが `ProductDetail` DTOの形式であること。
  4. JSONオブジェクトの内容が `{"productId": 3, "name": "商品C", "price": 3000, "description": null, "stock": 8, "imageUrl": null}` であること（`description` と `imageUrl` が `null` であることを確認）。
