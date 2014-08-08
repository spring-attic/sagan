exports.type = type;
exports.arch = arch;

var defaultOs = 'Unknown';
var defaultArch = '32';

/**
 * Detects the Operating System of the user's platform by inspecting
 * platform.appVersion
 * @param {{appVersion:string}?} environment optional environment, defaults to global `navigator`
 * @returns {string} 'Windows', 'Mac', 'Linux', or 'Unknown'
 */
function type(environment) {
  /*global navigator*/
  if(arguments.length === 0) {
    environment = navigator;
  }

  if (environment.appVersion.indexOf('Win') !== -1) {
    return 'Windows';
  }

  if (environment.appVersion.indexOf('Mac') !== -1) {
    return 'Mac';
  }

  if (environment.appVersion.indexOf('Linux') !== -1) {
    return 'Linux';
  }

  return defaultOs;
}

/**
 * Detects the X-bit architecture of the user's platform by inspecting
 * platform.userAgent and platform.platform
 * @param {{userAgent:string, platform:string}?} environment optional environment,
 *   defaults to global `navigator`
 * @returns {string} '32' or '64'
 */
function arch(environment) {
  /*global navigator*/
  /*jshint maxcomplexity:6*/
  if(arguments.length === 0) {
    environment = navigator;
  }

  if (/Mac OS X 10\.[0-5]([\.\s]|$)/.test(environment.userAgent)) {
    return '32';
  }

  if (environment.userAgent.indexOf('Mac OS X') !== -1
      || environment.userAgent.indexOf('WOW64') !== -1
      || environment.platform.indexOf('Win64') !== -1
      || environment.platform.indexOf('Linux x86_64') !== -1) {
    return '64';
  }

  return defaultArch;
}
