var forEach = Function.prototype.call.bind(Array.prototype.forEach);

module.exports = function(e){
  forEach(e.target.elements, function(input) {
    if (input.type == 'hidden' && input.name == '_filters') {
      input.disabled = true;
    }
  });
};