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
