document.addEventListener('DOMContentLoaded', function () {
    async function submitUSERS() {
        const form = document.getElementById('USERS-form');

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
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(USERSData)
            });

            if (!response.ok) {
                throw new Error('新規登録に失敗しました');
            }

            const USERS = await response.json();
            displayUSERSComplete(USERS);
            USERSCompleteModal.show();

        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    }

    const submitButton = document.getElementById('submit-button');
    if (submitButton) {
        submitButton.addEventListener('click', submitUSERS);
    }
});
