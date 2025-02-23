document.addEventListener('DOMContentLoaded', () => {

    const itemLists = document.querySelectorAll('.itemList');

    itemLists.forEach((itemList) => {
        let initialVisibleCount = 10;
        let items = itemList.querySelectorAll('.item');
        let currentVisibleCount = initialVisibleCount;

        for (let i = initialVisibleCount; i < items.length; i++) {
            items[i].style.display = 'none';
        }


        function checkScrollPosition() {
            if (window.innerHeight + window.scrollY >= document.body.scrollHeight - 100) {
                loadMoreItems();
            }
        }

        window.addEventListener('scroll', checkScrollPosition);


        function loadMoreItems() {
            for (let i = currentVisibleCount; i < currentVisibleCount + initialVisibleCount; i++) {
                if (items[i]) {
                    items[i].style.display = 'block';
                }
            }
            currentVisibleCount += initialVisibleCount;

            if (currentVisibleCount >= items.length) {
                window.removeEventListener('scroll', loadMoreItems);
            }
        }
    });
});


document.addEventListener('DOMContentLoaded', () => {

    const backToTopBtn = document.getElementById('backToTopBtn');

    window.addEventListener('scroll', () => {
        if (window.scrollY > 300) {
            backToTopBtn.classList.add('show');
        } else {
            backToTopBtn.classList.remove('show');
        }
    });

    backToTopBtn.addEventListener('click', () => {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });
});

