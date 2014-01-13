describe('Main page', function () {

    describe('the title', function () {

        it('should be correct', function () {
            return this.browser.path('/')
                .setImplicitWaitTimeout(5000)
                .title().then(function (title) {
                    expect(title).toBe('Spring');
                });
        });

    });

});
