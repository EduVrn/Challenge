$(document).ready(function () {
    $(".avatar").addClass("col-md-4 col-sm-6 col-xs-12");
    $(".avatar-image").addClass("text-center col-md-12 col-sm-12 col-xs-12");
    $(".bind-account").addClass("col-md-6 col-sm-6 col-xs-6");
    $(".underAvatarButton").addClass("btn btn-default text-center col-md-12 col-sm-12 col-xs-12");
    $(".rate-iconP").addClass("text-center col-md-6 col-sm-6 col-xs-6").removeClass("rate-iconP");
    $(".rate-blockP").addClass("col-md-12 col-sm-12 col-xs-12").removeClass("rate-blockP");
    $(".elementOfListInProfile").addClass("challenge col-lg-4 col-md-6 col-sm-12 col-xs-12");
    $(".userLists").addClass("col-md-8 col-sm-6 col-xs-12");
});
(function ($) {
    $(window).on("load", function () {
        if ($(window).height() < ($('.fixed_profile_info').height() + 70)) {
            $('.fixed_profile_info').css('position', 'initial');
        }
        var t = setTimeout(function () {
           $(".firstCollapsable").click();
        }, 700);
    });
    $(window).on("resize", function () {
        if ($(window).height() > ($('.fixed_profile_info').height() + 70)) {
            $('.fixed_profile_info').css('position', '');
            if ($(window).scrollTop() < 120) {
                $('.fixed_profile_info').removeClass('affix');
                $('.fixed_profile_info').addClass('affix-top');
            }
        } else {
            $('.fixed_profile_info').css('position', 'initial');
        }
    });
    $(window).on("scroll", function () {
        if ($(window).height() > ($('.fixed_profile_info').height() + 70)) {
            if ($(window).scrollTop() > 120) {
                $('.fixed_profile_info').removeClass('affix-top');
                $('.fixed_profile_info').addClass('affix');
            }
        }
    });
})(jQuery);
