
# 機能: 管理者機能

## テスト対象API:

- `GET /api/products`
- `POST /api/products`
- `PUT /api/products`
- `DELETE /api/products`
- ログイン処理 (a-script.java)

## テストシナリオ

### No. 6-1

- テストケース名: 正しいユーザー名とパスワードを入力する
- 前提条件: 有効な管理者ユーザー情報が存在する。
- 手順:
  1. ログイン画面で正しいユーザー名とパスワードを入力。
- 入力データ: `username=admin`, `password=pass123`
- 期待結果:
  1. 認証に成功し、セッションが開始される。
  2. 管理者ページへ遷移する。

### No. 6-2

- テストケース名: 商品一覧表示（正常系）
- 前提条件:
  - DBに2件の商品が存在する。
- 手順:
  1. `/api/products` にGETリクエストを送信。
- 入力データ: なし
- 期待結果:
  1. HTTPステータス `200 OK`
  2. JSON配列に2商品分の `ProductListItem` が含まれる。

### No. 6-3

- テストケース名: 商品の登録から新たに商品を登録する
- 前提条件: ログイン済みである。
- 手順:
  1. `/api/products` に新商品の情報でPOSTリクエストを送信。
- 入力データ例:
  ```json
  {
    "name": "新商品",
    "price": 1500,
    "description": "新しい説明",
    "stock": 20,
    "imageUrl": "/imgX.png"
  }
  ```
- 期待結果:
  1. HTTPステータス `201 Created`
  2. リクエストと同じ内容のJSONが返る。
  3. DBに商品が保存され、セッションに `userinfo` が設定される。

### No. 6-4

- テストケース名: 商品編集から商品情報の編集をおこなう
- 前提条件: `productId=1` の商品が存在する。
- 手順:
  1. `/api/products` にPUTリクエストを送信（内容変更）。
- 入力データ例:
  ```json
  {
    "productId": 1,
    "name": "編集後の商品",
    "price": 1800,
    "description": "編集済み説明",
    "stock": 30,
    "imageUrl": "/edit.png"
  }
  ```
- 期待結果:
  1. HTTPステータス `200 OK`
  2. 編集後の商品情報が `ProductListItem` 形式で返却される。
  3. DBに変更が反映される。

### No. 6-5

- テストケース名: 削除ボタンを押すと商品が一覧から削除される
- 前提条件: `productId=1` の商品が存在する。
- 手順:
  1. `/api/products/1` にDELETEリクエストを送信。
- 入力データ: パスパラメータ: `productId = 1`
- 期待結果:
  1. HTTPステータス `200 OK`
  2. 商品一覧から該当商品が消える。

### No. 6-6

- テストケース名: ユーザー名、パスワードの一方、または両方が間違っている
- 前提条件: 認証処理が有効である。
- 手順:
  1. ログイン画面で誤ったユーザー名またはパスワードを入力。
- 入力データ例: `username=wrong`, `password=pass123`
- 期待結果:
  1. HTTPステータス `401 Unauthorized`
  2. エラーメッセージ表示、ページ遷移しない。

### No. 6-7

- テストケース名: 新規商品登録で商品名をnullで指定する
- 手順:
  1. `/api/products` にPUTリクエストを送信（name=null）。
- 入力データ例:
  ```json
  {
    "productId": 2,
    "name": null,
    "price": 1000,
    "description": "説明",
    "stock": 5,
    "imageUrl": "/img.png"
  }
  ```
- 期待結果:
  1. HTTPステータス `400 Bad Request`
  2. バリデーションエラーメッセージが返却される。

### No. 6-8

- テストケース名: 新規商品登録で価格を0以下で指定する
- 手順:
  1. `/api/products` にPUTリクエストを送信（price <= 0）。
- 入力データ例:
  ```json
  {
    "productId": 3,
    "name": "商品X",
    "price": 0,
    "description": "説明",
    "stock": 10,
    "imageUrl": "/img.png"
  }
  ```
- 期待結果:
  1. HTTPステータス `400 Bad Request`
  2. バリデーションエラーメッセージが返却される。

### No. 6-9

- テストケース名: 新規商品登録で在庫を負数で指定する
- 手順:
  1. `/api/products` にPUTリクエストを送信（stock < 0）。
- 入力データ例:
  ```json
  {
    "productId": 4,
    "name": "商品Y",
    "price": 1200,
    "description": "説明",
    "stock": -5,
    "imageUrl": "/img.png"
  }
  ```
- 期待結果:
  1. HTTPステータス `400 Bad Request`
  2. バリデーションエラーメッセージが返却される。
