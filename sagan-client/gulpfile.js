var combine = require('gulp-util').combine,
    gulpFilter = require('gulp-filter'),
    uglify = require('gulp-uglify'),
    imagemin = require('gulp-imagemin'),
    bower = require('gulp-bower'),
    bowerSrc = require('gulp-bower-src'),
    cssmin = require('gulp-minify-css'),
    watch = require('gulp-watch'),
    changed = require('gulp-changed'),
    gulp = require('gulp'),
    es = require('event-stream');

var paths = {
    scripts: ['build/tmp/static/run.js'],
    css: {
        files: ['src/css/main.css'],
        root: 'src/css'
    },
    images: ['src/img/**/*'],
    assets: ['src/*.txt','src/*.html','src/font*/**','src/css*/filterable-list.css'],
    dest: './dist/'
};


// install bower dependencies listed in "bower.json" in ./src/lib
gulp.task('bower-install', function(){
    return bower().pipe(gulp.dest('./src/lib'));
});

// concat and minify CSS files
gulp.task('minify-css', function() {
    return gulp.src(paths.css.files)
        .pipe(cssmin({root:paths.css.root}))
        .pipe(gulp.dest(paths.dest+'css'));
});

// uglify JavaScript source files
gulp.task('minify-scripts', function() {
    return gulp.src(paths.scripts)
        .pipe(uglify())
        .pipe(gulp.dest(paths.dest));
});

// optimize images
gulp.task('optim-img', function() {
    return gulp.src(paths.images)
        .pipe(imagemin())
        .pipe(gulp.dest(paths.dest+'img'));
});

// copy main bower files (see bower.json) and optimize js
gulp.task('bower-files', function() {
    var filter = gulpFilter(["**/*.js", "!**/*.min/js"]);
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

gulp.task('default', ['bower-install'], function(){ });

gulp.task('build', ['bower-install', 'minify-css', 'minify-scripts', 'optim-img', 'copy-assets', 'bower-files'], function(){ });

// temp tasks, until live reloading is in configured
gulp.task('bower-gradle', function() {
    return bowerSrc()
        .pipe(gulp.dest('../sagan-site/build/resources/main/static/'+'lib'));
})

gulp.task('watch', ['bower-install', 'bower-gradle'], function(){
    return gulp.src(['src/**', '!src/lib/**'])
        .pipe(watch())
        .pipe(gulp.dest('../sagan-site/build/resources/main/static/'))
});
