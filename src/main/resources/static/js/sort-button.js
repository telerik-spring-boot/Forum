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
            sortByInput.value = this.id.replace("sort", "");
            
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


var buttonUp1 = document.getElementById('buttonUp1');
var buttonUp2 = document.getElementById('buttonUp2');
if (buttonUp1 && buttonUp2) {
    var buttonUp1PrevColor = window.getComputedStyle(buttonUp1).color;
    var buttonUp2PrevColor = window.getComputedStyle(buttonUp2).color;


    buttonUp1.addEventListener('click', function () {
        buttonUp1.classList.add('active');
        buttonUp1.style.color = 'green'
        buttonUp2.classList.remove('active');
        buttonUp2.style.color = buttonUp2PrevColor;
    });

    buttonUp2.addEventListener('click', function () {
        buttonUp2.classList.add('active');
        buttonUp2.style.color = 'red'
        buttonUp1.classList.remove('active');
        buttonUp1.style.color = buttonUp1PrevColor;

    });
}

