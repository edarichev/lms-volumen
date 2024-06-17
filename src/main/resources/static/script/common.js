/**
 * 
 */
function fnConfirmDeleteCategory(msg, actionUrl) {
    if (confirm(msg))
        window.location.replace(actionUrl);
}