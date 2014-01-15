/**
 * Creates a function that associates one or more HTML elements with
 * clipboard buttons.  If an element has an ID, it will be associated
 * dynamically.  Otherwise, a static copy of the element's text (its
 * value or textContent property) will be set on the clipboard button
 * immediately.
 * @module
 * @function
 * @param {function(HTMLElement): HTMLElement} provideButton
 *     creates a button HTML element.
 * @returns {function((Array.<HTMLElement>|NodeList)) : Array}
 */
module.exports = function (provideButton) {
    return function createClipboardElements (elements) {
        var el, button, text, buttons = [];
        for (var i = 0, len = elements.length; i < len; i++) {
            el = elements[i];
            button = provideButton(el);
            if (el.id) {
                button.setAttribute('data-clipboard-target', el.id);
            }
            else {
                text = el.value || el.textContent || el.innerText;
                button.setAttribute('data-clipboard-text', text);
            }
            buttons.push(button);
        }
        return buttons;
    };
};
