/**
 * A couple of version simple buster describe/expect-style
 * tests, one sync, one async.  Async tests may also return
 * a promise that fulfills to indicate success, or rejects
 * to indicate failure.  When using promises, DO NOT accept
 * the `done` parameter to the it() callback.
 */

require('../buster-spec-expose');

describe('example test', function() {
	describe('when expectations are met', function() {
		it('passes', function() {
			expect(true).toBeTrue();
		});
	});

	describe('when test is async', function() {
		describe('and expectations are met', function() {
			it('passes using done()', function(done) {
				setTimeout(function() {
					expect(true).toBeTrue();
					done();
				});
			});

			// Example of using a promise instead of done()
			// it('passes returning a promise', function(/* DO NOT use done */) {
				// Do some testing
				// and eventually:
				// return promise;
			// });
		});
	});
});
