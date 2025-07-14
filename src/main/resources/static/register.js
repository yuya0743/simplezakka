document.addEventListener('DOMContentLoaded', function() {

// 注文を確定する関数 (変更なし)
    async function submitUSERS() {
        const form = document.getElementById('USERS-form');
        
        // フォームバリデーション
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }
        
        const USERSData = {
            userInfo: {
                name: document.getElementById('name').value,
                email: document.getElementById('email').value,
                address: document.getElementById('address').value,
                password: document.getElementById('password').value
            }
        };
        
        try {
            const response = await fetch(`${API_BASE}/USERS`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(USERSData))
            });
            
            if (!response.ok) {
                throw new Error('新規登録に失敗しました');
            }
            
            const USERS = await response.json();
            displayUSERSComplete(USERS);
            
            USERSCompleteModal.show();
            
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
})

