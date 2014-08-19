module.exports = function(storage) {

    if(storage == null) {
        storage = window.localStorage;
    }

    return {
        getItem: function (sKey) {
            return storage.getItem(sKey);
        },
        setItem: function (sKey, sValue) {
            storage.setItem(sKey, sValue);
        },
        removeItem: function (sKey) {
            storage.removeItem(sKey);
        },
        hasItem: function (sKey) {
            return storage.getItem(sKey) != null;
        },
        key: function (idx) {
            return storage.key(idx);
        },
        clear: function () {
            storage.clear();
        }
    };
};