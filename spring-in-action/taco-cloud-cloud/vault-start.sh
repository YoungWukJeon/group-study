#!bin/bash

docker pull vault
docker run --name vault -d --cap-add=IPC_LOCK -p 8200:8200 -e 'VAULT_DEV_ROOT_TOKEN_ID=roottoken'
#docker start vault
#docker run vault status
#docker run vault secrets disable secret
#docker run vault secrets enable --path=secret kv
