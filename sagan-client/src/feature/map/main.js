var $ = require('jquery');
var reduce = Array.prototype.reduce;

var teamMapWrapper = '.js-team-map--wrapper';
var teamMapContainer = '.js-team-map--container';
var fadeDuration = 100;

var mapboxToken = "pk.eyJ1Ijoic2FnYW5vcHMiLCJhIjoiWWdDMV9IOCJ9.vaLPw9uztUp6uvYof0ZM6g";

/**
 * Composition plan for the data-map feature
 * @module
 */
module.exports = initMap;

/**
 * Creates the map and populates it with data found in the server-rendered
 * teamLocations global.  It's unfortunate to rely on a global, but
 * because the data is server-rendered it's a reasonable short term
 * compromise.
 * @returns {{destroy: Function}}
 */
function initMap() {
    /*global teamLocations*/
    var map, _destroy;

    _destroy = function() {};

    require(['leaflet'], function(leaflet) {
        $(function() {
            // manually set image path since leaflet isn't loaded by a script tag
            leaflet.Icon.Default.imagePath = '/lib/leaflet/dist/images';
            ready(leaflet);
        });
    });

    return {
        destroy: destroy
    };

    function ready(leaflet) {
        map = createMap(leaflet);

        var teamMemberIds = getTeamMemberIdMap($('.team-members--wrapper'));

        var bounds = teamLocations.reduce(function(bounds, teamLocation) {
            var element = teamMemberIds[teamLocation.memberId];

            if (element) {
                var marker = createMarker(leaflet, teamLocation, element);
                marker.addTo(map);

                bounds.push(new leaflet.LatLng(teamLocation.latitude, teamLocation.longitude));
            }

            return bounds;
        }, []);

        setMapView(leaflet, map, teamLocations, bounds);

        $(teamMapWrapper).on('click', enableMapMouseWheelSupport);

        _destroy = function() {
            map.remove();
            $(teamMapWrapper).off('click', enableMapMouseWheelSupport);
        };
    }

    function destroy() {
        _destroy();
    }

    function enableMapMouseWheelSupport() {
        map.scrollWheelZoom.enable();
        map.touchZoom.enable();
        $(teamMapContainer).fadeOut(fadeDuration);

        $(teamMapWrapper).mouseleave(function() {
            map.scrollWheelZoom.disable();
            map.touchZoom.disable();
            $(teamMapContainer).fadeIn(fadeDuration);
        });
    }
}

function createMap(leaflet) {
    var map = leaflet.map('map', {
        scrollWheelZoom: false,
        touchZoom: false
    }).setView([51.505, -0.09], 2);

    leaflet.tileLayer('https://{s}.tiles.mapbox.com/v4/saganops.io2c9g52/{z}/{x}/{y}.png?access_token='+mapboxToken, {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
        maxZoom: 18
    }).addTo(map);

    return map;
}

function getTeamMemberIdMap(container) {
    return reduce.call($('[data-member-id]', container), function(ids, el) {
        var id = el.getAttribute('data-member-id');
        if(id != null) {
            ids[id] = el;
        }
        return ids;
    }, {});
}

function createMarker(leaflet, teamLocation, element) {
    var marker = leaflet.marker([teamLocation.latitude, teamLocation.longitude], {title: teamLocation.name});
    return marker.bindPopup($(element).html());
}

function setMapView(leaflet, map, teamLocations, bounds) {
    var length = teamLocations.length;

    if (length > 1) {
        map.fitBounds(bounds);
    } else if (length == 1) {
        var latLng = new leaflet.LatLng(
            teamLocations[0].latitude, teamLocations[0].longitude);
        map.setView(latLng, 5); // Why 5?
    }
}
