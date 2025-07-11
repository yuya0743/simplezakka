const API_BASE = '/api';

 document.getElementById('login-btn').addEventListener('click', function() 
    {
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;
        if (email && password) {
            const responce=fetch(`${API_BASE}/user/email`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email: email, password: password })
                
            })
            const user = response.json();
            if(user.password === password)
        
            alert("ログイン成功");
        // ログイン成功後の処理をここに追加
        
            window.location.href = '/'; // ホームページにリダイレクト
            } else {
             alert("メールアドレスまたはパスワードが間違っています");
            }
    
})
