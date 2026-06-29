# Hospital Infrastructure Project

## Overview
Automated deployment of a clinic's appointment system with infrastructure as code.

## Project Structure
- **terraform/** – Proxmox IaC (VMs, networking)
- **ansible/** – Configuration management (Docker, Nginx, Gitea)
- **docker/** – Container definitions (PostgreSQL, Redis)
- **app/** – Spring Boot backend (Java 17)
- **bot/** – Telegram notification bot
- **cicd/** – Gitea + runner compose file

## Infrastructure Details
- **Hypervisor**: Proxmox VE 8
- **Base OS**: Ubuntu 22.04 LTS
- **Orchestration**: Docker Compose
- **CI/CD**: Gitea Actions with self-hosted runner

## Deployment Pipeline
1. Terraform provisions VMs on Proxmox.
2. Ansible configures Docker, Nginx, and Gitea.
3. Gitea Actions builds Docker images and pushes to local registry.
4. Ansible deploy playbook pulls and restarts containers.

## Secrets Management
Sensitive variables (DB passwords, bot tokens) are stored in Ansible Vault (`ansible/group_vars/all/vault.yml`).  
Use `ansible-vault encrypt/decrypt` to manage them.

---
