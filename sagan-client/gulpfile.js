var cssmin = require('gulp-minify-css'),
  gulp = require('gulp');

var paths = {
  css: {
    files: ['src/css/*.css'],
    root: 'src/css'
  },
  external_css: [
    'jspm_packages/npm/eonasdan-bootstrap-datetimepicker@4.15.35/build/css/bootstrap-datetimepicker.min.css',
    'node_modules/twitter-bootstrap/docs/assets/css/bootstrap.css',
    'node_modules/font-awesome/css/font-awesome.min.css'
  ],
  assets: ['src/img*/**', 'src/*.txt', 'src/*.html', 'src/font*/**', 'src/css*/filterable-list.css'],
  dest: './dist/'
};

// concat and minify CSS files
gulp.task('minify-css', function() {
  return gulp.src(paths.css.files)
    .pipe(cssmin({root: paths.css.root}))
    .pipe(gulp.dest(paths.dest + 'css'));
});

// copy external css
gulp.task('copy-external-css', function() {
  return gulp.src(paths.external_css)
    .pipe(gulp.dest(paths.dest + 'lib/css'));
});

// copy assets
gulp.task('copy-assets', function() {
  return gulp.src(paths.assets)
    .pipe(gulp.dest(paths.dest));
});

gulp.task('build', ['minify-css', 'copy-external-css', 'copy-assets'], function() { });