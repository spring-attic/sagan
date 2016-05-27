var $ = require('jquery');
var PWS = require('pws-sdk');

module.exports = function initDeployPws() {

    $(ready);

    return {
        destroy: destroy
    };

    function ready() {
        $.QueryString = (function(a) {
            if (a == "") return {};
            var b = {};
            for (var i = 0; i < a.length; ++i)
            {
                var p=a[i].split('=');
                if (p.length != 2) continue;
                b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, " "));
            }
            return b;
        })(window.location.search.substr(1).split('&'));

        PWS.getLoginStatus(function (response) {
            var status = response.status;
            if (status === 'connected') {
                //Logged In
                var state = response.state;
                var token = response.token;
                var repo = $.QueryString["descriptor"];

                //Get User Info
                PWS.getUserInfo(state, token, repo, function (payload) {
                    //Let user choose deployPlan, organization
                    if (payload.errorMsg) {
                        $("#alert-error").show();
                    } else {
                        // populate the fields in the modal
                        $('#organizations').append(
                            $.map(payload.organizations, function (org) {
                                return '<option value="' + org.guid + '">' + org.name + '</option>';
                            }).join("")
                        );

                        $('#space-name').text(payload.deployPlan.spaces[0].name);
                        $('#app-name').text(payload.deployPlan.spaces[0].apps[0].name);

                        var services = payload.deployPlan.spaces[0].apps[0].bind_services;
                        if (services.length == 0) {
                            $('#display-services').hide();
                        } else {
                            $('#display-services').show();
                        }
                        $('#service-names').append(
                            $.map(services, function (service) {
                                return '<li class="list-group-item">' + service + '</li>';
                            }).join("")
                        );

                        // Show the configuration form
                        $('#spinner-loading').hide();
                        $('#deploy-config').show();

                        // When the Deploy button is clicked
                        $("#deploy-action").click(function () {

                            // Check if the user has selected an organization.
                            var selectedOrg = $('#organizations option:selected');
                            if (selectedOrg.val() == '---') {
                                // NO - Inform the user to select an org
                                $('#alert-org').show();
                            } else {
                                // YES - Submit the push process
                                $('#alert-org').hide();

                                payload.deployPlan.orgName = selectedOrg.text();
                                payload.deployPlan.orgUuid = selectedOrg.val();

                                var newPayload = {
                                    deployPlan: payload.deployPlan
                                };

                                PWS.push(state, token, newPayload, function (appData) {
                                    // Swap the deploy form to the status panel
                                    $('#deploy-config').hide();
                                    $('#deploy-status').show();

                                    // Set the links to access the app and the PWS console
                                    $('#pws-console').attr('href', PWS.getAppConsole(appData.pushId));
                                    $('#pws-app').attr('href', PWS.getAppRoute(appData.pushId));

                                    // Keep updating the status every seconds
                                    var interval = setInterval(function () {
                                        PWS.getAppStatus(appData.pushId, function (data) {
                                            var lines = $("#deploy-console strong").length;
                                            $.each(data.slice(lines), function(){
                                                $("#deploy-console").append('<strong>&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt; </strong>' + this + '</br>');
                                            });
                                            $("#deploy-console-wrapper").animate({ scrollTop: $("#deploy-console-wrapper")[0].scrollHeight}, 500);
                                        });
                                    }, 1000);

                                    // If the Stop button is pressed, stop updating the status
                                    $('#stop-updates').click(function () {
                                        clearInterval(interval);
                                        $('#stop-updates').hide();
                                    });
                                });
                            }
                        });
                    }
                });
            }
            else {
                $('#spinner-loading').hide();
                $('#alert-auth').show();
            }
        });

    }

    function destroy() {}
};