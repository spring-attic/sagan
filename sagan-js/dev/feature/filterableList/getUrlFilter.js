module.exports = getUrlFilter;

function getUrlFilter() {
    // grab first "filter=..." param in url
    var query = document.location.search;
    var matches = query.match(/[&?]filter=([^&]+)/);
    return matches ? decodeURIComponent(matches[1]) : '';
}
