require('../buster-spec-expose');

describe('the title', function() {

  it('should be correct', function(done) {
    this.browser.path('/')
        .getTitle(function(e, title) {
          expect(title).toBe('Spring');
        })
        .call(done);
  });

});