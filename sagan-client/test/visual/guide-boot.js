/**
 * Initialize environment and run tests.
 * @param wire {Function} wire.js's wiring function
 * @description
 * This is the function that is run when the javascript environment is
 * ready.  Note that wire.js handles domReady internally so there's no
 * reason to specify it directly.  You should load and wire a spec here
 * to initialize the harness.  You can put unit tests and/or functional
 * tests in here, too!
 */
function runTests (wire) {

	wire({

		/* Get a reference to the root of our DOM environment. */
		testNode: { $ref: 'dom!test', at: document.body },

		/* Load the wire plugins we're using. */
		plugins: [
			{ module: 'wire/debug' },
			{ module: 'wire/dom' },
			{ module: 'wire/dom/render' }
		]
	});

}
