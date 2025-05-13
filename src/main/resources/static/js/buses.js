const hamburger = document.querySelector('.hamburger');
const navLinks = document.querySelector('.nav-links');
const scrollToTop = document.querySelector('.scroll-to-top');

if (hamburger && navLinks) {
    hamburger.addEventListener('click', () => {
        hamburger.classList.toggle('active');
        navLinks.classList.toggle('active');
    });
}
if (scrollToTop) {
    window.addEventListener('scroll', () => {
        if (window.pageYOffset > 300) {
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
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.booking-form form').forEach(form => {
        form.addEventListener('submit', function(e) {
            const seats = form.querySelector('[name="seats"]').value;
            const totalAmount = form.querySelector('[name="totalAmount"]').value;

            if (!seats || seats === "") {
                e.preventDefault();
                alert("Please select at least one seat");
                return false;
            }

            if (!totalAmount || parseFloat(totalAmount) <= 0) {
                e.preventDefault();
                alert("Invalid total amount. Please select seats again.");
                return false;
            }

            return true;
        });
    });
});
document.querySelectorAll('.booking-form form').forEach(form => {
    form.addEventListener('submit', function(e) {
        const button = this.querySelector('button[type="submit"]');
        button.disabled = true;
        button.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...';
    });
});

// // Bus card toggle functionality
// function toggleBusDetails(header) {
//     const busCard = header.closest('.bus-card');
//     const details = busCard.querySelector('.bus-details');

//     if (busCard.classList.contains('expanded')) {
//         // Collapsing the card - RESET ALL SELECTIONS
//         busCard.classList.remove('expanded');
//         details.style.display = 'none';

//         // Clear all selections
//         const seatNumbers = busCard.querySelector('#seat-numbers');
//         const totalAmount = busCard.querySelector('#total-amount');
//         const selectedSeats = busCard.querySelectorAll('.seat.selected');
//         if (seatNumbers) seatNumbers.value = '';
//         if (totalAmount) totalAmount.value = 'Rs. 0';
//         selectedSeats.forEach(seat => seat.classList.remove('selected'));


//         // If you want to also clear boarding point selection:
//         const boardingSelect = busCard.querySelector('.booking-form select');
//         if (boardingSelect) boardingSelect.selectedIndex = 0;
//     } else {
//         // Expanding the card (original behavior)
//         document.querySelectorAll('.bus-card.expanded').forEach(card => {
//             card.classList.remove('expanded');
//             const cardDetails = card.querySelector('.bus-details');
//             if (cardDetails) cardDetails.style.display = 'none';
//         });

//         busCard.classList.add('expanded');
//         details.style.display = 'block';

//         // Generate fresh seat layout
//         const bookedSeatsStr = busCard.dataset.bookedSeats || '';
//         const bookedSeats = bookedSeatsStr ? bookedSeatsStr.split(',') : [];
//         console.log('Booked seats:', bookedSeats);
//         console.log('Booked seats:', bookedSeats)
//         const seatLayout = details.querySelector('.seat-layout');
//         if (seatLayout) generateSeatLayout(seatLayout, bookedSeats);
//     }
// }
function toggleBusDetails(header) {
    const busCard = header.closest('.bus-card');
    const details = busCard.querySelector('.bus-details');

    if (busCard.classList.contains('expanded')) {
        busCard.classList.remove('expanded');
        details.style.display = 'none';
        
        // Clear selections
        const seatNumbers = busCard.querySelector('#seat-numbers');
        const totalAmountDisplay = busCard.querySelector('#total-amount-display');
        const selectedSeats = busCard.querySelectorAll('.seat.selected');
        if (seatNumbers) seatNumbers.value = '';
        if (totalAmountDisplay) totalAmountDisplay.value = 'Rs. 0';
        selectedSeats.forEach(seat => seat.classList.remove('selected'));
        
        const boardingSelect = busCard.querySelector('.booking-form select');
        if (boardingSelect) boardingSelect.selectedIndex = 0;
    } else {
        // Close any other expanded cards first
        document.querySelectorAll('.bus-card.expanded').forEach(card => {
            card.classList.remove('expanded');
            const cardDetails = card.querySelector('.bus-details');
            if (cardDetails) cardDetails.style.display = 'none';
        });

        busCard.classList.add('expanded');
        details.style.display = 'block';

        const bookedSeatsStr = busCard.dataset.bookedSeats || '';
        const bookedSeats = bookedSeatsStr ? bookedSeatsStr.split(',') : [];
        const seatLayout = details.querySelector('.seat-layout');
        if (seatLayout) generateSeatLayout(seatLayout, bookedSeats);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    // Handle bus card expansion
    document.querySelectorAll('.bus-header').forEach(header => {
        header.addEventListener('click', function() {
            toggleBusDetails(this);
        });
    });
});





// Generate seat layout
function generateSeatLayout(container,bookedSeats=[]) {
    // Define all seats in order
    const rows = [
        // First row (driver + front seats)
        {
            groupA: ['A1'],
            groupB: ['B1', 'B2']
        },
        // Regular rows
        {
            groupA: ['A2', 'A3'],
            groupB: ['B3', 'B4']
        },
        {
            groupA: ['A4', 'A5'],
            groupB: ['B5', 'B6']
        },
        {
            groupA: ['A6', 'A7'],
            groupB: ['B7', 'B8']
        },
        {
            groupA: ['A8', 'A9'],
            groupB: ['B9', 'B10']
        },
        {
            groupA: ['A10', 'A11'],
            groupB: ['B11', 'B12']
        },
        {
            groupA: ['A12', 'A13'],
            groupB: ['B13', 'B14']
        },
        {
            groupA: ['A14', 'A15'],
            groupB: ['B15', 'B16']
        },
        // Last row (treated like middle but with extra seat in gap)
        {
            groupA: ['A16', 'A17'],
            groupB: ['B17', 'B18'],
            extraSeat: 'A18'  // Extra seat in the gap
        }
    ];

    let html = '<div class="seat-rows-container">';

    rows.forEach((row, index) => {
        const isFirstRow = index === 0;
        const isLastRow = index === rows.length - 1;

        html += `
        <div class="seat-row ${isFirstRow ? 'first-row' : ''} ${isLastRow ? 'last-row' : ''}">
            <div class="seat-group group-a">
                ${row.groupA.map(seat => 
                    `<div class="seat ${bookedSeats.includes(seat) ? 'booked' : 'available'}" 
                          data-seat="${seat}">${seat}</div>`
                ).join('')}
            </div>
            
            ${isLastRow ? 
                `<div class="seat-spacer">
                    <div class="seat ${bookedSeats.includes(row.extraSeat) ? 'booked' : 'available'} extra-seat" 
                         data-seat="${row.extraSeat}">${row.extraSeat}</div>
                </div>`
                : '<div class="seat-spacer"></div>'
            }
            
            <div class="seat-group group-b">
                ${row.groupB.map(seat => 
                    `<div class="seat ${bookedSeats.includes(seat) ? 'booked' : 'available'}" 
                          data-seat="${seat}">${seat}</div>`
                ).join('')}
            </div>
        </div>
    `;
    });

    html += '</div>';
    container.innerHTML = html;

    // Add click handlers to all seats
    // container.querySelectorAll('.seat').forEach(seat => {
    //     seat.addEventListener('click', function() {
    //         selectSeat(this);
    //     });
    // });

    container.querySelectorAll('.seat.available').forEach(seat => {
        seat.addEventListener('click', function() {
            this.classList.toggle('selected');
            updateSelectedSeats();
        });
    });
}

// Select seat functionality
function selectSeat(seatElement) {
    if (seatElement.classList.contains('booked')) {
        return; // Can't select booked seats
    }

    // Toggle selected state with animation
    seatElement.classList.toggle('selected');

    // Force reflow to ensure animation restarts
    void seatElement.offsetWidth;

    // Update the selected seats display
    updateSelectedSeats();

    seatElement.classList.add('seat-feedback');
    setTimeout(() => {
        seatElement.classList.remove('seat-feedback');
    }, 300);
}

// Update selected seats input field
function updateSelectedSeats() {
    const busCard = document.querySelector('.bus-card.expanded');
    if (!busCard) return;

    const selectedSeats = Array.from(busCard.querySelectorAll('.seat.selected'))
    .map(seat => seat.dataset.seat); 

    const seatInput = busCard.querySelector('#seat-numbers');
    const totalAmountDisplay = busCard.querySelector('#total-amount-display');
    const totalAmountHidden = busCard.querySelector('#total-amount-hidden');
    const seatsHiddenInput = busCard.querySelector('input[name="seats"]');

    seatInput.value = selectedSeats.join(', ');
    seatsHiddenInput.value = selectedSeats.join(',');

    // Get the correct fare (accounting for discounts)
    let fareElement = busCard.querySelector('.discounted-fare span');
    if (!fareElement) {
        fareElement = busCard.querySelector('.original-fare');
    }

    const fareText = fareElement.textContent;
    const fare = parseFloat(fareText.replace('Rs. ', '').trim());
    const totalAmount = selectedSeats.length * fare;

    totalAmountDisplay.value = 'Rs. ' + totalAmount.toFixed(2);
    totalAmountHidden.value = totalAmount.toFixed(2);
}