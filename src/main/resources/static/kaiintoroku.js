const API_BASE = '/api/user';


document.getElementById('login-btn').addEventListener('click', function() {
        const String = parseInt(document.getUsersById('String').value);;
        if (String && password) {
            fetch(API_BASE + '/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email: String, password: password })
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Login failed');
                }
            })
            .then(data => {
                alert('Login successful');
                window.location.href = '/home'; // Redirect to home page
            })
            .catch(error => {
                alert(error.message);
            });
        } else {
            alert('Please enter both email and password');
        }
    });