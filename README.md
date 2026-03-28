# Hospital Infrastructure Project

## Overview
This repository contains the infrastructure code and application components for a clinic's automated system. The project focuses on reliability and automated tracking of medical devices integrated with national systems.

## Project Structure
- **terraform/**: Infrastructure as Code for Proxmox.
- **ansible/**: Server configuration and software deployment.
- **docker/**: Containerization logic.
- **app/**: Main application backend (Python/Flask).
- **bot/**: Notification and management bot.
- **docs/**: Technical documentation and graduation project materials.

## Infrastructure Details
- **Hypervisor**: Proxmox VE
- **Base Image**: Ubuntu 22.04 LTS (Cloud Image)
- **Template ID**: 9000 (Pre-configured with QEMU Guest Agent)