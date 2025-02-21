// pagination.js
document.addEventListener("DOMContentLoaded", function () {
    const commentsPerPage = 5;
    const comments = document.querySelectorAll("#comments-container .comment-box");
    const totalPages = Math.ceil(comments.length / commentsPerPage);
    const paginationControls = document.getElementById("pagination-controls");

    function showPage(pageNumber) {
        // Hide all comments
        comments.forEach(comment => comment.style.display = "none");

        // Show the comments for the current page
        const start = pageNumber * commentsPerPage;
        const end = start + commentsPerPage;
        for (let i = start; i < end && i < comments.length; i++) {
            comments[i].style.display = "block";
        }

        // Update pagination buttons
        updatePaginationButtons(pageNumber);
    }

    function updatePaginationButtons(currentPage) {
        paginationControls.innerHTML = ""; // Clear existing buttons

        // Create "Previous" button
        const prevButton = document.createElement("li");
        prevButton.classList.add("page-item");
        prevButton.classList.toggle("disabled", currentPage === 0);
        prevButton.innerHTML = `<a class="page-link" href="#">Previous</a>`;
        prevButton.addEventListener("click", () => showPage(currentPage - 1));
        paginationControls.appendChild(prevButton);

        // Create a button for the first page
        if (currentPage > 1) {
            const firstButton = document.createElement("li");
            firstButton.classList.add("page-item");
            firstButton.innerHTML = `<a class="page-link" href="#">1</a>`;
            firstButton.addEventListener("click", () => showPage(0));
            paginationControls.appendChild(firstButton);

            const ellipsis = document.createElement("li");
            ellipsis.classList.add("page-item");
            ellipsis.innerHTML = `<span class="page-link">...</span>`;
            paginationControls.appendChild(ellipsis);
        }

        // Create buttons for the previous, current, and next pages
        for (let i = currentPage - 1; i <= currentPage + 1; i++) {
            if (i >= 0 && i < totalPages) {
                const pageButton = document.createElement("li");
                pageButton.classList.add("page-item");
                pageButton.classList.toggle("active", i === currentPage);
                pageButton.innerHTML = `<a class="page-link" href="#">${i + 1}</a>`;
                pageButton.addEventListener("click", () => showPage(i));
                paginationControls.appendChild(pageButton);
            }
        }

        // Create a button for the last page
        if (currentPage < totalPages - 2) {
            const ellipsis = document.createElement("li");
            ellipsis.classList.add("page-item");
            ellipsis.innerHTML = `<span class="page-link">...</span>`;
            paginationControls.appendChild(ellipsis);

            const lastButton = document.createElement("li");
            lastButton.classList.add("page-item");
            lastButton.innerHTML = `<a class="page-link" href="#">${totalPages}</a>`;
            lastButton.addEventListener("click", () => showPage(totalPages - 1));
            paginationControls.appendChild(lastButton);
        }

        // Create "Next" button
        const nextButton = document.createElement("li");
        nextButton.classList.add("page-item");
        nextButton.classList.toggle("disabled", currentPage === totalPages - 1);
        nextButton.innerHTML = `<a class="page-link" href="#">Next</a>`;
        nextButton.addEventListener("click", () => showPage(currentPage + 1));
        paginationControls.appendChild(nextButton);
    }

    // Show the first page by default
    showPage(0);
});
