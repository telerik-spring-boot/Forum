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