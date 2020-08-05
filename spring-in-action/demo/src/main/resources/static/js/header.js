$(document).ready(function() {
    $("#fixed-bar-user-menu").hide();

    $("body").click(function(e) {
        if (!$("#header-user-profile-button").is(e.target) &&
            !($("#header-user-profile-button img").is(e.target))) {
            $("#fixed-bar-user-menu").hide();
        }
    });

    $("#header-user-profile-button").click(function() {
        $("#fixed-bar-user-menu").toggle();
    });
});