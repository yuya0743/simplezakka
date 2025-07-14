const API_BASE = '/api';

document.getElementById('login-btn').addEventListener('click', async function () {
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    if (email && password) {
        try {
            const response = await fetch(`${API_BASE}/user/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });

            if (response.ok) {
                alert("ログイン成功");
                window.location.href = '/'; // ホームページにリダイレクト
            } else {
                alert("メールアドレスまたはパスワードが間違っています");
            }
        } catch (error) {
            console.error("通信エラー:", error);
            alert("通信エラーが発生しました");
        }
    }
});
