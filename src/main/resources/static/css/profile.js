function toggleProfileMenu() {
    const menu = document.querySelector(".profile-menu");

    if (menu) {
        menu.classList.toggle("open");
    }
}

document.addEventListener("click", function (event) {
    const menu = document.querySelector(".profile-menu");

    if (menu && !menu.contains(event.target)) {
        menu.classList.remove("open");
    }
});

document.addEventListener("keydown", function (event) {
    if (event.key === "Escape") {
        const menu = document.querySelector(".profile-menu");

        if (menu) {
            menu.classList.remove("open");
        }
    }
});
