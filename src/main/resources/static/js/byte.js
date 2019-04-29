/**
 # byte.js
 byte('5kb')     // 5120
 byte('10mb')    // 104857600
 byte('512b')    // 512
 byte('1024')    // 1024
 byte(1024)      // 1024
 byte('3gb')     // 3221225472
 **/

(function (g) {
    var r = /(\d*.?\d+)([kmgtb]+)/
        , _ = {};

    _.b = 1;
    _.kb = _.b * 1024;
    _.mb = _.kb * 1024;
    _.gb = _.mb * 1024;
    _.tb = _.gb * 1024;

    function byte (s) {
        if (s === Number(s)) return Number(s);
        r.exec(s.toLowerCase());
        return RegExp.$1 * _[RegExp.$2];
    }

    g.top ? g.byte = byte : module.exports = byte;
})(this);