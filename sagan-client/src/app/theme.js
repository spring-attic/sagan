import {get, set} from 'local-storage';

if (!get('theme')) {
    const isDark =
        window.matchMedia &&
        window.matchMedia('(prefers-color-scheme: dark)').matches;
    set('theme', isDark ? 'dark' : 'light');
}

document.body.className = `${document.body.className} ${get('theme')}`;