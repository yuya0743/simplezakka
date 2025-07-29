# 機能: カート機能

## テスト対象API:

- `GET /api/cart` (カート情報取得)
- `POST /api/cart` (カートに商品追加)
- `PUT /api/cart/items/{itemId}` (カート内商品の数量変更)
- `DELETE /api/cart/items/{itemId}` (カート内商品の削除)

## テストデータ準備方針:

- 各テストケース実行前に、セッションをクリア（または新しいセッションを使用）し、必要に応じてDBに商品データを準備します。
- 商品情報は `Product(ID, Name, Price, Stock, ...)` の形式で記述します。
- カート情報は `Cart(items={itemId: CartItem(..., quantity, subtotal)}, totalQuantity, totalPrice)` の形式で記述します。

## テストシナリオ

### No. 2-1

- テストケース名: カート追加（正常系 - 空のカートにはじめて商品を追加）
- 前提条件:
  - DBに商品 `Product(1, "シンプルデスクオーガナイザー", 3500, "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。", 20, "/images/desk-organizer.png", true),
Product(2, "アロマディフューザー（ウッド）", 4200, "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。", 15, "/images/aroma-diffuser.png", true),
Product(3, "コットンブランケット", 5800, "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。", 10, "/images/cotton-blanket.png", false),
Product(4, "ステンレスタンブラー", 2800, "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。", 30, "/images/tumbler.png", false),
Product(5, "ミニマルウォールクロック", 3200, "余計な装飾のないシンプルな壁掛け時計。静音設計。", 25, "/images/wall-clock.png", false),
Product(6, "リネンクッションカバー", 2500, "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。", 40, "/images/cushion-cover.png", true),
Product(7, "陶器フラワーベース", 4000, "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。", 15, "/images/flower-vase.png", false),
Product(8, "木製コースター（4枚セット）", 1800, "天然木を使用したシンプルなデザインのコースター。4枚セット。", 50, "/images/wooden-coaster.png", false),
Product(9, "キャンバストートバッグ", 3600, "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。", 35, "/images/tote-bag.png", true),` が存在する。
  - セッションにカート情報が存在しない（または空）。
- 手順:
  1. HTTP POSTリクエストを `/api/cart` エンドポイントに送信する。リクエストボディに指定の商品IDと数量を含める。
- 入力データ:
  - リクエストボディ(JSON): `{"productId": 1, "quantity": 2}`
  - セッション: 空
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であり、以下のカート情報を含むこと:
      "items": {
    "1": {
      "id": "1",
      "productId": 1,
      "name": "シンプルデスクオーガナイザー",
      "price": 3500,
      "imageUrl": "/images/desk-organizer.png",
      "quantity": 2,
      "subtotal": 7000
    }
  },
  "totalQuantity": 2,
  "totalPrice": 7000
  1. セッションに `cart` 属性が保存され、その内容がレスポンスボディと同じカート情報であること。
  2. `CartService.addItemToCart` が引数 `productId=1`, `quantity=2` で1回呼び出されること。
  3. `ProductRepository.findById` が引数 `1` で1回呼び出されること。

### No. 2-2

- テストケース名: カート追加（正常系 - すでにカートにある商品と同じ商品をさらに追加）
- 前提条件:
  - DBに商品 `Product(1, "シンプルデスクオーガナイザー", 3500, "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。", 20, "/images/desk-organizer.png", true),
Product(2, "アロマディフューザー（ウッド）", 4200, "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。", 15, "/images/aroma-diffuser.png", true),
Product(3, "コットンブランケット", 5800, "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。", 10, "/images/cotton-blanket.png", false),
Product(4, "ステンレスタンブラー", 2800, "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。", 30, "/images/tumbler.png", false),
Product(5, "ミニマルウォールクロック", 3200, "余計な装飾のないシンプルな壁掛け時計。静音設計。", 25, "/images/wall-clock.png", false),
Product(6, "リネンクッションカバー", 2500, "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。", 40, "/images/cushion-cover.png", true),
Product(7, "陶器フラワーベース", 4000, "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。", 15, "/images/flower-vase.png", false),
Product(8, "木製コースター（4枚セット）", 1800, "天然木を使用したシンプルなデザインのコースター。4枚セット。", 50, "/images/wooden-coaster.png", false),
Product(9, "キャンバストートバッグ", 3600, "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。", 35, "/images/tote-bag.png", true),`   が存在する。
  - セッションに以下のカート情報が存在する:
    - `Cart(items={"1": CartItem(..., quantity=1, subtotal=3500)}, totalQuantity=1, totalPrice=3500)`
- 手順:
  1. HTTP POSTリクエストを `/api/cart` エンドポイントに送信する。リクエストボディに同じ商品IDと追加する数量を含める。
- 入力データ:
  - リクエストボディ(JSON): `{"productId": 1, "quantity": 3}`
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であり、以下のカート情報を含むこと:
     - `items`: `{"1": {"id": "1", ..., "quantity": 4, "subtotal": 14000}}` (数量が 1 + 3 = 4 になる)
     - `totalQuantity`: 4
     - `totalPrice`: 
  3. セッションの `cart` 属性の内容がレスポンスボディと同じカート情報に更新されていること。
  4. `CartService.addItemToCart` が引数 `productId=1`, `quantity=3` で1回呼び出されること。

### No. 2-3

- テストケース名: カート追加（正常系 - カートに存在する商品と別の商品を追加）
- 前提条件:
  - DBに商品 `Product(1, "シンプルデスクオーガナイザー", 3500, "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。", 20, "/images/desk-organizer.png", true),
Product(2, "アロマディフューザー（ウッド）", 4200, "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。", 15, "/images/aroma-diffuser.png", true),
Product(3, "コットンブランケット", 5800, "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。", 10, "/images/cotton-blanket.png", false),
Product(4, "ステンレスタンブラー", 2800, "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。", 30, "/images/tumbler.png", false),
Product(5, "ミニマルウォールクロック", 3200, "余計な装飾のないシンプルな壁掛け時計。静音設計。", 25, "/images/wall-clock.png", false),
Product(6, "リネンクッションカバー", 2500, "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。", 40, "/images/cushion-cover.png", true),
Product(7, "陶器フラワーベース", 4000, "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。", 15, "/images/flower-vase.png", false),
Product(8, "木製コースター（4枚セット）", 1800, "天然木を使用したシンプルなデザインのコースター。4枚セット。", 50, "/images/wooden-coaster.png", false),
Product(9, "キャンバストートバッグ", 3600, "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。", 35, "/images/tote-bag.png", true),`  が存在する。
  - セッションに以下のカート情報が存在する:
    - `Cart(items={"1": CartItem(..., quantity=1, subtotal=3500)}, totalQuantity=1, totalPrice=3500)`
- 手順:
  1. HTTP POSTリクエストを `/api/cart` エンドポイントに送信する。リクエストボディに別の商品IDと数量を含める。
- 入力データ:
  - リクエストボディ(JSON): `{"productId": 2, "quantity": 1}`
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であり、以下のカート情報を含むこと:
     - {"items": {
    "1": {
      "id": "1",
      "productId": 1,
      "name": "シンプルデスクオーガナイザー",
      "price": 3500,
      "imageUrl": "/images/desk-organizer.png",
      "quantity": 1,
      "subtotal": 3500
    },
    "2": {
      "id": "2",
      "productId": 2,
      "name": "アロマディフューザー（ウッド）",
      "price": 4200,
      "imageUrl": "/images/aroma-diffuser.png",
      "quantity": 1,
      "subtotal": 4200
    }
  },
  "totalQuantity": 2,
  "totalPrice": 7700(3500 + 4200)}
      
  1. セッションの `cart` 属性の内容がレスポンスボディと同じカート情報に更新されていること。
  2. `CartService.addItemToCart` が引数 `productId=2`, `quantity=1` で1回呼び出されること。

### No. 2-4

- テストケース名: カート数量変更（正常系）
- 前提条件:
  - DBに商品 `Product(1, "シンプルデスクオーガナイザー", 3500, "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。", 20, "/images/desk-organizer.png", true),
Product(2, "アロマディフューザー（ウッド）", 4200, "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。", 15, "/images/aroma-diffuser.png", true),
Product(3, "コットンブランケット", 5800, "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。", 10, "/images/cotton-blanket.png", false),
Product(4, "ステンレスタンブラー", 2800, "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。", 30, "/images/tumbler.png", false),
Product(5, "ミニマルウォールクロック", 3200, "余計な装飾のないシンプルな壁掛け時計。静音設計。", 25, "/images/wall-clock.png", false),
Product(6, "リネンクッションカバー", 2500, "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。", 40, "/images/cushion-cover.png", true),
Product(7, "陶器フラワーベース", 4000, "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。", 15, "/images/flower-vase.png", false),
Product(8, "木製コースター（4枚セット）", 1800, "天然木を使用したシンプルなデザインのコースター。4枚セット。", 50, "/images/wooden-coaster.png", false),
Product(9, "キャンバストートバッグ", 3600, "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。", 35, "/images/tote-bag.png", true),`   が存在する。
  - セッションに以下のカート情報が存在する:
    - `Cart(items={"1": CartItem(..., quantity=2, subtotal=7000)}, totalQuantity=2, totalPrice=7000)`
- 手順:
  1. HTTP PUTリクエストを `/api/cart/items/1` エンドポイントに送信する。リクエストボディに変更後の数量を含める。
- 入力データ:
  - パスパラメータ: `itemId = 1`
  - リクエストボディ(JSON): `{"quantity": 5}`
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であり、以下のカート情報を含むこと:
     - `items`: `{"1": {"id": "1", ..., "quantity": 5, "subtotal": 17500}}`
     - `totalQuantity`: 5
     - `totalPrice`: 17500
  3. セッションの `cart` 属性の内容がレスポンスボディと同じカート情報に更新されていること。
  4. `CartService.updateItemQuantity` が引数 `itemId="1"`, `quantity=5` で1回呼び出されること。

### No. 2-5

- テストケース名: カート商品削除（正常系）
- 前提条件:
  - DBに商品 `Product(1, "シンプルデスクオーガナイザー", 3500, "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。", 20, "/images/desk-organizer.png", true),
Product(2, "アロマディフューザー（ウッド）", 4200, "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。", 15, "/images/aroma-diffuser.png", true),
Product(3, "コットンブランケット", 5800, "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。", 10, "/images/cotton-blanket.png", false),
Product(4, "ステンレスタンブラー", 2800, "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。", 30, "/images/tumbler.png", false),
Product(5, "ミニマルウォールクロック", 3200, "余計な装飾のないシンプルな壁掛け時計。静音設計。", 25, "/images/wall-clock.png", false),
Product(6, "リネンクッションカバー", 2500, "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。", 40, "/images/cushion-cover.png", true),
Product(7, "陶器フラワーベース", 4000, "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。", 15, "/images/flower-vase.png", false),
Product(8, "木製コースター（4枚セット）", 1800, "天然木を使用したシンプルなデザインのコースター。4枚セット。", 50, "/images/wooden-coaster.png", false),
Product(9, "キャンバストートバッグ", 3600, "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。", 35, "/images/tote-bag.png", true),`   が存在する。
  - セッションに以下のカート情報が存在する:
    - `Cart(items={"1": CartItem(..., quantity=1, subtotal=3500), "2": CartItem(..., quantity=1, subtotal=4200)}, totalQuantity=2, totalPrice=7700)`
- 手順:
  1. HTTP DELETEリクエストを `/api/cart/items/1` エンドポイントに送信する。
- 入力データ:
  - パスパラメータ: `itemId = 1`
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であり、以下のカート情報を含むこと:
     - `items`: `{"2": {"id": "2", ..., "quantity": 1, "subtotal": 1000}}` (商品1が削除されている)
     - `totalQuantity`: 1
     - `totalPrice`: 1000
  3. セッションの `cart` 属性の内容がレスポンスボディと同じカート情報に更新されていること。
  4. `CartService.removeItemFromCart` が引数 `itemId="1"` で1回呼び出されること。

### No. 2-6

- テストケース名: カート取得（正常系 - 空の状態で取得）
- 前提条件:
  - セッションにカート情報が存在しない（または空）。
- 手順:
  1. HTTP GETリクエストを `/api/cart` エンドポイントに送信する。
- 入力データ:
  - セッション: 空
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であり、以下のカート情報を含むこと:
     - `items`: `{}` (空のオブジェクト)
     - `totalQuantity`: 0
     - `totalPrice`: 0
  3. `CartService.getCartFromSession` が1回呼び出されること。

### No. 2-7

- テストケース名: カート取得（正常系 - 商品が複数入った状態で取得）
- 前提条件:
  - DBに商品 `Product(1, "シンプルデスクオーガナイザー", 3500, "机の上をすっきり整理できる木製オーガナイザー。ペン、メモ、スマートフォンなどを収納できます。", 20, "/images/desk-organizer.png", true),
Product(2, "アロマディフューザー（ウッド）", 4200, "天然木を使用したシンプルなデザインのアロマディフューザー。LEDライト付き。", 15, "/images/aroma-diffuser.png", true),
Product(3, "コットンブランケット", 5800, "オーガニックコットン100%のやわらかブランケット。シンプルなデザインで様々なインテリアに合います。", 10, "/images/cotton-blanket.png", false),
Product(4, "ステンレスタンブラー", 2800, "保温・保冷機能に優れたシンプルなデザインのステンレスタンブラー。容量350ml。", 30, "/images/tumbler.png", false),
Product(5, "ミニマルウォールクロック", 3200, "余計な装飾のないシンプルな壁掛け時計。静音設計。", 25, "/images/wall-clock.png", false),
Product(6, "リネンクッションカバー", 2500, "天然リネン100%のクッションカバー。取り外して洗濯可能。45×45cm対応。", 40, "/images/cushion-cover.png", true),
Product(7, "陶器フラワーベース", 4000, "手作りの風合いが魅力の陶器製フラワーベース。シンプルな形状で花を引き立てます。", 15, "/images/flower-vase.png", false),
Product(8, "木製コースター（4枚セット）", 1800, "天然木を使用したシンプルなデザインのコースター。4枚セット。", 50, "/images/wooden-coaster.png", false),
Product(9, "キャンバストートバッグ", 3600, "丈夫なキャンバス地で作られたシンプルなトートバッグ。内ポケット付き。", 35, "/images/tote-bag.png", true),`  が存在する。
  - セッションに以下のカート情報が存在する:
    - `Cart(items={"1": CartItem(..., quantity=2, subtotal=7000), "2": CartItem(..., quantity=1, subtotal=4200)}, totalQuantity=3, totalPrice=11200)`
- 手順:
  1. HTTP GETリクエストを `/api/cart` エンドポイントに送信する。
- 入力データ:
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であり、前提条件と同じカート情報を含むこと。
  3. `CartService.getCartFromSession` が1回呼び出されること。

### No. 2-8

- テストケース名: カート追加（異常系 - 存在しない商品を追加）
- 前提条件:
  - DBに `productId = 999` の商品が存在しない。
  - セッションは空。
- 手順:
  1. HTTP POSTリクエストを `/api/cart` エンドポイントに送信する。
- 入力データ:
  - リクエストボディ(JSON): `{"productId": 999, "quantity": 1}`
  - セッション: 空
- 期待結果:
  1. HTTPステータスコードが `404 Not Found` であること。
  2. レスポンスボディは空、またはエラー情報を含むこと。
  3. セッションにカート情報は作成されないこと。
  4. `CartService.addItemToCart` が引数 `productId=999`, `quantity=1` で呼び出され、`null` を返すこと。
  5. `ProductRepository.findById` が引数 `999` で呼び出され、`Optional.empty()` を返すこと。

### No. 2-9

- テストケース名: カート追加（異常系 - 数量0を指定）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションは空。
- 手順:
  1. HTTP POSTリクエストを `/api/cart` エンドポイントに送信する。
- 入力データ:
  - リクエストボディ(JSON): `{"productId": 1, "quantity": 0}`
  - セッション: 空
- 期待結果:
  1. HTTPステータスコードが `400 Bad Request` であること。
  2. レスポンスボディに `quantity` フィールドに関するバリデーションエラーメッセージ（例: "数量は1以上である必要があります"）を含むJSONが返却されること。
  3. `CartController` の `addItem` メソッド内でバリデーションエラーが発生し、`CartService` は呼び出されないこと。

### No. 2-10

- テストケース名: カート追加（異常系 - 数量に負数を指定）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションは空。
- 手順:
  1. HTTP POSTリクエストを `/api/cart` エンドポイントに送信する。
- 入力データ:
  - リクエストボディ(JSON): `{"productId": 1, "quantity": -1}`
  - セッション: 空
- 期待結果:
  1. HTTPステータスコードが `400 Bad Request` であること。
  2. レスポンスボディに `quantity` フィールドに関するバリデーションエラーメッセージを含むJSONが返却されること。
  3. `CartService` は呼び出されないこと。

### No.2-11
- テストケース名: カート追加（異常系 - 数量をnullで指定）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションは空。
- 手順:
  1. HTTP POSTリクエストを `/api/cart` エンドポイントに送信する。
- 入力データ:
  - リクエストボディ(JSON): `{"productId": 1, "quantity": null}`
  - セッション: 空
- 期待結果:
  1. HTTPステータスコードが `400 Bad Request` であること。
  2. レスポンスボディに `quantity` フィールドに関するバリデーションエラーメッセージ（例: "数量は必須です"）を含むJSONが返却されること。
  3. `CartService` は呼び出されないこと。

### No.2-12
- テストケース名: カート追加（異常系 - 商品IDをnullで指定）
- 前提条件:
  - セッションは空。
- 手順:
  1. HTTP POSTリクエストを `/api/cart` エンドポイントに送信する。
- 入力データ:
  - リクエストボディ(JSON): `{"productId": null, "quantity": 1}`
  - セッション: 空
- 期待結果:
  1. HTTPステータスコードが `400 Bad Request` であること。
  2. レスポンスボディに `productId` フィールドに関するバリデーションエラーメッセージ（例: "商品IDは必須です"）を含むJSONが返却されること。
  3. `CartService` は呼び出されないこと。

### No.2-13
- テストケース名: カート数量変更（異常系 - 数量0を指定）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションにカート情報 `Cart(items={"1": CartItem(...)})` が存在する。
- 手順:
  1. HTTP PUTリクエストを `/api/cart/items/1` エンドポイントに送信する。
- 入力データ:
  - パスパラメータ: `itemId = 1`
  - リクエストボディ(JSON): `{"quantity": 0}`
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `400 Bad Request` であること。
  2. レスポンスボディに `quantity` フィールドに関するバリデーションエラーメッセージを含むJSONが返却されること。
  3. `CartService` は呼び出されないこと。

### No.2-14
- テストケース名: カート数量変更（異常系 - 数量に負数を指定）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションにカート情報 `Cart(items={"1": CartItem(...)})` が存在する。
- 手順:
  1. HTTP PUTリクエストを `/api/cart/items/1` エンドポイントに送信する。
- 入力データ:
  - パスパラメータ: `itemId = 1`
  - リクエストボディ(JSON): `{"quantity": -1}`
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `400 Bad Request` であること。
  2. レスポンスボディに `quantity` フィールドに関するバリデーションエラーメッセージを含むJSONが返却されること。
  3. `CartService` は呼び出されないこと。

### No.2-15
- テストケース名: カート数量変更（異常系 - 数量をnullで指定）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションにカート情報 `Cart(items={"1": CartItem(...)})` が存在する。
- 手順:
  1. HTTP PUTリクエストを `/api/cart/items/1` エンドポイントに送信する。
- 入力データ:
  - パスパラメータ: `itemId = 1`
  - リクエストボディ(JSON): `{"quantity": null}`
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `400 Bad Request` であること。
  2. レスポンスボディに `quantity` フィールドに関するバリデーションエラーメッセージ（例: "数量は必須です"）を含むJSONが返却されること。
  3. `CartService` は呼び出されないこと。

### No.2-16
- テストケース名: カート数量変更（異常系 - 存在しない商品IDで更新）
- 前提条件:
  - セッションにカート情報 `Cart(items={"1": CartItem(...)})` が存在する。
  - カート内に `itemId = 999` の商品は存在しない。
- 手順:
  1. HTTP PUTリクエストを `/api/cart/items/999` エンドポイントに送信する。
- 入力データ:
  - パスパラメータ: `itemId = 999`
  - リクエストボディ(JSON): `{"quantity": 5}`
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であり、変更前と同じカート情報を含むこと。
  3. セッションのカート情報が変更されていないこと。
  4. `CartService.updateItemQuantity` が引数 `itemId="999"`, `quantity=5` で呼び出されること。

### No.2-17
- テストケース名: カート商品削除（異常系 - 存在しない商品IDで削除）
- 前提条件:
  - セッションにカート情報 `Cart(items={"1": CartItem(...)})` が存在する。
  - カート内に `itemId = 999` の商品は存在しない。
- 手順:
  1. HTTP DELETEリクエストを `/api/cart/items/999` エンドポイントに送信する。
- 入力データ:
  - パスパラメータ: `itemId = 999`
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であり、変更前と同じカート情報を含むこと。
  3. セッションのカート情報が変更されていないこと。
  4. `CartService.removeItemFromCart` が引数 `itemId="999"` で呼び出されること。
