jQuery("#simulation")
  .on("click", ".s-c33f51f5-e968-4ecd-a296-3768d48f3b7d .click", function(event, data) {
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
                    "target": "screens/cadcdbd5-d639-4865-b840-0c142b724760"
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
                    "#s-c33f51f5-e968-4ecd-a296-3768d48f3b7d #s-Label_5": {
                      "attributes": {
                        "background-color": "#434343",
                        "background-image": "none",
                        "opacity": "0.6"
                      }
                    }
                  },{
                    "#s-c33f51f5-e968-4ecd-a296-3768d48f3b7d #s-Label_5": {
                      "attributes-ie": {
                        "-pie-background": "#434343",
                        "-pie-poll": "false",
                        "-ms-filter": "progid:DXImageTransform.Microsoft.Alpha(Opacity=60)",
                        "filter": "alpha(opacity=60)"
                      }
                    }
                  },{
                    "#s-c33f51f5-e968-4ecd-a296-3768d48f3b7d #s-Label_5": {
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
                    "target": "screens/193ae028-6f59-4f1e-bc7d-256dc234d694"
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
                    "#s-c33f51f5-e968-4ecd-a296-3768d48f3b7d #s-Label_6": {
                      "attributes": {
                        "background-color": "#434343",
                        "background-image": "none",
                        "opacity": "0.6"
                      }
                    }
                  },{
                    "#s-c33f51f5-e968-4ecd-a296-3768d48f3b7d #s-Label_6": {
                      "attributes-ie": {
                        "-pie-background": "#434343",
                        "-pie-poll": "false",
                        "-ms-filter": "progid:DXImageTransform.Microsoft.Alpha(Opacity=60)",
                        "filter": "alpha(opacity=60)"
                      }
                    }
                  },{
                    "#s-c33f51f5-e968-4ecd-a296-3768d48f3b7d #s-Label_6": {
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