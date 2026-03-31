data "proxmox_virtual_environment_node" "pve_node" {
  node_name = "pve"
}

locals {
  vms = {
    "prod" = {
      id          = 101
      cores       = 4
      memory      = 8192
      disk_size   = 80
      description = "Production: Spring + Bot + DB"
    },
    "dev" = {
      id          = 102
      cores       = 2
      memory      = 2048
      disk_size   = 40
      description = "Development / Staging"
    },
    "cicd" = {
      id          = 103
      cores       = 2
      memory      = 4096
      disk_size   = 30
      description = "CI/CD: Gitea + Runner"
    }
  }
}

resource "proxmox_virtual_environment_vm" "server" {
  for_each = local.vms

  name        = "hospital-${each.key}"
  description = each.value.description
  tags        = ["hospital", "terraform", each.key]

  node_name = data.proxmox_virtual_environment_node.pve_node.id
  vm_id     = each.value.id

  agent {
    enabled = true
  }

  cpu {
    cores = each.value.cores
    type  = "host"
  }

  memory {
    dedicated = each.value.memory
  }

  clone {
    vm_id = var.template_id # Используем переменную
    full  = true
  }

  disk {
    datastore_id = "local-lvm"
    interface    = "scsi0"
    size         = each.value.disk_size
  }

  network_device {
    bridge = "vmbr0"
    model  = "virtio"
  }

  initialization {
    ip_config {
      ipv4 {
        address = "dhcp"
      }
    }

    user_account {
      username = "root"
      password = var.vm_password
      keys     = [var.ssh_public_key]
    }
  }

  on_boot = true
}