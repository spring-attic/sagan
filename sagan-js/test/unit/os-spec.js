var os = require('../../dev/app/os');

// TODO: These are good candidates for generative testing. We
// could consider introducing a generative testing tool

describe('os', function() {

  describe('type', function() {

    it('should return Unknown for unknown', function() {
      var expected = 'Unknown';
      ['foo', 'win', 'mac', 'linux', ''].forEach(function(appVersion) {
        expect(os.type({ appVersion: appVersion })).toBe(expected);
      });
    });

    it('should return Windows for Win', function() {
      var expected = 'Windows';
      expect(os.type({ appVersion: 'abcWinxyz'})).toBe(expected);
      expect(os.type({ appVersion: 'abc Win xyz'})).toBe(expected);
      expect(os.type({ appVersion: 'Winabcxyz'})).toBe(expected);
      expect(os.type({ appVersion: 'Win abcxyz'})).toBe(expected);
      expect(os.type({ appVersion: 'abcxyzWin'})).toBe(expected);
      expect(os.type({ appVersion: 'abcxyz Win'})).toBe(expected);
    });

    it('should return Mac for Mac', function() {
      var expected = 'Mac';
      expect(os.type({ appVersion: 'abcMacxyz'})).toBe(expected);
      expect(os.type({ appVersion: 'abc Mac xyz'})).toBe(expected);
      expect(os.type({ appVersion: 'Macabcxyz'})).toBe(expected);
      expect(os.type({ appVersion: 'Mac abcxyz'})).toBe(expected);
      expect(os.type({ appVersion: 'abcxyzMac'})).toBe(expected);
      expect(os.type({ appVersion: 'abcxyz Mac'})).toBe(expected);

    });

    it('should return Linux for Linux', function() {
      var expected = 'Linux';
      expect(os.type({ appVersion: 'abcLinuxxyz'})).toBe(expected);
      expect(os.type({ appVersion: 'abc Linux xyz'})).toBe(expected);
      expect(os.type({ appVersion: 'Linuxabcxyz'})).toBe(expected);
      expect(os.type({ appVersion: 'Linux abcxyz'})).toBe(expected);
      expect(os.type({ appVersion: 'abcxyzLinux'})).toBe(expected);
      expect(os.type({ appVersion: 'abcxyz Linux'})).toBe(expected);
    });
  });

  describe('arch', function() {

    it('should return 32 for unknown', function() {
      ['foo', 'Win', 'Linux', 'Mac', 'Mac OS', 'Mac OS 10R5'].forEach(function(s) {
        expect(os.arch({ userAgent: s, platform: s })).toBe('32');
      });
    });

    it('should return 32 for Mac OS X 10.0.0 - 10.5.x', function() {
      var expected = '32';
      for(var i=0; i<=5; i++) {
        expect(os.arch({ userAgent: 'Mac OS X 10.' + i, platform: '' })).toBe(expected);
      }

      expect(os.arch({ userAgent: 'Mac OS X 10.0.', platform: '' })).toBe(expected);
      expect(os.arch({ userAgent: 'Mac OS X 10.0 ', platform: '' })).toBe(expected);
      expect(os.arch({ userAgent: 'Mac OS X 10.0.1', platform: '' })).toBe(expected);
      expect(os.arch({ userAgent: 'Mac OS X 10.5.', platform: '' })).toBe(expected);
      expect(os.arch({ userAgent: 'Mac OS X 10.5 ', platform: '' })).toBe(expected);
      expect(os.arch({ userAgent: 'Mac OS X 10.5.99999', platform: '' })).toBe(expected);
    });

    it('should return 64 for Mac OS X >= 10.6.0', function() {
      var expected = '64';
      expect(os.arch({ userAgent: 'Mac OS X 10.6', platform: '' })).toBe(expected);
      expect(os.arch({ userAgent: 'Mac OS X 10.6.', platform: '' })).toBe(expected);
      expect(os.arch({ userAgent: 'Mac OS X 10.6. ', platform: '' })).toBe(expected);
      expect(os.arch({ userAgent: 'Mac OS X 10.6.0', platform: '' })).toBe(expected);
      expect(os.arch({ userAgent: 'Mac OS X 10.6.1', platform: '' })).toBe(expected);
      expect(os.arch({ userAgent: 'Mac OS X 10.10', platform: '' })).toBe(expected);
      expect(os.arch({ userAgent: 'Mac OS X 10.10.0', platform: '' })).toBe(expected);
    });

    it('should return 64 for Win64', function() {
      var expected = '64';
      expect(os.arch({ userAgent: '', platform: 'abcWin64xyz'})).toBe(expected);
      expect(os.arch({ userAgent: '', platform: 'abc Win64 xyz'})).toBe(expected);
      expect(os.arch({ userAgent: '', platform: 'Win64abcxyz'})).toBe(expected);
      expect(os.arch({ userAgent: '', platform: 'Win64 abcxyz'})).toBe(expected);
      expect(os.arch({ userAgent: '', platform: 'abcxyzWin64'})).toBe(expected);
      expect(os.arch({ userAgent: '', platform: 'abcxyz Win64'})).toBe(expected);
    });

    it('should return 64 for Linux x86_64', function() {
      var expected = '64';
      expect(os.arch({ userAgent: '', platform: 'abcLinux x86_64xyz'})).toBe(expected);
      expect(os.arch({ userAgent: '', platform: 'abc Linux x86_64 xyz'})).toBe(expected);
      expect(os.arch({ userAgent: '', platform: 'Linux x86_64abcxyz'})).toBe(expected);
      expect(os.arch({ userAgent: '', platform: 'Linux x86_64 abcxyz'})).toBe(expected);
      expect(os.arch({ userAgent: '', platform: 'abcxyzLinux x86_64'})).toBe(expected);
      expect(os.arch({ userAgent: '', platform: 'abcxyz Linux x86_64'})).toBe(expected);
    });
  });
});