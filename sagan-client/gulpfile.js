var gulpFilter = require('gulp-filter'),
    cram = require('gulp-cram'),
    uglify = require('gulp-uglify'),
    bowerSrc = require('gulp-bower-src'),
    sourcemaps = require('gulp-sourcemaps'),
    cssmin = require('gulp-minify-css'),
    gulp = require('gulp');

var paths = {
    run: 'src/run.js',
    css: {
        files: ['src/css/*.css'],
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
        excludes: ['gmaps']
    };

    return cram(paths.run, opts).into('run.js')
        .pipe(sourcemaps.init())
        .pipe(uglify())
        .pipe(sourcemaps.write("./"))
        .pipe(gulp.dest(paths.dest));
});

// copy main bower files (see bower.json) and optimize js
gulp.task('bower-files', function() {
    var filter = gulpFilter(["**/*.js", "!**/*.min.js"]);
    return bowerSrc()
        .pipe(sourcemaps.init())
        .pipe(filter)
        .pipe(uglify())
        .pipe(filter.restore())
        .pipe(sourcemaps.write("./"))
        .pipe(gulp.dest(paths.dest+'lib'));
})

// copy assets
gulp.task('copy-assets', function() {
    return gulp.src(paths.assets)
        .pipe(gulp.dest(paths.dest));
})

gulp.task('build', ['minify-css', 'build-modules', 'copy-assets', 'bower-files'], function(){ });