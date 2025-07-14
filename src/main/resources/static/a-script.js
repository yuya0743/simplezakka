function showOrHide() {
    let showpass = document.getElementById("pass");
    let check = document.getElementById("showpassword");
    if (check.checked) {
        showpass.type = "text";
    } else {
        showpass.type = "password";
    }
}
 
let userdata = [
    { name: "administrator", age: 20, email: "Administrator@example.com", password: "Administrator" },
    { name: "user1", age: 21, email: "user1@example.com", password: "user-1" },
    { name: "user2", age: 22, email: "user2@example.com", password: "user-2" },
    { name: "user3", age: 23, email: "user3@example.com", password: "user-3" }
];
 
// ページロード完了後にイベントを登録する
window.addEventListener('DOMContentLoaded', function() {
    document.getElementById('a-login-btn').addEventListener('click', function () {
        let username = document.getElementById("username").value;
        let password = document.getElementById("pass").value;
 
        let found = false;
 
        for (let i = 0; i < userdata.length; i++) {
            if (userdata[i].name === username && userdata[i].password === password) {
                found = true;
                break;
            }
        }
 
        if (found) {
            window.location.href = 'a-top.html';
        } else {
            alert("ユーザー名またはパスワードが間違っています。");
        }
    });
});
 
 