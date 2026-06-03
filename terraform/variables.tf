variable "proxmox_url" {
  description = "API endpoint for Proxmox (e.g., https://10.6.1.25:8006/api2/json)"
  type        = string
}

variable "proxmox_user" {
  description = "Username for Proxmox API (e.g., root@pam)"
  type        = string
}

variable "proxmox_password" {
  description = "Password for the Proxmox user"
  type        = string
  sensitive   = true
}

variable "ssh_public_key" {
  description = "Public SSH key for the admin user on the VMs"
  type        = string
}

variable "template_id" {
  description = "The ID of the Ubuntu Cloud-Init template (e.g., 104)"
  type        = number
  default     = 104
}

variable "vm_password" {
  description = "Hashed password for the VM 'admin' user (SHA-512 format)"
  type        = string
  sensitive   = true
}