window.Spring = window.Spring || {};

Spring.CreateMap = function () {
    var map = L.map('map', {
        scrollWheelZoom: false,
        touchZoom: false
    }).setView([51.505, -0.09], 2);
    L.tileLayer('http://{s}.tile.cloudmade.com/dc6ad76c483d4e5c92152aa34375ec28/1/256/{z}/{x}/{y}.png', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://cloudmade.com">CloudMade</a>',
        maxZoom: 18
    }).addTo(map);

    var bounds = [];
    var length = teamLocations.length;
    var teamLocation = null;
    for (var i = 0; i < length; i++) {
        teamLocation = teamLocations[i];

        var element = $('.team-members--wrapper').find("[data-member-id='" + teamLocation.memberId + "']");
        if (element) {
            var marker = L.marker([teamLocation.latitude, teamLocation.longitude], {title: teamLocation.name});
            marker.bindPopup(element.html());
            marker.addTo(map);
            bounds.push(new L.LatLng(teamLocation.latitude, teamLocation.longitude));
        }
    }
    if (length > 1) {
        map.fitBounds(bounds);
    } else if (length == 1) {
        var latLng = new L.LatLng(teamLocations[0].latitude, teamLocations[0].longitude);
        map.setView(latLng, 5);
    }

    $(".js-team-map--wrapper").click(function() {
        map.scrollWheelZoom.enable();
        map.touchZoom.enable();
        $(".js-team-map--container").fadeOut("100");
        $(".js-team-map--wrapper").mouseleave(function() {
            map.scrollWheelZoom.disable();
            map.touchZoom.disable();
            $(".js-team-map--container").fadeIn("100");
        });
      });

};
