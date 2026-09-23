const toggle = document.querySelector('.menu-toggle');
const menu = document.querySelector('.nav-links');

toggle?.addEventListener('click', () => {
  const open = toggle.getAttribute('aria-expanded') === 'true';
  toggle.setAttribute('aria-expanded', String(!open));
  menu?.classList.toggle('open', !open);
});

document.querySelectorAll('.nav-links a').forEach(link => {
  link.addEventListener('click', () => {
    menu?.classList.remove('open');
    toggle?.setAttribute('aria-expanded', 'false');
  });
});

const observer = new IntersectionObserver(entries => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      entry.target.classList.add('visible');
      observer.unobserve(entry.target);
    }
  });
}, { threshold: 0.12 });

document.querySelectorAll('.reveal').forEach(el => observer.observe(el));

const header = document.querySelector('.site-header');
let lastY = window.scrollY;

window.addEventListener('scroll', () => {
  const currentY = window.scrollY;
  if (!header) return;

  if (currentY > 100) {
    header.style.transform = currentY > lastY ? 'translateY(-5px)' : 'translateY(0)';
  } else {
    header.style.transform = 'translateY(0)';
  }

  lastY = currentY;
}, { passive: true });