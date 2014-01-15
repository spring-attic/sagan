/**
 * This bootstrap script interacts with a parent document that launches
 * a "View Harness", either as an iframe or as a new browser tab.
 * The view harness must be provided the name of a function to call
 * on the parent window where the AMD/CJS environment has been prepared.
 * The name of the callback function is passed as the contents of
 * the document fragment (hash) of the document url.  (e.g.
 * "my-view-harness.html#callbackFuncName")  The parent then launches and
 * configures a minimal testing environment, including curl.js
 * and wire.js before returning control to this harness.
 */
var curl;
(function () {
	var cbName;
	try {
		// find callback on parent window
		cbName = document.location.hash.substr(1);
		if (!cbName) throw new Error('callback not found in url.');
		if (!window.parent) throw new Error('no parent window found.');
		if (!window.parent[cbName]) throw new Error('no callback found.');

		// call parent, passing a callback
		window.parent[cbName](function () {
			// launch wire.js and initialize the environment
			curl(['wire'], runTests);
		});
	}
	catch (ex) {
		// fail loudly
		console.error(ex);
		throw ex;
	}
}());

