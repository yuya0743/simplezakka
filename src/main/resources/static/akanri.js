document.addEventListener('DOMContentLoaded', function() {
    // モーダル要素の取得
    const productModal = new bootstrap.Modal(document.getElementById('productModal'));
    const cartModal = new bootstrap.Modal(document.getElementById('cartModal'));
    const checkoutModal = new bootstrap.Modal(document.getElementById('checkoutModal'));
    const orderCompleteModal = new bootstrap.Modal(document.getElementById('orderCompleteModal'));
    
    // APIのベースURL
    const API_BASE = '/api';

    
    // 全商品を保持する配列
    let allProducts = [];
    
    // 商品一覧の取得と表示
    fetchProducts();
    
  
    
 
    
    
   
   

    
    
    // 商品一覧を取得して表示する関数
    async function fetchProducts() {
        try {
            const response = await fetch(`${API_BASE}/productsdetail`);
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

    // カテゴリプルダウンの変更イベントでリアルタイム更新
    document.getElementById('category-select').addEventListener('change', filterProducts);

    // 検索クエリに基づいて商品をフィルタリングする関数
    function filterProducts() {
    const keyword = searchInput.value.trim().toLowerCase();
    const selectedCategory = document.getElementById("category-select").value;

    const filtered = allProducts.filter(product => {
        const matchName = product.name.toLowerCase().includes(keyword);
        const matchCategory = selectedCategory === "" || product.category === selectedCategory;
        return matchName && matchCategory;
    });

    displayProducts(filtered);
}

    
    // 商品一覧を表示する関数 (変更なし、引数にproductsを受け取る)
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
                       
                        <th>価格</th>
                        <th>在庫</th>
                        <th>画像URL</th>
                        <th>おすすめ</th>
                        <th>カテゴリ</th>
                        <th>素材</th>
                        </tr>
                </thead>
                <tbody>
        `;

        products.forEach(product => {
            tableHtml += `
                <tr>
                    <td>${product.productId || ''}</td>
                    <td>${product.name || ''}</td>
                    
                    <td>¥${product.price ? product.price.toLocaleString() : ''}</td>
                    <td>${product.stock || 0}</td>
                    <td><a href="${product.imageUrl || '#'}" target="_blank">${product.imageUrl ? '画像リンク' : 'なし'}</a></td>
                    <td>${product.isRecommended ? 'する' : 'しない'}</td>
                    <td>${product.category || ''}</td>
                    <td>${product.material || ''}</td>
                    
                </tr>
            `;
        });

        tableHtml += `
                </tbody>
            </table>
        `;

        container.innerHTML = tableHtml;

        
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



// 初期表示
filterProducts();

