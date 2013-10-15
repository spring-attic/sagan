(function(global) {
  var buster = require('buster');
  buster.spec.expose();
  global.expect = buster.expect;
}(typeof window === 'undefined' && typeof global !== 'undefined' ? global : this));
