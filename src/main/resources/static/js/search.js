function interceptFilterForm(form){
    var inputs = form.getElementsByTagName("input");

    for (var i = 0; i < inputs.length; ++i) {
        var input = inputs[i];
        if (input.type == "hidden") {
            input.parentNode.removeChild(input);
        }
    }

    form.submit();
}