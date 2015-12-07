#!/usr/bin/env bash

echo "
    - hosts: cmd2rdf
      sudo: yes
      roles:
        - common_handlers
        - samples
" > provisioning/tmp.yml

ansible-playbook provisioning/tmp.yml -vvv -u vagrant -i provisioning/hosts --private-key .vagrant/machines/cmd2rdf/virtualbox/private_key

rm provisioning/tmp.yml
