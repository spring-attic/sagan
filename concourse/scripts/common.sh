#!/bin/bash
if [[ -d $PWD/gradle && ! -d $HOME/.gradle ]]; then
  ln -s "$PWD/gradle" "$HOME/.gradle"
fi