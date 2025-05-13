const hamburger = document.querySelector('.hamburger');
const navLinks = document.querySelector('.nav-links');
const scrollToTop = document.querySelector('.scroll-to-top');
const acc = document.querySelectorAll('.accordion-header');

hamburger.addEventListener('click', () => {
    hamburger.classList.toggle('active');
    navLinks.classList.toggle('active');
});

window.addEventListener('scroll', () => {
    if (window.pageYOffset > 250) {
        scrollToTop.classList.add('show');
    } else {
        scrollToTop.classList.remove('show');
    }
});

scrollToTop.addEventListener('click', () => {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
});

acc.forEach(item => {
    item.addEventListener('click', function() {
        this.classList.toggle('active');
        const panel = this.nextElementSibling;
        if (panel.style.maxHeight) {
            panel.style.maxHeight = null;
        } else {
            panel.style.maxHeight = panel.scrollHeight + "px";
        }
    });
});