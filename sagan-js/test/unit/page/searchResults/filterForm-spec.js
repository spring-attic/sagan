var filterForm = require('../../../../dev/feature/searchFacets/filterForm');

describe('filterForm', function () {
    it('should disable hidden _filters inputs', function () {
        var inputs = [
            { type: 'hidden', name: '_filters' },
            { type: 'hidden', name: '_filters' }
        ];

        filterForm(makeSubmitEvent(inputs));

        verifyDisabled(inputs);
    });

    it('should not disable non-matching inputs', function () {
        var inputs = [
            { type: 'hidden', name: 'filters' },
            { type: 'hidden', name: '_filters' },
            { type: 'text', name: '_filters' }
        ];

        filterForm(makeSubmitEvent(inputs));

        verifyDisabled(inputs, 1);
    });
});

function makeSubmitEvent(inputs) {
    return { target: { elements: inputs } };
}

function verifyDisabled(inputs, expectedNum) {
    if (typeof expectedNum !== 'number') {
        expectedNum = inputs.length;
    }

    inputs = inputs.filter(function (input) {
        return input.disabled;
    });

    expect(inputs.length).toBe(expectedNum);
}