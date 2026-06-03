# Переменные
VAULT_PASS_PATH = ~/.hospital_vault_pass
INVENTORY = ansible/inventory.ini

.PHONY: help vault-edit deploy-prod

help:
	@echo "Usage:"
	@echo "  make vault-edit   - Edit encrypted secrets"
	@echo "  make deploy-prod  - Run Ansible playbook for Production (VM 101)"

vault-edit:
	ansible-vault edit ansible/group_vars/all/vault.yml --vault-password-file $(VAULT_PASS_PATH)

deploy-prod:
	ansible-playbook -i $(INVENTORY) ansible/deploy.yml --vault-password-file $(VAULT_PASS_PATH)
