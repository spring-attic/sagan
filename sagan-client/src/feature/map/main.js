var $ = require('jquery');
var reduce = Array.prototype.reduce;

var teamMapWrapper = '.js-team-map--wrapper';
var teamMapContainer = '.js-team-map--container';
var fadeDuration = 100;

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

    System.import('gmaps').then(function (GMaps) {
        $(function() {
            ready(GMaps);
        });
    });

    return {
        destroy: destroy
    };

    function ready(GMaps) {
        map = createMap(GMaps);
        var teamMemberIds = getTeamMemberIdMap($('.team-members--wrapper'));

        teamLocations.forEach(function(teamLocation) {
            var element = teamMemberIds[teamLocation.memberId];
            if (element) {
                createMarker(map, teamLocation, element);
            }
        });

        setMapView(map, teamLocations);

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
        $(teamMapContainer).fadeOut(fadeDuration);
        $(teamMapWrapper).mouseleave(function() {
            $(teamMapContainer).fadeIn(fadeDuration);
        });
    }
}

function createMap(GMaps) {
    var map = new GMaps({
        div: '#map',
        lat: 51.505,
        lng: -0.09,
        disableDefaultUI: true
    });

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

function createMarker(map, teamLocation, element) {

    map.addMarker({
        lat: teamLocation.latitude,
        lng: teamLocation.longitude,
        title: teamLocation.name,
        infoWindow: {content: $(element).html()}
    });
}

function setMapView(map, teamLocations) {
    var length = teamLocations.length;

    if (length > 1) {
        map.fitZoom();
    } else if (length == 1) {
        map.setCenter(teamLocations[0].latitude,teamLocations[0].longitude);
        map.setZoom(5);
    }
}