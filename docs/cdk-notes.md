## Issue: EACCES Error

- Message: `Error: EACCES: permission denied, rename '/usr/local/bin/cdk'`
- Cause: `cdk ls`
- Solution ([reference](https://stackoverflow.com/a/27265264)):

```
sudo chown -R `whoami` ~/.npm
sudo chown -R `whoami` /usr/local/lib/node_modules
sudo chown -R `whoami` /usr/local/bin
```
