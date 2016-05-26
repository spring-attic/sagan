System.config({
  defaultJSExtensions: true,
  transpiler: "traceur",
  paths: {
    "github:*": "jspm_packages/github/*",
    "npm:*": "jspm_packages/npm/*"
  },

  map: {
    "FortAwesome/font-awesome": "github:FortAwesome/font-awesome@3.2.1",
    "bootstrap": "github:twbs/bootstrap@2.3.2",
    "clipboard": "npm:clipboard@1.5.10",
    "eonasdan/bootstrap-datetimepicker": "github:eonasdan/bootstrap-datetimepicker@0.0.10",
    "font-awesome": "github:FortAwesome/font-awesome@3.2.1",
    "gmaps": "npm:gmaps@0.4.21",
    "google-code-prettify": "github:tcollard/google-code-prettify@1.0.4",
    "jquery": "npm:jquery@1.11.3",
    "jquery-timeago": "github:rmm5t/jquery-timeago@1.4.3",
    "most": "npm:most@0.2.4",
    "traceur": "github:jmcriffey/bower-traceur@0.0.93",
    "traceur-runtime": "github:jmcriffey/bower-traceur-runtime@0.0.93",
    "github:jspm/nodelibs-assert@0.1.0": {
      "assert": "npm:assert@1.3.0"
    },
    "github:jspm/nodelibs-process@0.1.2": {
      "process": "npm:process@0.11.2"
    },
    "github:jspm/nodelibs-util@0.1.0": {
      "util": "npm:util@0.10.3"
    },
    "npm:assert@1.3.0": {
      "util": "npm:util@0.10.3"
    },
    "npm:clipboard@1.5.10": {
      "good-listener": "npm:good-listener@1.1.7",
      "select": "npm:select@1.0.6",
      "tiny-emitter": "npm:tiny-emitter@1.0.2"
    },
    "npm:closest@0.0.1": {
      "matches-selector": "npm:matches-selector@0.0.1"
    },
    "npm:delegate@3.0.1": {
      "closest": "npm:closest@0.0.1"
    },
    "npm:good-listener@1.1.7": {
      "delegate": "npm:delegate@3.0.1"
    },
    "npm:inherits@2.0.1": {
      "util": "github:jspm/nodelibs-util@0.1.0"
    },
    "npm:jquery@1.11.3": {
      "process": "github:jspm/nodelibs-process@0.1.2"
    },
    "npm:most@0.2.4": {
      "process": "github:jspm/nodelibs-process@0.1.2"
    },
    "npm:process@0.11.2": {
      "assert": "github:jspm/nodelibs-assert@0.1.0"
    },
    "npm:util@0.10.3": {
      "inherits": "npm:inherits@2.0.1",
      "process": "github:jspm/nodelibs-process@0.1.2"
    }
  }
});
