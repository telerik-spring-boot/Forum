document.querySelectorAll('.btn-outline-dark').forEach(button => {
    button.addEventListener('click', function () {
        document.querySelectorAll('.btn-outline-dark').forEach(btn => btn.classList.remove('active'));
        this.classList.add('active');

        var sortingBtns = document.getElementsByClassName('post-sort-option');
        Array.prototype.forEach.call(sortingBtns, function (sortingBtn) {
            if (document.getElementById('postsBtn').classList.contains('active')) {
                sortingBtn.classList.remove('d-none');
            } else {
                sortingBtn.classList.add('d-none');
            }
        });
    });
});


var buttonGroup1 = document.getElementById('buttonGroup1');
var buttonUp1 = document.getElementById('buttonUp1');
var buttonUp2 = document.getElementById('buttonUp2');
var buttonUp1PrevColor = buttonUp1.style.color;
var buttonUp2PrevColor = buttonUp2.style.color;


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

document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('sortTitle').addEventListener('click', function () {
        document.getElementById('sortByInput').value = 'title';
    });

    document.getElementById('sortLikes').addEventListener('click', function () {
        document.getElementById('sortByInput').value = 'likes';
    });

    document.getElementById('sortContent').addEventListener('click', function () {
        document.getElementById('sortByInput').value = 'content';
    });
});


document.addEventListener("DOMContentLoaded", function () {
    const applyButton = document.getElementById("applyFilterBtn");
    const inputs = document.querySelectorAll(".filter-option");
    const sortOptions = document.querySelectorAll(".sort-option");

    function checkIfAnyInputHasValue() {
        // Check if any input has a value
        const anyInputFilled = Array.from(inputs).some(input => input.value.trim() !== "");

        if (anyInputFilled) {
            applyButton.classList.remove('d-none');
        } else {
            applyButton.classList.add('d-none');
        }
    }

    // Show button when any filter input has text
    inputs.forEach(input => {
        input.addEventListener("input", checkIfAnyInputHasValue);
    });

    // Show button when a sorting option is clicked
    sortOptions.forEach(option => {
        option.addEventListener("click", function (event) {
            event.preventDefault();
            // document.getElementById("sortByInput").value = this.id.replace("sort", "").toLowerCase();
            applyButton.classList.remove('d-none');
        });
    });

    // Run once to check if fields are pre-filled (useful when refreshing the page)
    checkIfAnyInputHasValue();
});