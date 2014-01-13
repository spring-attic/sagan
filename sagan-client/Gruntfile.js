module.exports = function (grunt) {

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        cssmin: {
            combine: {
                files: {
//                    '../sagan-site/build/resources/static/css/main.css': '../sagan-site/src/main/resources/static/css/main.css'
                    'build/resources/css/main.css': '../sagan-site/src/main/resources/static/css/main.css'
                }
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.registerTask('default', ['cssmin']);
};
