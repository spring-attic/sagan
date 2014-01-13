/*global describe:true, it:true, expect: true */
var clipboardProvider = require('../../../../src/feature/clipboardButtons/buttonConnector');
var fauxElement = require('../../../lib/fauxElement');

describe('buttonConnector', function() {

    var testText = 'some text',
        testValue = 'some value',
        testId = 'myId',
        elements = [
        fauxElement('div', { textContent: testText }),
        fauxElement('input', { value: testValue }),
        fauxElement('span', { id: testId, innerText: testText })
    ];

    it('should create a function', function() {
        expect(typeof clipboardProvider()).toBe('function');
    });

    it('should create a button for each provided element', function() {
        var buttons = clipboardProvider(stubProvider)(elements);
        expect(buttons.length).toBe(elements.length);
    });

    it('should copy text from the provided element', function() {
        var buttons = clipboardProvider(stubProvider)(elements);
        expect(buttons[0].getAttribute('data-clipboard-text'))
            .toBe(testText);
        expect(buttons[1].getAttribute('data-clipboard-text'))
            .toBe(testValue);
    });

    it('should connect a button to the provided element', function() {
        var buttons = clipboardProvider(stubProvider)(elements);
        expect(buttons[2].getAttribute('data-clipboard-target'))
            .toBe(testId);
    });

});

function stubProvider (el) {
    return fauxElement();
}
