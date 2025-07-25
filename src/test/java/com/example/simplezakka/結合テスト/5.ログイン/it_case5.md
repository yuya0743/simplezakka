# 機能: ユーザーログイン・マイページ・ログアウト機能

## テスト対象API

- `POST /api/user/login`（ログイン）
- `GET /api/user/mypage`（マイページ情報取得）
- `POST /api/user/logout`（ログアウト）

## テストデータ準備方針

- テスト実行前に必要なユーザーデータ（メールアドレス、パスワード、その他属性）をDBに投入し、テストごとに状態を初期化する。
- 各ケースでセッション情報のクリアやCookie削除も行う。

## テストシナリオ

---

### No. 5-1 登録済み会員が正しい情報でログインする（正常系）

- **前提条件**ユーザーテーブルに以下のデータが存在する。User(email: `user1@example.com`, password: `testpass`, name: `テストユーザー`)
- **手順**

  1. HTTP POSTリクエストを `/api/user/login` に以下のJSONをボディとして送信
     ```json
     {
       "email": "user1@example.com",
       "password": "testpass"
     }
     ```
- **期待結果**

  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式で、ログインユーザー情報（`email`, `name` など）が返ること。
  3. レスポンスヘッダーにセッション管理用Cookie（JSESSIONIDなど）が付与されること。
  4. クライアント側でセッションが有効になり、以後の認証付きAPIアクセスが可能となること。

---

### No. 5-2 ログイン後にマイページへアクセス（正常系）

- **前提条件**No. 5-1と同じくユーザーが登録済みセッションが有効な状態（前のログインAPI呼び出し後のセッションまたはCookieを利用）
- **手順**

  1. HTTP GETリクエストを `/api/user/mypage` にセッション情報付きで送信
- **期待結果**

  1. HTTPステータスコードが `200 OK` であること。
  2. レスポンスボディがJSON形式で、ユーザーのマイページ情報（`email`, `name` など）が返ること。

---

### No. 5-3 ログアウト処理（正常系）

- **前提条件**セッションが有効な状態（ログイン済み）
- **手順**

  1. HTTP POSTリクエストを `/api/user/logout` にセッション情報付きで送信
- **期待結果**

  1. HTTPステータスコードが `200 OK` であること。
  2. サーバー側でセッション情報が初期化されること。
  3. レスポンスヘッダーでセッションCookieが無効化されること（例：`Set-Cookie: JSESSIONID=; Max-Age=0` など）。

---

### No. 5-4 存在しないメールアドレスでログイン（異常系）

- **前提条件**ユーザーテーブルに `unregistered@example.com` のユーザーが存在しない
- **手順**

  1. HTTP POSTリクエストを `/api/user/login` に以下のJSONを送信
     ```json
     {
       "email": "unregistered@example.com",
       "password": "somepass"
     }
     ```
- **期待結果**

  1. HTTPステータスコードが `401 Unauthorized` であること。

### No. 5-5 パスワードが間違っている場合（異常系）

- **前提条件**ユーザーテーブルに `user1@example.com` のユーザーが登録済み、パスワードは `testpass`
- **手順**

  1. HTTP POSTリクエストを `/api/user/login` に以下のJSONを送信
     ```json
     {
       "email": "user1@example.com",
       "password": "wrongpass"
     }
     ```
- **期待結果**

  1. HTTPステータスコードが `401 Unauthorized` であること。

---

### No. 5-6 ログインしていない状態でマイページにアクセス（異常系）

- **前提条件**セッションや認証情報が存在しない
- **手順**

  1. HTTP GETリクエストを `/api/user/mypage` にセッション情報なしで送信
- **期待結果**

  1. HTTPステータスコードが `401 Unauthorized` であること。

---
