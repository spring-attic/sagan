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
    "clipboard": "npm:clipboard@1.5.12",
    "eonasdan/bootstrap-datetimepicker": "github:eonasdan/bootstrap-datetimepicker@0.0.10",
    "font-awesome": "github:FortAwesome/font-awesome@3.2.1",
    "gmaps": "npm:gmaps@0.4.24",
    "google-code-prettify": "github:tcollard/google-code-prettify@1.0.4",
    "jquery": "npm:jquery@1.12.4",
    "jquery-timeago": "github:rmm5t/jquery-timeago@1.5.3",
    "most": "npm:most@0.2.4",
    "traceur": "github:jmcriffey/bower-traceur@0.0.93",
    "traceur-runtime": "github:jmcriffey/bower-traceur-runtime@0.0.93",
    "github:jspm/nodelibs-assert@0.1.0": {
      "assert": "npm:assert@1.4.1"
    },
    "github:jspm/nodelibs-buffer@0.1.0": {
      "buffer": "npm:buffer@3.6.0"
    },
    "github:jspm/nodelibs-process@0.1.2": {
      "process": "npm:process@0.11.5"
    },
    "github:jspm/nodelibs-util@0.1.0": {
      "util": "npm:util@0.10.3"
    },
    "github:jspm/nodelibs-vm@0.1.0": {
      "vm-browserify": "npm:vm-browserify@0.0.4"
    },
    "npm:assert@1.4.1": {
      "assert": "github:jspm/nodelibs-assert@0.1.0",
      "buffer": "github:jspm/nodelibs-buffer@0.1.0",
      "process": "github:jspm/nodelibs-process@0.1.2",
      "util": "npm:util@0.10.3"
    },
    "npm:buffer@3.6.0": {
      "base64-js": "npm:base64-js@0.0.8",
      "child_process": "github:jspm/nodelibs-child_process@0.1.0",
      "fs": "github:jspm/nodelibs-fs@0.1.2",
      "ieee754": "npm:ieee754@1.1.6",
      "isarray": "npm:isarray@1.0.0",
      "process": "github:jspm/nodelibs-process@0.1.2"
    },
    "npm:clipboard@1.5.12": {
      "good-listener": "npm:good-listener@1.1.7",
      "select": "npm:select@1.0.6",
      "tiny-emitter": "npm:tiny-emitter@1.1.0"
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
    "npm:jquery@1.12.4": {
      "process": "github:jspm/nodelibs-process@0.1.2"
    },
    "npm:most@0.2.4": {
      "process": "github:jspm/nodelibs-process@0.1.2"
    },
    "npm:process@0.11.5": {
      "assert": "github:jspm/nodelibs-assert@0.1.0",
      "fs": "github:jspm/nodelibs-fs@0.1.2",
      "vm": "github:jspm/nodelibs-vm@0.1.0"
    },
    "npm:util@0.10.3": {
      "inherits": "npm:inherits@2.0.1",
      "process": "github:jspm/nodelibs-process@0.1.2"
    },
    "npm:vm-browserify@0.0.4": {
      "indexof": "npm:indexof@0.0.1"
    }
  }
});
