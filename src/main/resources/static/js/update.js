function deleteUpdate(id) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch("/admin/update/" + id, {
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

function resolveUpdate(id) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch("/admin/update/resolve/" + id, {
        method: "POST",
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