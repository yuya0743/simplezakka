# 機能: 注文機能

## テスト対象API:

- `POST /api/orders` (注文実行)

## テストデータ準備方針:

- 各テストケース実行前に、セッションをクリア（または新しいセッションを使用）し、データベースをクリーンな状態にします。
- 必要な商品データをDBに準備し、カートに必要な商品をセッションに追加します。
- DBの状態変化を確認するため、テスト前後の `products` テーブルの在庫数、`orders` テーブル、`order_details` テーブルのレコード数や内容を確認します。

## テストシナリオ

### No. 3-1

- テストケース名: 注文（正常系 - カートに1種類の商品を入れて注文）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションにカート `Cart(items={"1": CartItem(..., quantity=3, subtotal=1500)}, totalQuantity=3, totalPrice=1500)` が存在する。
- 手順:
  1. HTTP POSTリクエストを `/api/orders` エンドポイントに送信する。リクエストボディに顧客情報を含める。
- 入力データ:
  - リクエストボディ(JSON):
    ```json
    {
      "customerInfo": {
        "name": "顧客 一郎",
        "email": "ichiro@test.com",
        "address": "東京都千代田区テスト1-1",
        "phoneNumber": "03-1234-5678"
      }
    }
    ```
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `201 Created` であること。
  2. レスポンスボディがJSON形式であり、`OrderResponse` の形式（`orderId` と `orderDate` を含む）であること。`orderId` はnullでない整数値、`orderDate` は有効な日時文字列であること。
  3. DB状態変化:
     - `orders` テーブルに1件レコードが追加され、顧客情報、合計金額(1500)、注文日時、ステータス("PENDING")などが正しく登録されていること。
     - `order_details` テーブルに1件レコードが追加され、上記の `orders` レコードに関連付き、商品情報(商品A, 500)、数量(3)が正しく登録されていること。
     - `products` テーブルの商品ID=1の在庫数が `7` (10 - 3) に更新されていること。
  4. セッション状態変化: セッションから `cart` 属性が削除されている（カートがクリアされている）こと。
  5. `OrderService.placeOrder` が1回呼び出されること。
  6. `ProductRepository.findById(1)` が呼び出されること（在庫確認または詳細取得のため）。
  7. `OrderRepository.save` が1回呼び出されること。
  8. `ProductRepository.decreaseStock(1, 3)` が1回呼び出され、`1` を返すこと。
  9. `CartService.clearCart` が1回呼び出されること。

### No. 3-2
- テストケース名: 注文（正常系 - カートに複数種類の商品を入れて注文）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)`, `Product(2, "商品B", 1000, 5)` が存在する。
  - セッションにカート `Cart(items={"1": CartItem(..., quantity=2, subtotal=1000), "2": CartItem(..., quantity=1, subtotal=1000)}, totalQuantity=3, totalPrice=2000)` が存在する。
- 手順:
  1. HTTP POSTリクエストを `/api/orders` エンドポイントに送信する。リクエストボディに顧客情報を含める。
- 入力データ:
  - リクエストボディ(JSON): (No. 3-1 と同様の顧客情報)
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `201 Created` であること。
  2. レスポンスボディが `OrderResponse` 形式であること。
  3. DB状態変化:
     - `orders` テーブルに1件レコードが追加され、合計金額(2000)などが正しく登録されていること。
     - `order_details` テーブルに2件レコードが追加され、それぞれ商品A(数量2)と商品B(数量1)の情報が正しく登録されていること。両方のレコードが同じ `orders` レコードに関連付いていること。
     - `products` テーブルの商品ID=1の在庫数が `8` (10 - 2) に、商品ID=2の在庫数が `4` (5 - 1) に更新されていること。
  4. セッション状態変化: セッションから `cart` 属性が削除されていること。
  5. (各メソッドの呼び出し回数や引数は、カートの内容に合わせて検証する)
     - `ProductRepository.decreaseStock(1, 2)` と `ProductRepository.decreaseStock(2, 1)` がそれぞれ1回ずつ呼び出されること。

### No. 3-3

- テストケース名: 注文（正常系 - 在庫数ぴったりの数量を注文）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 3)` が存在する。（在庫が3）
  - セッションにカート `Cart(items={"1": CartItem(..., quantity=3, subtotal=1500)}, totalQuantity=3, totalPrice=1500)` が存在する。（注文数も3）
- 手順:
  1. HTTP POSTリクエストを `/api/orders` エンドポイントに送信する。
- 入力データ:
  - リクエストボディ(JSON): (No. 3-1 と同様の顧客情報)
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `201 Created` であること。
  2. レスポンスボディが `OrderResponse` 形式であること。
  3. DB状態変化:
     - `orders`, `order_details` テーブルに正しくデータが登録されること。
     - `products` テーブルの商品ID=1の在庫数が `0` (3 - 3) に更新されていること。
  4. セッション状態変化: セッションから `cart` 属性が削除されていること。
  5. `ProductRepository.decreaseStock(1, 3)` が1回呼び出され、`1` を返すこと。

### No. 3-4

- テストケース名: 注文（異常系 - カートが空の状態で注文）
- 前提条件:
  - セッションにカート情報が存在しない（または空）。
- 手順:
  1. HTTP POSTリクエストを `/api/orders` エンドポイントに送信する。
- 入力データ:
  - リクエストボディ(JSON): (No. 3-1 と同様の顧客情報)
  - セッション: 空
- 期待結果:
  1. HTTPステータスコードが `400 Bad Request` であること。
  2. レスポンスボディは空、またはエラーを示す内容であること。
  3. DB状態変化: `orders`, `order_details`, `products` テーブルに変化がないこと。
  4. セッション状態変化: セッションの状態に変化がないこと（空のまま）。
  5. `OrderController` 内で `cartService.getCartFromSession` が呼び出されるが、カートが空のため `OrderService.placeOrder` は呼び出されないこと。

### No. 3-5
- テストケース名: 注文（異常系 - 在庫不足が発生）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 2)` が存在する。（在庫が2）
  - セッションにカート `Cart(items={"1": CartItem(..., quantity=3, subtotal=1500)}, totalQuantity=3, totalPrice=1500)` が存在する。（注文数が3）
- 手順:
  1. HTTP POSTリクエストを `/api/orders` エンドポイントに送信する。
- 入力データ:
  - リクエストボディ(JSON): (No. 3-1 と同様の顧客情報)
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `500 Internal Server Error` であること（`OrderService` 内で `RuntimeException` がスローされ、`GlobalExceptionHandler` で処理されるため）。
  2. レスポンスボディにエラーメッセージ（例: "在庫不足または商品未存在: 商品A" を含むJSON）が返却されること。
  3. DB状態変化: `orders`, `order_details`, `products` テーブルに変化がないこと（トランザクションがロールバックされる）。
  4. セッション状態変化: セッションの `cart` 属性がクリアされずに残っていること。
  5. `OrderService.placeOrder` が呼び出されるが、在庫確認のループ内で例外がスローされること。
  6. `OrderRepository.save` や `ProductRepository.decreaseStock`, `CartService.clearCart` は呼び出されないこと。


### No. 3-6
- テストケース名: 注文（異常系 - カート内の商品がDBに存在しない）
- 前提条件:
  - セッションにカート `Cart(items={"999": CartItem(productId=999, ...)}, ...)` が存在するが、DBに `productId = 999` の商品が存在しない。
- 手順:
  1. HTTP POSTリクエストを `/api/orders` エンドポイントに送信する。
- 入力データ:
  - リクエストボディ(JSON): (No. 3-1 と同様の顧客情報)
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `500 Internal Server Error` であること。
  2. レスポンスボディにエラーメッセージ（例: "在庫不足または商品未存在: (カート内の商品名)" を含むJSON）が返却されること。
  3. DB状態変化: `orders`, `order_details`, `products` テーブルに変化がないこと。
  4. セッション状態変化: セッションの `cart` 属性がクリアされずに残っていること。
  5. `OrderService.placeOrder` が呼び出されるが、在庫確認のループ内で `productRepository.findById(999)` が `Optional.empty()` を返し、例外がスローされること。


### No. 3-7
- テストケース名: 注文（異常系 - 顧客情報不足、例：名前が空）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションにカート `Cart(items={"1": CartItem(...)})` が存在する。
- 手順:
  1. HTTP POSTリクエストを `/api/orders` エンドポイントに送信する。リクエストボディの顧客情報の名前を空にする。
- 入力データ:
  - リクエストボディ(JSON):
    ```json
    {
      "customerInfo": {
        "name": "", // 空文字
        "email": "ichiro@test.com",
        "address": "東京都千代田区テスト1-1",
        "phoneNumber": "03-1234-5678"
      }
    }
    ```
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `400 Bad Request` であること。
  2. レスポンスボディに `customerInfo.name` に関するバリデーションエラーメッセージ（例: "お名前は必須です"）を含むJSONが返却されること。
  3. `OrderController` の `placeOrder` メソッド内でバリデーションエラーが発生し、`OrderService` は呼び出されないこと。
  4. DB状態変化: なし。
  5. セッション状態変化: なし。


### No. 3-8
- テストケース名: 注文（異常系 - 顧客情報の形式が不正、例：メール形式不正）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションにカート `Cart(items={"1": CartItem(...)})` が存在する。
- 手順:
  1. HTTP POSTリクエストを `/api/orders` エンドポイントに送信する。リクエストボディの顧客情報のメールアドレスを不正な形式にする。
- 入力データ:
  - リクエストボディ(JSON):
    ```json
    {
      "customerInfo": {
        "name": "顧客 一郎",
        "email": "invalid-email", // 不正な形式
        "address": "東京都千代田区テスト1-1",
        "phoneNumber": "03-1234-5678"
      }
    }
    ```
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `400 Bad Request` であること。
  2. レスポンスボディに `customerInfo.email` に関するバリデーションエラーメッセージ（例: "有効なメールアドレスを入力してください"）を含むJSONが返却されること。
  3. `OrderService` は呼び出されないこと。
  4. DB状態変化: なし。
  5. セッション状態変化: なし。


### No. 3-9
- テストケース名: 注文（異常系 - リクエストボディが空、または不正なJSON）
- 前提条件:
  - セッションにカート `Cart(items={"1": CartItem(...)})` が存在する。
- 手順:
  1. HTTP POSTリクエストを `/api/orders` エンドポイントに送信する。リクエストボディを空にするか、JSONとして不正な形式にする。
- 入力データ:
  - リクエストボディ: 空または `"{"customerInfo":}` など
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `400 Bad Request` または `500 Internal Server Error` であること (Spring MVCのメッセージコンバーターやバリデーションでのエラー)。
  2. レスポンスボディにエラー情報（例: "Required request body is missing" や JSONパースエラーメッセージ）が含まれること。
  3. `OrderController.placeOrder` が呼び出される前にエラーを発生する可能性が高い。
  4. DB状態変化: なし。
  5. セッション状態変化: なし。


### No. 3-10
- テストケース名: 注文（異常系 - (模擬) 注文DB保存時にエラー発生）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションにカート `Cart(items={"1": CartItem(...)})` が存在する。
  - (テスト実装上の工夫): `OrderRepository.save` メソッドが呼び出された際に、意図的に例外（例: `RuntimeException("DB save error")`）をスローするようにモックまたはテスト用実装で設定する。これは単体テストや特定の統合テストフレームワークで可能です。通常の結合テスト環境で再現するのは難しい場合があります。
- 手順:
  1. HTTP POSTリクエストを `/api/orders` エンドポイントに送信する。
- 入力データ:
  - リクエストボディ(JSON): (No. 3-1 と同様の顧客情報)
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `500 Internal Server Error` であること。
  2. レスポンスボディにエラーメッセージ（例: "DB save error" を含むJSON）が返却されること。
  3. DB状態変化: `orders`, `order_details`, `products` テーブルに変化がないこと（`@Transactional` によりロールバックされる）。
  4. セッション状態変化: セッションの `cart` 属性がクリアされずに残っていること。
  5. `OrderService.placeOrder` 内で `orderRepository.save` が呼び出され、そこで例外が発生すること。
  6. `CartService.clearCart` は呼び出されないこと。


### No. 3-11
- テストケース名: 注文（異常系 - (模擬) 在庫更新時にエラー発生）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションにカート `Cart(items={"1": CartItem(quantity=3,...)})` が存在する。
  - (テスト実装上の工夫): `ProductRepository.decreaseStock(1, 3)` が呼び出された際に、`0` を返すか、または例外をスローするようにモックまたはテスト用実装で設定する。
- 手順:
  1. HTTP POSTリクエストを `/api/orders` エンドポイントに送信する。
- 入力データ:
  - リクエストボディ(JSON): (No. 3-1 と同様の顧客情報)
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `500 Internal Server Error` であること (`OrderService` 内で `IllegalStateException` がスローされるため)。
  2. レスポンスボディにエラーメッセージ（例: "在庫の更新に失敗しました" を含むJSON）が返却されること。
  3. DB状態変化: `orders`, `order_details`, `products` テーブルに変化がないこと（ロールバックされる）。
  4. セッション状態変化: セッションの `cart` 属性がクリアされずに残っていること。
  5. `OrderService.placeOrder` 内で `productRepository.decreaseStock` が呼び出され、そこで例外が発生すること。
  6. `OrderRepository.save` は呼び出されないか、呼び出されてもロールバックされること。
  7. `CartService.clearCart` は呼び出されないこと。


### No. 3-12
- テストケース名: 注文（異常系 - (模擬) カートクリア時にエラー発生）
- 前提条件:
  - DBに商品 `Product(1, "商品A", 500, 10)` が存在する。
  - セッションにカート `Cart(items={"1": CartItem(...)})` が存在する。
  - (テスト実装上の工夫): `CartService.clearCart` が呼び出された際に、意図的に例外（例: `RuntimeException("Session error")`）をスローするようにモックまたはテスト用実装で設定する。
- 手順:
  1. HTTP POSTリクエストを `/api/orders` エンドポイントに送信する。
- 入力データ:
  - リクエストボディ(JSON): (No. 3-1 と同様の顧客情報)
  - セッション: 上記前提条件のカート情報
- 期待結果:
  1. HTTPステータスコードが `500 Internal Server Error` であること。
  2. レスポンスボディにエラーメッセージ（例: "Session error" を含むJSON）が返却されること。
  3. DB状態変化: `OrderService.placeOrder` メソッド全体が `@Transactional` であるため、`orders`, `order_details` テーブルへの登録、`products` テーブルの在庫更新もロールバックされる。結果としてDBに変化はない。
  4. セッション状態変化: カートクリアに失敗するため、セッションの `cart` 属性は残ったままになる。
  5. `OrderService.placeOrder` 内で `orderRepository.save` や `productRepository.decreaseStock` は成功するが、最後の `cartService.clearCart` で例外が発生し、トランザクション全体がロールバックされること。
