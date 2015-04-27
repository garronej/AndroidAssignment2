jQuery("#simulation")
  .on("click", ".s-6fa59add-59f9-4860-82e0-3f74076f6a60 .click", function(event, data) {
    var jEvent, jFirer, cases;
    if(data === undefined) { data = event; }
    jEvent = jimEvent(event);
    jFirer = jEvent.getEventFirer();
    if(jFirer.is("#s-Label_32")) {
      cases = [
        {
          "blocks": [
            {
              "actions": [
                {
                  "action": "jimNavigation",
                  "parameter": {
                    "target": "screens/f23e8f89-bd35-4ddf-8f13-b078e8a98778"
                  }
                }
              ]
            }
          ]
        }
      ];
      event.data = data;
      jEvent.launchCases(cases);
    } else if(jFirer.is("#s-Image_24")) {
      cases = [
        {
          "blocks": [
            {
              "actions": [
                {
                  "action": "jimNavigation",
                  "parameter": {
                    "isbackward": true
                  }
                }
              ]
            }
          ]
        }
      ];
      event.data = data;
      jEvent.launchCases(cases);
    } else if(jFirer.is("#s-Label_5")) {
      cases = [
        {
          "blocks": [
            {
              "actions": [
                {
                  "action": "jimPause",
                  "parameter": {
                    "pause": 100
                  }
                },
                {
                  "action": "jimChangeStyle",
                  "parameter": [ {
                    "#s-6fa59add-59f9-4860-82e0-3f74076f6a60 #s-Label_5": {
                      "attributes": {
                        "background-color": "#434343",
                        "background-image": "none",
                        "opacity": "0.6"
                      }
                    }
                  },{
                    "#s-6fa59add-59f9-4860-82e0-3f74076f6a60 #s-Label_5": {
                      "attributes-ie": {
                        "-pie-background": "#434343",
                        "-pie-poll": "false",
                        "-ms-filter": "progid:DXImageTransform.Microsoft.Alpha(Opacity=60)",
                        "filter": "alpha(opacity=60)"
                      }
                    }
                  },{
                    "#s-6fa59add-59f9-4860-82e0-3f74076f6a60 #s-Label_5": {
                      "attributes-ie8lte": {
                        "-ms-filter": "progid:DXImageTransform.Microsoft.Alpha(Opacity=60)",
                        "filter": "alpha(opacity=60)"
                      }
                    }
                  } ]
                }
              ]
            }
          ]
        },
        {
          "blocks": [
            {
              "actions": [
                {
                  "action": "jimNavigation",
                  "parameter": {
                    "target": "screens/57093960-5fed-42ca-9ca1-fdb3ea2a5a1a"
                  }
                }
              ]
            }
          ]
        }
      ];
      event.data = data;
      jEvent.launchCases(cases);
    } else if(jFirer.is("#s-Label_6")) {
      cases = [
        {
          "blocks": [
            {
              "actions": [
                {
                  "action": "jimPause",
                  "parameter": {
                    "pause": 100
                  }
                },
                {
                  "action": "jimChangeStyle",
                  "parameter": [ {
                    "#s-6fa59add-59f9-4860-82e0-3f74076f6a60 #s-Label_6": {
                      "attributes": {
                        "background-color": "#434343",
                        "background-image": "none",
                        "opacity": "0.6"
                      }
                    }
                  },{
                    "#s-6fa59add-59f9-4860-82e0-3f74076f6a60 #s-Label_6": {
                      "attributes-ie": {
                        "-pie-background": "#434343",
                        "-pie-poll": "false",
                        "-ms-filter": "progid:DXImageTransform.Microsoft.Alpha(Opacity=60)",
                        "filter": "alpha(opacity=60)"
                      }
                    }
                  },{
                    "#s-6fa59add-59f9-4860-82e0-3f74076f6a60 #s-Label_6": {
                      "attributes-ie8lte": {
                        "-ms-filter": "progid:DXImageTransform.Microsoft.Alpha(Opacity=60)",
                        "filter": "alpha(opacity=60)"
                      }
                    }
                  } ]
                }
              ]
            }
          ]
        },
        {
          "blocks": [
            {
              "actions": [
                {
                  "action": "jimNavigation",
                  "parameter": {
                    "target": "screens/cd24bf32-a2cb-4bae-80e7-7fd85d1bd5a0"
                  }
                }
              ]
            }
          ]
        }
      ];
      event.data = data;
      jEvent.launchCases(cases);
    }
  });