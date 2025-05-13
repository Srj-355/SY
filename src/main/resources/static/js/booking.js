const hamburger = document.querySelector('.hamburger');
const navLinks = document.querySelector('.nav-links');

hamburger.addEventListener('click', () => {
    hamburger.classList.toggle('active');
    navLinks.classList.toggle('active');
});

    document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');

    form.addEventListener('submit', function(e) {
    // Clear previous errors
    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    document.querySelectorAll('.invalid-feedback').forEach(el => el.remove());

    let isValid = true;

    // Validate first name
    const firstName = document.getElementById('firstName');
    if (!firstName.value.trim()) {
    showError(firstName, 'First name is required');
    isValid = false;
}

    // Validate last name
    const lastName = document.getElementById('lastName');
    if (!lastName.value.trim()) {
    showError(lastName, 'Last name is required');
    isValid = false;
}

    // Validate phone number
    const phoneNumber = document.getElementById('phoneNumber');
    if (!phoneNumber.value.trim()) {
    showError(phoneNumber, 'Phone number is required');
    isValid = false;
} else if (!/^[0-9]{10}$/.test(phoneNumber.value)) {
    showError(phoneNumber, 'Phone number must be 10 digits');
    isValid = false;
}

    // Validate payment method
    const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked');
    if (!paymentMethod) {
    const paymentOptions = document.querySelector('.payment-options');
    const errorDiv = document.createElement('div');
    errorDiv.className = 'invalid-feedback d-block';
    errorDiv.textContent = 'Please select a payment method';
    paymentOptions.appendChild(errorDiv);
    isValid = false;
}

    if (!isValid) {
    e.preventDefault();
}
});

    function showError(input, message) {
    input.classList.add('is-invalid');
    const errorDiv = document.createElement('div');
    errorDiv.className = 'invalid-feedback';
    errorDiv.textContent = message;
    input.parentNode.appendChild(errorDiv);
}
});
