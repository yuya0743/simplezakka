# 機能: 商品表示機能

## テスト対象API:

- `GET /api/products` (商品一覧取得)
- `GET /api/products/{productId}` (商品詳細取得)

## テストデータ準備方針:

- テストの独立性を保つため、各テストケース実行前にデータベースをクリーンな状態にし、テストケースごとに必要な商品データを投入することを想定します。
- 以下では、テストデータとして投入される商品情報を `Product(ID, Name, Price, Desc, Stock, ImageUrl, IsRecommended)` の形式で記述します。

## テストシナリオ

### No. 1-1,1-2

- テストケース名: 商品一覧表示及び製品詳細表示（正常系 - 商品データが複数存在する場合）
- 前提条件:
  - データベースの `products` テーブルに以下の10件の商品データが存在する。
`Product(1, "シンプルデスクオーガナイザー", 3500, "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。", 20, "/images/desk-organizer.png", true),
Product(2, "アロマディフューザー（ウッド）", 4200, "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。", 15, "/images/aroma-diffuser.png", true),
Product(3, "コットンブランケット", 5800, "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。", 10, "/images/cotton-blanket.png", false),
Product(4, "ステンレスタンブラー", 2800, "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。", 30, "/images/tumbler.png", false),
Product(5, "ミニマルウォールクロック", 3200, "余計な装飾のないシンプルな壁掛け時計。静音設計。", 25, "/images/wall-clock.png", false),
Product(6, "リネンクッションカバー", 2500, "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。", 40, "/images/cushion-cover.png", true),
Product(7, "陶器フラワーベース", 4000, "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。", 15, "/images/flower-vase.png", false),
Product(8, "木製コースター（4枚セット）", 1800, "天然木を使用したシンプルなデザインのコースター。4枚セット。", 50, "/images/wooden-coaster.png", false),
Product(9, "キャンバストートバッグ", 3600, "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。", 35, "/images/tote-bag.png", true),
- 手順:
  1. HTTP GETリクエストを `/api/products` エンドポイントに送信する。
- 入力データ: なし (GETリクエストのためボディなし)
-期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であること。
  3. 返却されるJSONオブジェクトが `ProductDetail` DTOの形式（`productId`, `name`, `price`, `description`, `stock`, `imageUrl` ,`isRecommended`フィールドを持つ）であること。
  4. JSONオブジェクトの内容が `{{
    "productId": 1,
    "name": "シンプルデスクオーガナイザー",
    "price": 3500,
    "description": "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。",
    "stock": 20,
    "imageUrl": "/images/desk-organizer.png"
  },
  {
    "productId": 2,
    "name": "アロマディフューザー（ウッド）",
    "price": 4200,
    "description": "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。",
    "stock": 15,
    "imageUrl": "/images/aroma-diffuser.png"
  },
  {
    "productId": 3,
    "name": "コットンブランケット",
    "price": 5800,
    "description": "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。",
    "stock": 10,
    "imageUrl": "/images/cotton-blanket.png"
  },
  {
    "productId": 4,
    "name": "ステンレスタンブラー",
    "price": 2800,
    "description": "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。",
    "stock": 30,
    "imageUrl": "/images/tumbler.png"
  },
  {
    "productId": 5,
    "name": "ミニマルウォールクロック",
    "price": 3200,
    "description": "余計な装飾のないシンプルな壁掛け時計。静音設計。",
    "stock": 25,
    "imageUrl": "/images/wall-clock.png"
  },
  {
    "productId": 6,
    "name": "リネンクッションカバー",
    "price": 2500,
    "description": "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。",
    "stock": 40,
    "imageUrl": "/images/cushion-cover.png"
  },
  {
    "productId": 7,
    "name": "陶器フラワーベース",
    "price": 4000,
    "description": "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。",
    "stock": 15,
    "imageUrl": "/images/flower-vase.png"
  },
  {
    "productId": 8,
    "name": "木製コースター（4枚セット）",
    "price": 1800,
    "description": "天然木を使用したシンプルなデザインのコースター。4枚セット。",
    "stock": 50,
    "imageUrl": "/images/wooden-coaster.png"
  },
  {
    "productId": 9,
    "name": "キャンバストートバッグ",
    "price": 3600,
    "description": "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。",
    "stock": 35,
    "imageUrl": "/images/tote-bag.png"
  }}` であること。

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
