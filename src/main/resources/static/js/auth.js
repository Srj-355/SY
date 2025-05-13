document.addEventListener('DOMContentLoaded', function() {
    // Mobile menu toggle
    const hamburger = document.querySelector('.hamburger');
    const navLinks = document.querySelector('.nav-links');

    hamburger.addEventListener('click', function() {
        hamburger.classList.toggle('active');
        navLinks.classList.toggle('active');
    });

    // Form switching with improved animation handling
    function showSignup() {
        const loginForm = document.getElementById('loginForm');
        const signupForm = document.getElementById('signupForm');
        window.history.pushState({}, '', '/auth?form=signup');
        loginForm.style.animation = 'slideOut 0.4s forwards';
        setTimeout(() => {
            loginForm.style.display = 'none';
            signupForm.style.display = 'block';
            signupForm.style.animation = 'slideIn 0.4s forwards';
        }, 400);
    }

    function showLogin() {
        const loginForm = document.getElementById('loginForm');
        const signupForm = document.getElementById('signupForm');
        window.history.pushState({}, '', '/auth?form=login');
        signupForm.style.animation = 'slideOut 0.4s forwards';
        setTimeout(() => {
            signupForm.style.display = 'none';
            loginForm.style.display = 'block';
            loginForm.style.animation = 'slideIn 0.4s forwards';
        }, 400);
    }


// Password strength indicator
    document.getElementById('signupPassword')?.addEventListener('input', function(e) {
        const password = e.target.value;
        const strengthIndicator = document.getElementById('passwordStrength') ||
            (function() {
                const div = document.createElement('div');
                div.id = 'passwordStrength';
                e.target.parentNode.appendChild(div);
                return div;
            })();

        let strength = 0;
        if (password.length >= 8) strength++;
        if (password.match(/[a-z]/)) strength++;
        if (password.match(/[A-Z]/)) strength++;
        if (password.match(/[0-9]/)) strength++;
        if (password.match(/[^a-zA-Z0-9]/)) strength++;

        let strengthText = '';
        let strengthClass = '';
        switch(strength) {
            case 0:
            case 1:
                strengthText = 'Very Weak';
                strengthClass = 'very-weak';
                break;
            case 2:
                strengthText = 'Weak';
                strengthClass = 'weak';
                break;
            case 3:
                strengthText = 'Medium';
                strengthClass = 'medium';
                break;
            case 4:
                strengthText = 'Strong';
                strengthClass = 'strong';
                break;
            case 5:
                strengthText = 'Very Strong';
                strengthClass = 'very-strong';
                break;
        }

        strengthIndicator.textContent = `Strength: ${strengthText}`;
        strengthIndicator.className = strengthClass;
    });

    // Expose functions to global scope for HTML onclick
    window.showSignup = showSignup;
    window.showLogin = showLogin;

    // Close mobile menu when clicking a link
    document.querySelectorAll('.nav-links a').forEach(link => {
        link.addEventListener('click', () => {
            if (navLinks.classList.contains('active')) {
                hamburger.classList.remove('active');
                navLinks.classList.remove('active');
            }
        });
    });
});