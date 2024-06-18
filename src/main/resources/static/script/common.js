/**
 * 
 */
function fnConfirmDeleteCategory(msg, actionUrl, id) {
    if (confirm(msg))
        window.location.replace(actionUrl + id);
}