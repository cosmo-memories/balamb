function deleteBook(id) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch("/admin/book/" + id, {
        method: "DELETE",
        headers: {
            "X-Requested-With": "XMLHttpRequest",
            [header]: token
        }
    }).then(response => {
        if (response.ok) {
            location.reload();
        } else {
            alert("Something went wrong!")
        }
    });
}

const toggleBtn = document.getElementById("toggle");
const editPanel = document.getElementById("editPanel");

editPanel.addEventListener("shown.bs.collapse", () => {
    toggleBtn.textContent = "Hide";
});

editPanel.addEventListener("hidden.bs.collapse", () => {
    toggleBtn.textContent = "Edit Book";
});