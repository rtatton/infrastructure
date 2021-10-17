#!/usr/bin/env bash

installHomebrew() {
  /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
}

# Reference: https://docs.localstack.cloud/localstack/limitations/#arm64-including-apple-m1-chip
setup() {

  installHomebrew
  brew install java11 jenv pyenv

  jenv add /Library/Java/JavaVirtualMachines/openjdk-11.jdk/Contents/Home/
  jenv global 11
  export PATH="$HOME/.jenv/bin:$PATH"
  eval "$(jenv init -)"

  pyenv install 3.8.10
  pyenv global 3.8.10
  export PATH="$HOME/Library/Python/3.8/bin:$PATH"
  python3 -m pip install localstack

  cd localstack || exit
  make install
  make start
}

# Reference: https://stackoverflow.com/a/3232082
read -r -p "Is Docker running? [y/N] " response
if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
  setup
else
  exit
fi
