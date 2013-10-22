var os = require('../../dev/app/os');

describe('os', function() {
  describe('type', function() {
    itEventually('should return Unknown for unknown', function() {});
    itEventually('should return Windows for Win', function() {});
    itEventually('should return Mac for Mac', function() {});
    itEventually('should return Linux for Linux', function() {});
  });

  describe('arch', function() {
    itEventually('should return 32 for unknown', function() {});
    itEventually('should return 32 for Mac OS X 10.0.0 - 10.5.x', function() {});
    itEventually('should return 64 for Mac OS X >= 10.6.0', function() {});
    itEventually('should return 64 for Windows 64', function() {});
    itEventually('should return 64 for Linux x86_64', function() {});
  });
});