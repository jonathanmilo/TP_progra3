// DOM Elements
const steps = document.querySelectorAll('.step');
const slider = document.getElementById('stepSlider');
const prevBtn = document.getElementById('prevBtn');
const nextBtn = document.getElementById('nextBtn');
const stepIndicator = document.getElementById('stepIndicator');

// State
let currentStep = 1;
let isScrolling = false;
let scrollTimeout = null;

// Debounce function for scroll events
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), wait);
    };
}

// Update visual states of steps
function updateStepStates() {
    steps.forEach((step, index) => {
        if (index + 1 === currentStep) {
            step.classList.add('highlighted');
        } else {
            step.classList.remove('highlighted');
        }
    });
}

// Update button states based on current step
function updateButtonStates() {
    prevBtn.disabled = currentStep === 1;
    nextBtn.disabled = currentStep === steps.length;
}

// Update step indicator text
function updateStepIndicator() {
    stepIndicator.textContent = `Step ${currentStep} of ${steps.length}`;
}

// Update all UI elements
function updateUI() {
    slider.value = currentStep;
    updateStepStates();
    updateButtonStates();
    updateStepIndicator();
}

// Scroll to a specific step
function scrollToStep(stepNumber) {
    if (stepNumber < 1 || stepNumber > steps.length) return;
    
    currentStep = stepNumber;
    isScrolling = true;
    
    const targetStep = document.getElementById(`step-${stepNumber}`);
    targetStep.scrollIntoView({
        behavior: 'smooth',
        block: 'center'
    });
    
    updateUI();
    
    // Reset isScrolling flag after animation completes
    clearTimeout(scrollTimeout);
    scrollTimeout = setTimeout(() => {
        isScrolling = false;
    }, 1000);
}

// Intersection Observer for scroll-based step detection
const observerOptions = {
    threshold: 0.5,
    rootMargin: '0px'
};

const observer = new IntersectionObserver((entries) => {
    // Only update if not currently programmatically scrolling
    if (isScrolling) return;
    
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            const stepNumber = parseInt(entry.target.id.split('-')[1]);
            currentStep = stepNumber;
            updateUI();
        }
    });
}, observerOptions);

// Observe all steps
steps.forEach(step => observer.observe(step));

// Slider event handler
slider.addEventListener('input', debounce((e) => {
    const stepNumber = parseInt(e.target.value);
    scrollToStep(stepNumber);
}, 100));

// Previous button handler
prevBtn.addEventListener('click', () => {
    if (currentStep > 1) {
        scrollToStep(currentStep - 1);
    }
});

// Next button handler
nextBtn.addEventListener('click', () => {
    if (currentStep < steps.length) {
        scrollToStep(currentStep + 1);
    }
});

// Handle manual scroll during animation
let lastScrollTime = Date.now();
const scrollHandler = debounce(() => {
    const currentTime = Date.now();
    const timeSinceLastScroll = currentTime - lastScrollTime;
    lastScrollTime = currentTime;
    
    // If user scrolls manually during animation, cancel the programmatic scroll
    if (isScrolling && timeSinceLastScroll < 100) {
        isScrolling = false;
        clearTimeout(scrollTimeout);
    }
}, 50);

window.addEventListener('scroll', scrollHandler, { passive: true });

// Keyboard navigation
document.addEventListener('keydown', (e) => {
    if (e.key === 'ArrowDown' || e.key === 'ArrowRight') {
        e.preventDefault();
        if (currentStep < steps.length) {
            scrollToStep(currentStep + 1);
        }
    } else if (e.key === 'ArrowUp' || e.key === 'ArrowLeft') {
        e.preventDefault();
        if (currentStep > 1) {
            scrollToStep(currentStep - 1);
        }
    }
});

// Initialize UI on page load
updateUI();
