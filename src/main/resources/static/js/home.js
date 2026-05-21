/* ==========================================================================
   AutoMart Homepage — Interactions & Animations
   ========================================================================== */

(function () {
    'use strict';

    /* ── 1. Navbar: add shadow/backdrop on scroll ── */
    const mainNav = document.getElementById('mainNav');
    if (mainNav) {
        const onScroll = () => {
            if (window.scrollY > 50) {
                mainNav.style.background = 'rgba(6,13,31,0.96)';
                mainNav.style.backdropFilter = 'blur(20px)';
                mainNav.style.boxShadow = '0 4px 28px rgba(0,0,0,0.40)';
            } else {
                mainNav.style.background = '';
                mainNav.style.backdropFilter = '';
                mainNav.style.boxShadow = '';
            }
        };
        window.addEventListener('scroll', onScroll, { passive: true });
        onScroll();
    }

    /* ── 2. Mobile menu toggle ── */
    const menuBtn   = document.getElementById('mobileMenuBtn');
    const mobileMenu = document.getElementById('mobileMenu');
    const iconOpen   = document.getElementById('menuIconOpen');
    const iconClose  = document.getElementById('menuIconClose');

    if (menuBtn && mobileMenu) {
        menuBtn.addEventListener('click', () => {
            const isOpen = mobileMenu.classList.toggle('open');
            if (iconOpen)  iconOpen.style.display  = isOpen ? 'none'  : 'block';
            if (iconClose) iconClose.style.display = isOpen ? 'block' : 'none';
        });

        /* Close mobile menu on link click */
        mobileMenu.querySelectorAll('a').forEach(link => {
            link.addEventListener('click', () => {
                mobileMenu.classList.remove('open');
                if (iconOpen)  iconOpen.style.display  = 'block';
                if (iconClose) iconClose.style.display = 'none';
            });
        });
    }

    /* ── 3. Scroll-reveal via IntersectionObserver ── */
    const revealElements = document.querySelectorAll('.reveal');
    if ('IntersectionObserver' in window && revealElements.length) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('visible');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.10, rootMargin: '0px 0px -40px 0px' });

        revealElements.forEach(el => observer.observe(el));
    } else {
        /* Fallback for older browsers */
        revealElements.forEach(el => el.classList.add('visible'));
    }

    /* ── 4. Count-up animation for hero stats ── */
    function countUp(el) {
        const target = parseInt(el.getAttribute('data-count'), 10);
        if (!target || target <= 0) return;
        const duration = 1600;
        const startTime = performance.now();

        const update = (currentTime) => {
            const elapsed  = currentTime - startTime;
            const progress = Math.min(elapsed / duration, 1);
            /* Ease-out cubic */
            const eased = 1 - Math.pow(1 - progress, 3);
            const current = Math.round(eased * target);
            el.textContent = current >= 1000 ? current.toLocaleString() : current;
            if (progress < 1) requestAnimationFrame(update);
        };
        requestAnimationFrame(update);
    }

    const statEls = document.querySelectorAll('[data-count]');
    if ('IntersectionObserver' in window && statEls.length) {
        const statObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    countUp(entry.target);
                    statObserver.unobserve(entry.target);
                }
            });
        }, { threshold: 0.50 });
        statEls.forEach(el => statObserver.observe(el));
    }

    /* ── 5. Smooth scroll for anchor links ── */
    document.querySelectorAll('a[href^="#"]').forEach(link => {
        link.addEventListener('click', e => {
            const id = link.getAttribute('href');
            const target = document.querySelector(id);
            if (target) {
                e.preventDefault();
                target.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        });
    });

    /* ── 6. Vehicle/Part card ripple effect on click ── */
    document.querySelectorAll('.vehicle-card, .part-card').forEach(card => {
        card.addEventListener('click', function (e) {
            const ripple = document.createElement('span');
            const rect = card.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            ripple.style.cssText = `
                position:absolute; border-radius:50%;
                width:${size}px; height:${size}px;
                top:${e.clientY - rect.top - size / 2}px;
                left:${e.clientX - rect.left - size / 2}px;
                background:rgba(59,130,246,0.12);
                transform:scale(0); animation:ripple 0.5s ease-out forwards;
                pointer-events:none; z-index:100;
            `;
            card.style.position = 'relative';
            card.style.overflow = 'hidden';
            card.appendChild(ripple);
            setTimeout(() => ripple.remove(), 600);
        });
    });

})();
