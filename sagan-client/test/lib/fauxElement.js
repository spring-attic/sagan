module.exports = fauxElement;

function fauxElement (type, props, attrs) {
    var el;
    el = {
        id: '',
        type: type || 'div',
        attributes: {},
        setAttribute: function (name, val) {
            el.attributes[name] = String(val);
        },
        getAttribute: function (name) {
            return name in el.attributes ? el.attributes[name] : null;
        }
    };
    for (var p in props) el[p] = props[p];
    for (var a in attrs) el.setAttribute(a, attrs[a]);
    return el;
}
