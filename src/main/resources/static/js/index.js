const hamburger = document.querySelector('.hamburger');
const navLinks = document.querySelector('.nav-links');

hamburger.addEventListener('click', () => {
    hamburger.classList.toggle('active');
    navLinks.classList.toggle('active');
});

let slideIndex = 0;
showSlides();

function showSlides() {
    let i;
    let slides = document.getElementsByClassName("slide");
    for (i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";
    }
    slideIndex++;
    if (slideIndex > slides.length) {slideIndex = 1}
    slides[slideIndex-1].style.display = "block";
    setTimeout(showSlides, 3000); // Change image every 3 seconds
}

// In your index.js
document.querySelector('.search-button').addEventListener('click', function(e) {
    e.preventDefault();

    const departure = document.getElementById('departure').value;
    const destination = document.getElementById('destination').value;
    const date = document.getElementById('date').value;

    if (!departure || !destination || !date) {
        alert('Please fill all fields');
        return;
    }

    if (departure === destination) {
        alert('Departure and destination cannot be the same');
        return;
    }

    // Submit form programmatically
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/search';

    const input1 = document.createElement('input');
    input1.type = 'hidden';
    input1.name = 'departure';
    input1.value = departure;

    const input2 = document.createElement('input');
    input2.type = 'hidden';
    input2.name = 'destination';
    input2.value = destination;

    const input3 = document.createElement('input');
    input3.type = 'hidden';
    input3.name = 'date';
    input3.value = date;

    form.appendChild(input1);
    form.appendChild(input2);
    form.appendChild(input3);

    document.body.appendChild(form);
    form.submit();
});