// TODO: Find a way to run these tests on many/every page?
// Since search is available on every page, we may eventually want
// to run these tests on every page.  For now, we're just running
// them on /

describe('=>Search', function() {

  var shownClass = 'js-show';

  it('should be hidden initially', function() {
    return this.browser.path('/')
        .elementByCss('.search-dropdown--container')
          .getAttribute('class')
          .then(function(className) {
            expect(className).not.toContain(shownClass);
          });
  });

  describe('when opened by clicking search nav', function() {

    it('should allow the user to perform a search', function() {
      var searchTerm = 'rest';

      return this.browser.path('/')
          // Extend the wait time for finding elements
          // Firefox seems to need this
          .setImplicitWaitTimeout(5000)

          // Find the open search trigger element and click it
          .waitForElementByCss('.js-search-input-open')
          .elementByCss('.js-search-input-open')
            .click()

          // Find the search dropdown and make sure it has
          // the class that makes it visible.  We can't rely on
          // .isVisible for this, as the heuristics it uses will
          // not work for this situation.
          .elementByCss('.search-dropdown--container')
            .getAttribute('class')
            .then(function(className) {
              expect(className).toContain(shownClass);
            })

          // Type a search term into the search input and submit
          // the search form
          .active()
            .type(searchTerm)
          .elementByCss('.search-dropdown--container form')
            .submit()

          // After submit, ensure that eventually, there are search results
          .waitForVisibleByCss('.search-results')
          .elementByCss('.search-results')
            .isVisible()
            .then(function(visible) {
              expect(visible).toBeTrue();
            })

          // Ensure that the search is shown by default on the results page
          .elementByCss('.search-dropdown--container')
            .getAttribute('class')
            .then(function(className) {
              expect(className).toContain(shownClass);
            })

          // Ensure that it is focused (active), and has been prefilled
          .active()
            .getValue()
            .then(function(value) {
              expect(value).toBe(searchTerm);
            });
    });
  });
});
