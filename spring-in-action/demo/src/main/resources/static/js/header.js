$(document).ready(() => {
    $("#fixed-bar-user-menu").hide();

    $("body").click((e) => {
        if (!$("#header-user-profile-button").is(e.target) &&
            !($("#header-user-profile-button img").is(e.target))) {
            $("#fixed-bar-user-menu").hide();
        }
    });

    $("#header-user-profile-button").click(() => {
        $("#fixed-bar-user-menu").toggle();
    });

    $("#menu-item-logout").click(() => {
        $("#logoutForm").trigger("submit");
    });

    // $("#menu-item-logout").on("click", () => {
    //     $.ajax({
    //         url: "",
    //         data: "",
    //         type: "POST",
    //         dataType: "json"
    //     });
    // });
});