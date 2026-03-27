document.addEventListener('DOMContentLoaded', function() {
    const container = document.getElementById('authors-container');
    const addBtn = document.getElementById('add-author-btn');

    addBtn.addEventListener('click', function() {
        const index = container.querySelectorAll('.author-field').length;
        const div = document.createElement('div');
        div.className = 'author-field';
        div.innerHTML = `
            <input type="text"
                   class="form-control mt-2"
                   name="authors[${index}]"
                   placeholder="Lastname, Firstname"
                   required
                   pattern=".+,\\s*.+" maxlength="90" />
            <button type="button" class="btn btn-dark mt-2 remove-author-btn">
                Remove Author
            </button>
        `;
        container.appendChild(div);
    });

    container.addEventListener('click', function(e) {
        if (e.target.classList.contains('remove-author-btn')) {
            e.preventDefault();
            e.target.parentElement.remove();

            container.querySelectorAll('.author-field input').forEach((input, i) => {
                input.setAttribute('name', `authors[${i}]`);
            });
        }
    });
});

const pubYear = document.getElementById('pubYear');
const currentYear = new Date().getFullYear();
pubYear.addEventListener("input", (event) => {
    pubYear.setCustomValidity("");
    if (!pubYear.validity.valid) {
        return;
    }
    if (pubYear.value > currentYear) {
        pubYear.setCustomValidity("Year must not be in the future.");
    }
});