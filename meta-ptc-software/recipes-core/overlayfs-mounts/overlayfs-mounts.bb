SUMMARY = "OverlayFS mount units for persistent storage (SD card or tmpfs fallback)"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# The overlayfs class generates systemd .mount units for /etc, /home, /root
# backed by the persist mount point (/mnt/persist from SD card or tmpfs fallback).
# This must be a proper recipe (not IMAGE_CLASSES) because do_install is noexec
# in image recipes — the overlayfs class relies on do_install to deploy the units.
inherit allarch overlayfs

OVERLAYFS_MOUNT_POINT[persist] = "/mnt/persist"
OVERLAYFS_WRITABLE_PATHS[persist] = "/etc /home /root"