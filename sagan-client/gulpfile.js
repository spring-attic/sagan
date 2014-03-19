var gulpFilter = require('gulp-filter'),
    cram = require('gulp-cram'),
    uglify = require('gulp-uglify'),
    bowerSrc = require('gulp-bower-src'),
    cssmin = require('gulp-minify-css'),
    watch = require('gulp-watch'),
    gulp = require('gulp');

var paths = {
    run: 'src/run.js',
    css: {
        files: ['src/css/main.css', 'src/css/guide.css'],
        root: 'src/css'
    },
    assets: ['src/img*/**','src/*.txt','src/*.html','src/font*/**','src/css*/filterable-list.css'],
    dest: './dist/'
};


// concat and minify CSS files
gulp.task('minify-css', function() {
    return gulp.src(paths.css.files)
        .pipe(cssmin({root:paths.css.root}))
        .pipe(gulp.dest(paths.dest+'css'));
});

// cram and uglify JavaScript source files
gulp.task('build-modules', function() {

    var opts = {
        includes: [ 'curl/loader/legacy', 'curl/loader/cjsm11'],
        excludes: ['leaflet']
    };

    return cram(paths.run, opts).into('run.js')
        .pipe(uglify())
        .pipe(gulp.dest(paths.dest));
});

// copy main bower files (see bower.json) and optimize js
gulp.task('bower-files', function() {
    var filter = gulpFilter(["**/*.js", "!**/*.min.js"]);
    return bowerSrc()
        .pipe(filter)
        .pipe(uglify())
        .pipe(filter.restore())
        .pipe(gulp.dest(paths.dest+'lib'));
})

// copy assets
gulp.task('copy-assets', function() {
    return gulp.src(paths.assets)
        .pipe(gulp.dest(paths.dest));
})

gulp.task('build', ['minify-css', 'build-modules', 'copy-assets', 'bower-files'], function(){ });

// temp tasks, until live reloading is configured
gulp.task('bower-gradle', function() {
    return bowerSrc()
        .pipe(gulp.dest('../sagan-site/build/resources/main/static/'+'lib'));
})

gulp.task('watch', ['bower-gradle'], function(){
    return gulp.src(['src/**', '!src/lib/**'])
        .pipe(watch())
        .pipe(gulp.dest('../sagan-site/build/resources/main/static/'))
});
