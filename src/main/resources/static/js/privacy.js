const hamburger = document.querySelector('.hamburger');
const navLinks = document.querySelector('.nav-links');
const scrollToTop = document.querySelector('.scroll-to-top');

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