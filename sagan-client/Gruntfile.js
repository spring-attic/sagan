module.exports = function (grunt) {

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        bower: {
            install: {}
        },
        cssmin: {
            combine: {
                files: {
                    'build/resources/main/static/css/main.css': 'src/css/main.css'
                }
            }
        },
        uglify: {
            main: {
                files: {
                    'build/resources/main/static/run.js': ['build/tmp/static/run.js'],
                    'build/resources/main/static/lib/curl/src/curl.js': ['src/lib/curl/src/curl.js']
                }
            }
        },
        copy: {
            build: {
                files:[
                    {expand: true, cwd: 'src/', src: ['img/**', 'css/**', 'font-custom/**'],
                        dest: '../sagan-site/build/resources/main/static/'
                    },
                    // TODO: should we copy resources from lib as well?
                    {expand: true, cwd: 'src/', src: ['lib/leaflet/dist/**', 'lib/bootstrap/docs/assets/**',
                        'lib/font-awesome/font/**', 'lib/zeroclipboard/**.swf', 'lib/google-code-prettify/src/**'],
                        dest: '../sagan-site/build/resources/main/static/'
                    },
                    {expand: true, cwd: 'src', src: '*', dest: '../sagan-site/build/resources/static/', filter:'isFile'},
                    {expand: true, cwd: 'build/resources/main/static/', src: '**', dest: '../sagan-site/build/resources/main/static/'}

                ]
            },
            dump: {
                files:[
                    {expand: true, cwd: 'src/', src: ['img/**', 'css/**', 'font-custom/**'],
                        dest: '../sagan-site/src/main/resources/static/'
                    },
                    // TODO: should we copy resources from lib as well?
                    {expand: true, cwd: 'src/', src: ['lib/leaflet/dist/**', 'lib/bootstrap/docs/assets/**',
                        'lib/font-awesome/font/**', 'lib/zeroclipboard/**.swf', 'lib/google-code-prettify/src/**'],
                        dest: '../sagan-site/src/main/resources/static/'
                    },
                    {expand: true, cwd: 'src/', src: '*', dest: '../sagan-site/src/main/resources/static/', filter:'isFile'},
                    {expand: true, cwd: 'build/resources/main/static/', src: '**', dest: '../sagan-site/src/main/resources/static/'}
                ]
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-bower-task');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-copy');

    grunt.registerTask('optimize', ['cssmin','uglify','copy:build']);
    grunt.registerTask('dump', ['optimize','copy:dump']);
    grunt.registerTask('default', ['bower:install']);
};
