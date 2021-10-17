# LocalStack Setup

## Process

1. Follow the instructions in ["Get Started"](https://docs.localstack.cloud/get-started/) for
   LocalStack.
2. If running on ARM64, follow
   the ["ARM64 (Including Apple M1 chip)" workaround.](https://docs.localstack.cloud/localstack/limitations/#arm64-including-apple-m1-chip)
   The shell script `bin/arm64-localstack-setup.sh` contains the same code provided in the linked
   workaround.

## Troubleshooting

`zsh: killed ...`

1. Uninstall Homebrew:

```/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/uninstall.sh)"```

2. Reinstall Homebrew:

```
cd /opt && \
mkdir homebrew && \
curl -L https://github.com/Homebrew/brew/tarball/master | tar xz --strip 1 -C homebrew
```

Reference: [StackOverflow](https://stackoverflow.com/a/66432398)