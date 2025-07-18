document.addEventListener('DOMContentLoaded', function() {
    // APIのベースURL
    const API_BASE = '/api';

    // 全商品を保持する配列
    let allProducts = [];

    // 商品一覧の取得と表示
    fetchProducts();

    // 商品一覧を取得して表示する関数
    async function fetchProducts() {
        try {
            const response = await fetch(`${API_BASE}/products`);
            if (!response.ok) {
                throw new Error('商品の取得に失敗しました');
            }
            const products = await response.json();
            allProducts = products;
            displayProducts(allProducts); // 最初は全商品を画面に表示
        } catch (error) {
            alert('商品の読み込みに失敗しました');
        }
    }

    // 商品一覧を表示する関数
    function displayProducts(products) {
        const container = document.getElementById('products-container');
        container.innerHTML = '';

        if (products.length === 0) {
            container.innerHTML = '<p class="text-center w-100">該当する商品が見つかりません。</p>';
            return;
        }

        let tableHtml = `
            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>商品名</th>
                        <th>説明</th>
                        <th>価格</th>
                        <th>在庫</th>
                        <th>画像URL</th>
                        <th>おすすめ</th>
                        <th>カテゴリ</th>
                        <th>素材</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
        `;

        products.forEach(product => {
            tableHtml += `
                <tr>
                    <td>${product.productId || ''}</td>
                    <td>${product.name || ''}</td>
                    <td>${product.description || ''}</td>
                    <td>¥${product.price ? product.price.toLocaleString() : ''}</td>
                    <td>${product.stock || 0}</td>
                    <td><a href="${product.imageUrl || '#'}" target="_blank">${product.imageUrl ? '画像リンク' : 'なし'}</a></td>
                    <td>${product.isRecommended ? 'する' : 'しない'}</td>
                    <td>${product.category || ''}</td>
                    <td>${product.material || ''}</td>
                    <td>
                        <a href="a-update.html?id=${product.productId}" class="btn btn-primary btn-sm">編集</a>
                        <button class="btn btn-danger btn-sm" onclick="deleteProduct(${product.productId})">削除</button>
                    </td>
                </tr>
            `;
        });

        tableHtml += `
                </tbody>
            </table>
        `;

        container.innerHTML = tableHtml;
    }

    // 削除機能（グローバルでOK）
    window.deleteProduct = function(id) {
        if (!confirm("本当に削除しますか？")) return;
        fetch(`/api/products/${id}`, { method: "DELETE" })
            .then(res => {
                if (!res.ok) {
                    return res.text().then(txt => {
                        throw new Error(txt || "削除に失敗しました");
                    });
                } else {
                    alert("削除しました");
                    window.location.href = "a-top.html";
                    fetchProducts();
                }
            })
            .catch(e => {
                alert(e.message || "削除に失敗しました");
            });
    };
});
 