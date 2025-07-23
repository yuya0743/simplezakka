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
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションにカート情報が存在しない（または空）。
- 手順:
  1. HTTP POSTリクエストを `/api/cart` エンドポイントに送信する。リクエストボディに指定の商品IDと数量を含める。
- 入力データ:
  - リクエストボディ(JSON): `{"productId": 1, "quantity": 2}`
  - セッション: 空
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であり、以下のカート情報を含むこと:
     - `items`: `{"1": {"id": "1", "productId": 1, "name": "商品A", "price": 500, "imageUrl": "(商品Aの画像URL)", "quantity": 2, "subtotal": 1000}}`
     - `totalQuantity`: 2
     - `totalPrice`: 1000
  3. セッションに `cart` 属性が保存され、その内容がレスポンスボディと同じカート情報であること。
  4. `CartService.addItemToCart` が引数 `productId=1`, `quantity=2` で1回呼び出されること。
  5. `ProductRepository.findById` が引数 `1` で1回呼び出されること。

### No. 2-2

- テストケース名: カート追加（正常系 - すでにカートにある商品と同じ商品をさらに追加）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションに以下のカート情報が存在する:
    - `Cart(items={"1": CartItem(..., quantity=1, subtotal=500)}, totalQuantity=1, totalPrice=500)`
- 手順:
  1. HTTP POSTリクエストを `/api/cart` エンドポイントに送信する。リクエストボディに同じ商品IDと追加する数量を含める。
- 入力データ:
  - リクエストボディ(JSON): `{"productId": 1, "quantity": 3}`
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であり、以下のカート情報を含むこと:
     - `items`: `{"1": {"id": "1", ..., "quantity": 4, "subtotal": 2000}}` (数量が 1 + 3 = 4 になる)
     - `totalQuantity`: 4
     - `totalPrice`: 2000
  3. セッションの `cart` 属性の内容がレスポンスボディと同じカート情報に更新されていること。
  4. `CartService.addItemToCart` が引数 `productId=1`, `quantity=3` で1回呼び出されること。

### No. 2-3

- テストケース名: カート追加（正常系 - カートに存在する商品と別の商品を追加）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)`, `Product(2, "商品B", 1000, 5)` が存在する。
  - セッションに以下のカート情報が存在する:
    - `Cart(items={"1": CartItem(..., quantity=1, subtotal=500)}, totalQuantity=1, totalPrice=500)`
- 手順:
  1. HTTP POSTリクエストを `/api/cart` エンドポイントに送信する。リクエストボディに別の商品IDと数量を含める。
- 入力データ:
  - リクエストボディ(JSON): `{"productId": 2, "quantity": 1}`
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であり、以下のカート情報を含むこと:
     - `items`: `{"1": {"id": "1", ..., "quantity": 1, "subtotal": 500}, "2": {"id": "2", "productId": 2, "name": "商品B", "price": 1000, "imageUrl": "(商品Bの画像URL)", "quantity": 1, "subtotal": 1000}}`
     - `totalQuantity`: 2 (1 + 1)
     - `totalPrice`: 1500 (500 + 1000)
  3. セッションの `cart` 属性の内容がレスポンスボディと同じカート情報に更新されていること。
  4. `CartService.addItemToCart` が引数 `productId=2`, `quantity=1` で1回呼び出されること。

### No. 2-4

- テストケース名: カート数量変更（正常系）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションに以下のカート情報が存在する:
    - `Cart(items={"1": CartItem(..., quantity=2, subtotal=1000)}, totalQuantity=2, totalPrice=1000)`
- 手順:
  1. HTTP PUTリクエストを `/api/cart/items/1` エンドポイントに送信する。リクエストボディに変更後の数量を含める。
- 入力データ:
  - パスパラメータ: `itemId = 1`
  - リクエストボディ(JSON): `{"quantity": 5}`
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式であり、以下のカート情報を含むこと:
     - `items`: `{"1": {"id": "1", ..., "quantity": 5, "subtotal": 2500}}`
     - `totalQuantity`: 5
     - `totalPrice`: 2500
  3. セッションの `cart` 属性の内容がレスポンスボディと同じカート情報に更新されていること。
  4. `CartService.updateItemQuantity` が引数 `itemId="1"`, `quantity=5` で1回呼び出されること。

### No. 2-5

- テストケース名: カート商品削除（正常系）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)`, `Product(2, "商品B", 1000, 5)` が存在する。
  - セッションに以下のカート情報が存在する:
    - `Cart(items={"1": CartItem(..., quantity=1, subtotal=500), "2": CartItem(..., quantity=1, subtotal=1000)}, totalQuantity=2, totalPrice=1500)`
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
  - DBに商品 `Product(1, "商品A", 500, 10)`, `Product(2, "商品B", 1000, 5)` が存在する。
  - セッションに以下のカート情報が存在する:
    - `Cart(items={"1": CartItem(..., quantity=2, subtotal=1000), "2": CartItem(..., quantity=1, subtotal=1000)}, totalQuantity=3, totalPrice=2000)`
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
