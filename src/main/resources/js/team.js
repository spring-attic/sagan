$(document).ready(function() {
    var map = L.map('map').setView([51.505, -0.09], 2);
    L.tileLayer('http://{s}.tile.cloudmade.com/dc6ad76c483d4e5c92152aa34375ec28/1/256/{z}/{x}/{y}.png', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://cloudmade.com">CloudMade</a>',
        maxZoom: 18
    }).addTo(map);

    var bounds = [];
    var length = teamLocations.length;
    var element = null;
    for (var i = 0; i < length; i++) {
        element = teamLocations[i];
        var marker = L.marker([element.latitude, element.longitude], {title: element.name});
        marker.bindPopup(element.name);
        marker.addTo(map);
        bounds.push(new L.LatLng(element.latitude, element.longitude));
    }
    map.fitBounds(bounds);
});