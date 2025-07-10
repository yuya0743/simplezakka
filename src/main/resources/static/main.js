document.addEventListener('DOMContentLoaded', function() {
    // モーダル要素の取得
    const productModal = new bootstrap.Modal(document.getElementById('productModal'));
    const cartModal = new bootstrap.Modal(document.getElementById('cartModal'));
    const checkoutModal = new bootstrap.Modal(document.getElementById('checkoutModal'));
    const orderCompleteModal = new bootstrap.Modal(document.getElementById('orderCompleteModal'));
    
    // APIのベースURL
    const API_BASE = '/api';

    // 検索関連要素の取得
    const searchInput = document.getElementById('search-input');
    const searchButton = document.getElementById('search-button');

    // 全商品を保持する配列
    let allProducts = [];
    
    // 商品一覧の取得と表示
    fetchProducts();
    
    // カート情報の取得と表示
    updateCartDisplay();
    
    // カートボタンクリックイベント
    document.getElementById('cart-btn').addEventListener('click', function() {
        updateCartModalContent();
        cartModal.show();
    });
    
    // 注文手続きボタンクリックイベント
    document.getElementById('checkout-btn').addEventListener('click', function() {
        cartModal.hide();
        checkoutModal.show();
    });
    
    // 注文確定ボタンクリックイベント
    document.getElementById('confirm-order-btn').addEventListener('click', function() {
        submitOrder();
    });

    // 検索入力フィールドのイベントリスナー（リアルタイム検索）
    searchInput.addEventListener('input', function() {
        filterProducts(searchInput.value);
    });

    // 検索ボタンのイベントリスナー（ボタンクリックで検索）
    searchButton.addEventListener('click', function() {
        filterProducts(searchInput.value);
    });
    
    // 商品一覧を取得して表示する関数
    async function fetchProducts() {
        try {
            const response = await fetch(`${API_BASE}/products`);
            if (!response.ok) {
                throw new Error('商品の取得に失敗しました');
            }
            const products = await response.json();
            console.log("取得した商品データ:", products); // ★この行を追加
            allProducts = products;
            displayProducts(allProducts); // 最初は全商品を画面に表示
        } catch (error) {
            console.error('Error:', error);
            alert('商品の読み込みに失敗しました');
        }
    }

    // 検索クエリに基づいて商品をフィルタリングする関数
    function filterProducts(query) {
        const lowerCaseQuery = query.toLowerCase();
        const filtered = allProducts.filter(product => 
            product.name.toLowerCase().includes(lowerCaseQuery)
        );
        displayProducts(filtered); // フィルタリングされた商品を表示
    }
    
    // 商品一覧を表示する関数 (変更なし、引数にproductsを受け取る)
    function displayProducts(products) {
        const container = document.getElementById('products-container');
        container.innerHTML = '';
        
        if (products.length === 0) {
            container.innerHTML = '<p class="text-center w-100">該当する商品が見つかりません。</p>';
            return;
        }

        products.forEach(product => {
            const card = document.createElement('div');
            card.className = 'col';
            card.innerHTML = `
                <div class="card product-card">
                    <img src="${product.imageUrl || 'https://via.placeholder.com/300x200'}" class="card-img-top" alt="${product.name}">
                    <div class="card-body">
                        <h5 class="card-title">${product.name}</h5>
                        <p class="card-text">¥${product.price.toLocaleString()}</p>
                        <button class="btn btn-outline-primary view-product" data-id="${product.productId}">詳細を見る</button>
                    </div>
                </div>
            `;
            container.appendChild(card);
            
            // 詳細ボタンのイベント設定
            card.querySelector('.view-product').addEventListener('click', function() {
                fetchProductDetail(product.productId);
            });
        });
    }
    
    // 商品詳細を取得する関数 (変更なし)
    async function fetchProductDetail(productId) {
        try {
            const response = await fetch(`${API_BASE}/products/${productId}`);
            if (!response.ok) {
                throw new Error('商品詳細の取得に失敗しました');
            }
            const product = await response.json();
            displayProductDetail(product);
        } catch (error) {
            console.error('Error:', error);
            alert('商品詳細の読み込みに失敗しました');
        }
    }
    
    // 商品詳細を表示する関数 (変更なし)
    function displayProductDetail(product) {
        document.getElementById('productModalTitle').textContent = product.name;
        
        const modalBody = document.getElementById('productModalBody');
        modalBody.innerHTML = `
            <div class="row">
                <div class="col-md-6">
                    <img src="${product.imageUrl || 'https://via.placeholder.com/400x300'}" class="img-fluid" alt="${product.name}">
                </div>
                <div class="col-md-6">
                    <p class="fs-4">¥${product.price.toLocaleString()}</p>
                    <p>${product.description}</p>
                    <p>在庫: ${product.stock} 個</p>
                    <div class="d-flex align-items-center mb-3">
                        <label for="quantity" class="me-2">数量:</label>
                        <input type="number" id="quantity" class="form-control w-25" value="1" min="1" max="${product.stock}">
                    </div>
                    <button class="btn btn-primary add-to-cart" data-id="${product.productId}">カートに入れる</button>
                </div>
            </div>
        `;
        
        // カートに追加ボタンのイベント設定
        modalBody.querySelector('.add-to-cart').addEventListener('click', function() {
            const quantity = parseInt(document.getElementById('quantity').value);
            addToCart(product.productId, quantity);
        });
        
        productModal.show();
    }
    
    // カートに商品を追加する関数 (変更なし)
    async function addToCart(productId, quantity) {
        try {
            const response = await fetch(`${API_BASE}/cart`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    productId: productId,
                    quantity: quantity
                })
            });
            
            if (!response.ok) {
                throw new Error('カートへの追加に失敗しました');
            }
            
            const cart = await response.json();
            updateCartBadge(cart.totalQuantity);
            
            productModal.hide();
            alert('商品をカートに追加しました');
        } catch (error) {
            console.error('Error:', error);
            alert('カートへの追加に失敗しました');
        }
    }
    
    // カート情報を取得する関数 (変更なし)
    async function updateCartDisplay() {
        try {
            const response = await fetch(`${API_BASE}/cart`);
            if (!response.ok) {
                throw new Error('カート情報の取得に失敗しました');
            }
            const cart = await response.json();
            updateCartBadge(cart.totalQuantity);
        } catch (error) {
            console.error('Error:', error);
        }
    }
    
    // カートバッジを更新する関数 (変更なし)
    function updateCartBadge(count) {
        document.getElementById('cart-count').textContent = count;
    }
    
    // カートモーダルの内容を更新する関数 (変更なし)
    async function updateCartModalContent() {
        try {
            const response = await fetch(`${API_BASE}/cart`);
            if (!response.ok) {
                throw new Error('カート情報の取得に失敗しました');
            }
            const cart = await response.json();
            displayCart(cart);
        } catch (error) {
            console.error('Error:', error);
            alert('カート情報の読み込みに失敗しました');
        }
    }
    
    // カート内容を表示する関数 (変更なし)
    function displayCart(cart) {
        const modalBody = document.getElementById('cartModalBody');
        
        if (cart.items && Object.keys(cart.items).length > 0) {
            let html = `
                <table class="table">
                    <thead>
                        <tr>
                            <th>商品</th>
                            <th>単価</th>
                            <th>数量</th>
                            <th>小計</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
            `;
            
            Object.values(cart.items).forEach(item => {
                html += `
                    <tr>
                        <td>${item.name}</td>
                        <td>¥${item.price.toLocaleString()}</td>
                        <td>
                            <input type="number" class="form-control form-control-sm update-quantity" 
                                   data-id="${item.id}" value="${item.quantity}" min="1" style="width: 70px">
                        </td>
                        <td>¥${item.subtotal.toLocaleString()}</td>
                        <td>
                            <button class="btn btn-sm btn-danger remove-item" data-id="${item.id}">削除</button>
                        </td>
                    </tr>
                `;
            });
            
            html += `
                    </tbody>
                    <tfoot>
                        <tr>
                            <th colspan="3" class="text-end">合計:</th>
                            <th>¥${cart.totalPrice.toLocaleString()}</th>
                            <th></th>
                        </tr>
                    </tfoot>
                </table>
            `;
            
            modalBody.innerHTML = html;
            
            // 数量更新イベントの設定
            document.querySelectorAll('.update-quantity').forEach(input => {
                input.addEventListener('change', function() {
                    updateItemQuantity(this.dataset.id, this.value);
                });
            });
            
            // 削除ボタンイベントの設定
            document.querySelectorAll('.remove-item').forEach(button => {
                button.addEventListener('click', function() {
                    removeItem(this.dataset.id);
                });
            });
            
            // 注文ボタンの有効化
            document.getElementById('checkout-btn').disabled = false;
        } else {
            modalBody.innerHTML = '<p class="text-center">カートは空です</p>';
            document.getElementById('checkout-btn').disabled = true;
        }
    }
    
    // カート内の商品数量を更新する関数 (変更なし)
    async function updateItemQuantity(itemId, quantity) {
        try {
            const response = await fetch(`${API_BASE}/cart/items/${itemId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    quantity: parseInt(quantity)
                })
            });
            
            if (!response.ok) {
                throw new Error('数量の更新に失敗しました');
            }
            
            const cart = await response.json();
            displayCart(cart);
            updateCartBadge(cart.totalQuantity);
        } catch (error) {
            console.error('Error:', error);
            alert('数量の更新に失敗しました');
            updateCartModalContent(); // 失敗時は元の状態に戻す
        }
    }
    
    // カート内の商品を削除する関数 (変更なし)
    async function removeItem(itemId) {
        try {
            const response = await fetch(`${API_BASE}/cart/items/${itemId}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                throw new Error('商品の削除に失敗しました');
            }
            
            const cart = await response.json();
            displayCart(cart);
            updateCartBadge(cart.totalQuantity);
        } catch (error) {
            console.error('Error:', error);
            alert('商品の削除に失敗しました');
        }
    }
    
    // 注文を確定する関数 (変更なし)
    async function submitOrder() {
        const form = document.getElementById('order-form');
        
        // フォームバリデーション
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }
        
        const orderData = {
            customerInfo: {
                name: document.getElementById('name').value,
                email: document.getElementById('email').value,
                address: document.getElementById('address').value,
                phoneNumber: document.getElementById('phone').value
            }
        };
        
        try {
            const response = await fetch(`${API_BASE}/orders`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(orderData)
            });
            
            if (!response.ok) {
                throw new Error('注文の確定に失敗しました');
            }
            
            const order = await response.json();
            displayOrderComplete(order);
            
            checkoutModal.hide();
            orderCompleteModal.show();
            
            // カート表示をリセット
            updateCartBadge(0);
            
            // フォームリセット
            form.reset();
            form.classList.remove('was-validated');
        } catch (error) {
            console.error('Error:', error);
            alert('注文の確定に失敗しました');
        }
    }
    
    // 注文完了画面を表示する関数 (変更なし)
    function displayOrderComplete(order) {
        document.getElementById('orderCompleteBody').innerHTML = `
            <p>ご注文ありがとうございます。注文番号は <strong>${order.orderId}</strong> です。</p>
            <p>ご注文日時: ${new Date(order.orderDate).toLocaleString()}</p>
            <p>お客様のメールアドレスに注文確認メールをお送りしました。</p>
        `;
    }
});
// 商品データ例（カテゴリ情報付き）
const products = [
    { name: "シンプルデスクオーガナイザー", category: "デスク周り" },
    { name: "アロマディフューザー（ウッド）", category: "インテリア・雑貨" },
    { name: "ミニマルウォールクロック", category: "インテリア・雑貨" },
    { name: "陶器フラワーベース", category: "インテリア・雑貨" },
    { name: "木製コースター（四枚セット）", category: "インテリア・雑貨" },
    { name: "コットンブランケット", category: "家具・寝具" },
    { name: "リネンクッションカバー", category: "家具・寝具" },
    { name: "ガラス保存容器セット", category: "キッチン用品" },
    { name: "ステンレスタンブラー", category: "キッチン用品" },
    { name: "キャンバストートバッグ", category: "バッグ・トラベル" }
];

// 検索・カテゴリでフィルター
function renderProducts() {
    const keyword = document.getElementById("search-input").value.trim();
    const selectedCategory = document.getElementById("category-select").value;
    const container = document.getElementById("products-container");

    // 一旦クリア
    container.innerHTML = "";

    const filtered = products.filter(p => {
        const matchKeyword = keyword === "" || p.name.includes(keyword);
        const matchCategory = selectedCategory === "" || p.category === selectedCategory;
        return matchKeyword && matchCategory;
    });

for (const p of filtered) {
  const div = document.createElement("div");
  div.className = "col";

  div.innerHTML = `
    <div class="card p-3">
      <img src="${p.image}" alt="${p.name}" class="card-img-top" style="max-height: 200px; object-fit: cover;">
      <div class="card-body">
        <h5 class="card-title">${p.name}</h5>
        <p class="card-text">${p.category}</p>
        <a href="${p.detailsUrl}" class="btn btn-primary">商品詳細</a>
      </div>
    </div>
  `;

  container.appendChild(div);
}
}

// 検索ボタン、カテゴリ変更時に実行
document.getElementById("search-button").addEventListener("click", renderProducts);
document.getElementById("category-select").addEventListener("change", renderProducts);

// 初期表示
renderProducts();
