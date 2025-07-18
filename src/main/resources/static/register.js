        document.getElementById('users-form').addEventListener('submit', function (e) {
            e.preventDefault();

        const name = form.querySelector('[name="name"]').value;
        const email = form.querySelector('[name="email"]').value;
        const address = form.querySelector('[name="address"]').value;
        const password = form.querySelector('[name="password"]').value;


            fetch('/api/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ name, email, address, password }),
            })
                .then((response) => {
                    if (!response.ok) {
                        return response.text().then(text => {
                throw new Error(text || '登録に失敗しました。');
            });
        }
        return response.json(); 
    })
                .then((data) => {
                    alert('登録が成功しました。');
                    window.location.href = 'login.html';                 })
                .catch((error) => {
                    alert(error.message);
                });
        });