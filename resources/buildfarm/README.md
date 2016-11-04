# Build Farm Ansible Playbook

This Playbook will install development environments for Ubuntu, macOS, and Windows, with prerequisites for compiling Aseba.The `hosts` file contains an inventory of the machines on which the toolchains should be installed.

## Invocation
The playbook can be invoked using
```
ansible-playbook -i hosts -s site.yml
```
Alternatively, any OS-specific playbook may be invoked separately.

## Preparing nodes
Ubuntu and macOS nodes must allow *ssh* access and the user must be authorized to use *sudo*. macOS nodes must already have Xcode Command Line Tools installed.

Windows nodes must have Powershell >= 3.0 and must allow *Powershell Remoting*. The appropriate user and connection parameters must be set in `group_vars/ci-windows.yml`. 