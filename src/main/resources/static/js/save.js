document.addEventListener("DOMContentLoaded", function () {
    // Access the elements after the document has fully loaded
    const rememberPasswordBtn = document.getElementById("inputRememberPassword");
    const usernameField = document.getElementById("username");
    const passwordField = document.getElementById("inputPassword");


    const encryptionKey = 'some-very-hard-key-to-break';
    // Listen for changes to the checkbox
    rememberPasswordBtn.addEventListener("change", function () {
        // Only store credentials if the checkbox is checked
        if (rememberPasswordBtn.checked) {
            const username = usernameField.value;
            const password = passwordField.value;

            if (username && password) {
                // Store the username and password (securely in a real-world scenario)
                const encryptedUsername = CryptoJS.AES.encrypt(username, encryptionKey).toString();
                const encryptedPassword = CryptoJS.AES.encrypt(password, encryptionKey).toString();


                localStorage.setItem("rememberUsername", encryptedUsername);
                sessionStorage.setItem("rememberPassword", encryptedPassword);

                alert("Your credentials will be remembered!");
            } else {
                alert("Please enter both username and password.");
            }
        } else {
            // If unchecked, clear the stored credentials
            localStorage.removeItem("rememberUsername");
            sessionStorage.removeItem("rememberPassword");
        }
    });

    // Auto-fill saved credentials on page load
    const savedEncryptedUsername = localStorage.getItem("rememberUsername");
    const savedEncryptedPassword = sessionStorage.getItem("rememberPassword");

    if (savedEncryptedUsername && savedEncryptedPassword) {
        const decryptedUsername = CryptoJS.AES.decrypt(savedEncryptedUsername, encryptionKey).toString(CryptoJS.enc.Utf8);
        const decryptedPassword = CryptoJS.AES.decrypt(savedEncryptedPassword, encryptionKey).toString(CryptoJS.enc.Utf8);

        usernameField.value = decryptedUsername;
        passwordField.value = decryptedPassword;

        rememberPasswordBtn.checked = true;
    }
});