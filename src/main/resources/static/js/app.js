// ============================================
// Second-Hand Car Sales System — JavaScript
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    // Auto-dismiss alerts after 5 seconds
    document.querySelectorAll('.alert-dismissible').forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s, transform 0.5s';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // Star rating interactive input
    document.querySelectorAll('.star-input label').forEach(label => {
        label.addEventListener('click', function() {
            const value = this.dataset.value;
            const container = this.closest('.star-input');
            container.querySelectorAll('label').forEach(l => {
                l.classList.toggle('active', parseInt(l.dataset.value) <= parseInt(value));
                l.innerHTML = parseInt(l.dataset.value) <= parseInt(value) ? '★' : '☆';
            });
            container.querySelector('input[type="hidden"]').value = value;
        });
    });

    // Confirm delete actions
    document.querySelectorAll('[data-confirm]').forEach(el => {
        el.addEventListener('click', function(e) {
            if (!confirm(this.dataset.confirm || 'Are you sure?')) {
                e.preventDefault();
            }
        });
    });

    // Toggle petrol/electric fields in vehicle form
    const fuelTypeSelect = document.getElementById('fuelType');
    if (fuelTypeSelect) {
        fuelTypeSelect.addEventListener('change', function() {
            const petrolFields = document.getElementById('petrolFields');
            const electricFields = document.getElementById('electricFields');
            if (petrolFields && electricFields) {
                if (this.value === 'ELECTRIC') {
                    petrolFields.style.display = 'none';
                    electricFields.style.display = 'block';
                } else {
                    petrolFields.style.display = 'block';
                    electricFields.style.display = 'none';
                }
            }
        });
    }

    // Fade-in animation on scroll
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('fade-in-up');
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.1 });

    document.querySelectorAll('.animate-on-scroll').forEach(el => observer.observe(el));

    // Search input debounce
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        let timeout;
        searchInput.addEventListener('input', function() {
            clearTimeout(timeout);
            timeout = setTimeout(() => {
                if (this.form) this.form.submit();
            }, 800);
        });
    }
});
