/*global describe:true, it:true, expect: true */
var copyButtonProvider = require('../../../../dev/page/guide/copyButtonProvider');
var fauxElement = require('../../../lib/fauxElement');

describe('copyButtonProvider', function() {

    var host = fauxElement(),
        proto = fauxElement(),
        clone = fauxElement();

    before(function () {
        host.parentNode = fauxElement();
        host.parentNode.insertBefore = this.spy();
        proto.cloneNode = this.spy(function() { return clone; });
    });

    it('should create a function', function() {
        expect(typeof copyButtonProvider()).toBe('function');
    });

    it('should insert a copy of the button before the host element', function() {
        var provider = copyButtonProvider(proto);
        provider(host);
        expect(host.parentNode.insertBefore).toHaveBeenCalledOnceWith(clone, host);
        expect(proto.cloneNode).toHaveBeenCalledOnce();
    });

});
