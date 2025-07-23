        document.getElementById('users-form').addEventListener('submit', function (e) {
            e.preventDefault();

         const userdata = {
                name: document.getElementById('name').value,
                email: document.getElementById('email').value,
               password: Number(document.getElementById('password').value),
                address: Number(document.getElementById('address').value),
                
            };


            fetch('/api/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ userdata }),
            })
                .then((response) => {
                    if (!response.ok) {
                        return response.text().then(text => {
                throw new Error(text || '登録に失敗しました。');
            });
        }
        return response.json(); 
    })
                .then((userdata) => {
                    alert('登録が成功しました。');
                    window.location.href = 'login.html';                 })
                .catch((error) => {
                    alert(error.message);
                });
        });