document.addEventListener("DOMContentLoaded", function () {
    const applyButton = document.getElementById("applyFilterBtn");
    const sortByInput = document.getElementById("sortByInput");
    const filterInputs = document.querySelectorAll(".filter-option");
    const sortOptions = document.querySelectorAll(".sort-option");

    function updateApplyButtonVisibility() {
        const isSortSelected = sortByInput.value.trim() !== "";


        const isAnyFilterFilled = Array.from(filterInputs).some(input => input.value.trim() !== "");

        if (isSortSelected || isAnyFilterFilled) {
            applyButton.classList.remove("d-none");
        } else {
            applyButton.classList.add("d-none");
        }
    }

    sortOptions.forEach(option => {
        option.addEventListener("click", function (event) {
            event.preventDefault();
            var sortOrderInput = document.getElementById("sortOrderInput");
            sortByInput.value = this.id.replace("sort", "");

            console.log(sortByInput.value);
            if (sortByInput.value === "createdAt" || sortByInput.value === "likes") {
                sortOrderInput.value = "desc";
            } else {
                sortOrderInput.value = "asc";
            }

            if (option.classList.contains('active')) {
                sortByInput.value = "";
                option.classList.remove('active');
            } else {
                sortOptions.forEach(option => option.classList.remove('active'));
                option.classList.add('active');
            }

            updateApplyButtonVisibility();
        });
    });

    filterInputs.forEach(input => {
        input.addEventListener("input", updateApplyButtonVisibility);
    });

    updateApplyButtonVisibility();
});

document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".clickable-entity").forEach(button => {
        button.addEventListener("click", function () {
            window.location.href = this.getAttribute('redirect-link');
        });
    });
});



