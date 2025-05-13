document.addEventListener('DOMContentLoaded', function() {
    // Sidebar Toggle
    $('#sidebarCollapse').on('click', function() {
        $('#sidebar').toggleClass('active');
    });

    // Dropdown Menu
    $('.dropdown-toggle').dropdown();

    // Initialize DataTable for bus listing

    $('#dataTable').DataTable({
        responsive: true,
        language: {
            search: "_INPUT_",
            searchPlaceholder: "Search buses...",
        },
        dom: '<"top"f>rt<"bottom"lip><"clear">',
        pageLength: 10
    });
});

// Time input formatting
document.addEventListener('DOMContentLoaded', function() {
    const timeInputs = document.querySelectorAll('input[type="time"]');

    timeInputs.forEach(input => {
        input.addEventListener('change', function() {
            if (this.value) {
                const [hours, minutes] = this.value.split(':');
                const formattedTime = `${hours.padStart(2, '0')}:${minutes.padStart(2, '0')}`;
                this.value = formattedTime;
            }
        });
    });
});

// Form validation
document.addEventListener('DOMContentLoaded', function() {
    const forms = document.querySelectorAll('form.needs-validation');

    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }

            form.classList.add('was-validated');
        }, false);
    });
});

// Delete confirmation
document.querySelectorAll('.delete-btn').forEach(button => {
    button.addEventListener('click', function(e) {
        if (!confirm('Are you sure you want to delete this bus?')) {
            e.preventDefault();
        }
    });

    // Notification Alert
    $('.nav-link').on('click', function(e) {
        if ($(this).find('.fa-bell').length > 0) {
            e.preventDefault();
            alert('You have 3 new notifications!');
        }
    });

    // Logout Confirmation
    $('.dropdown-item').on('click', function() {
        if ($(this).text() === 'Logout') {
            if (!confirm('Are you sure you want to logout?')) {
                return false;
            }
        }
    });

    // Tooltip Initialization
    $('[data-bs-toggle="tooltip"]').tooltip();


});