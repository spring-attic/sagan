var $ = require('jquery');

module.exports = Controller;

function Controller(root, showHideClass) {
  this.root = root;
  this._input = $('input', root);
  this._showHideClass = showHideClass;
  this._isShown = $(root).hasClass(showHideClass);
}

Controller.prototype.setValue = function(value) {
  this._input.attr('value', value);
}

Controller.prototype.isShown = function() {
  return this._isShown;
};

Controller.prototype.hide = function() {
  this._hide();
};

Controller.prototype.show = function(additionalClass) {
  var cls = this._showHideClass;
  if(additionalClass) {
    cls += ' ' + additionalClass;
  }

  this._hide = function() {
    $(this.root).removeClass(cls);
    this._input.blur();
    this._isShown = false;
    this._hide = noop;
  }

  $(this.root).addClass(cls);
  this._input.focus();
  this._isShown = true;
};

Controller.prototype._hide = noop;

function noop() {}