# 機能: ログイン

## テスト対象API:

- `POST /api/users/login` (ログイン)
- `POST /api/users/mypage` (マイページ接続)
- `POST /api/users/logout` (ログアウト)

## テストデータ準備方針:

- User情報は `Users(Name, Email, Address, Password)` の形式で記述します。

## テストシナリオ

### No. 4-1
- テストケース名: 新規会員登録（正常系）
- 前提条件: 入力フォーム制約通りに入力されたデータ
- 手順: 
1. HTTP POSTリクエストを `/api/users` エンドポイントに送信する。リクエストボディに入力データを含める。
   1. 入力データ(JSON): `{"name":test , "Email": test,"Address": 東京, "Password":0000}`

- 期待結果:
1. HTTPステータスコードが `201 OK` であること。
2. レスポンスボディがJSON形式であり、以下のカート情報を含むこと:
     - `Users`: `{"name": "test", "Email": "test@mail", "Address": "東京", "Password": 0000}`

### No. 4-2
- テストケース名: 新規会員登録（異常系：DB接続エラー）

- 前提条件: DBが停止中、何らかの故障

- 手順: 
1. HTTP POSTリクエストを `/api/users` エンドポイントに送信する。リクエストボディに入力データを含める。
- 入力データ(JSON): `{"name":test , "Email": test,"Address": 東京, "Password":0000}`

- 期待結果:
1. HTTPステータスコードが `500` であること。
2. DBに登録されない。
3. ログにエラーが出力される。
