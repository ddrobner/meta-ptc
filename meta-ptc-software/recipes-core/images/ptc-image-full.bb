SUMMARY = "Custom Replicated PetaLinux Image"
LICENSE = "MIT"

inherit core-image

KERNEL_CLASSES += "kernel-fitimage"
KERNEL_IMAGETYPE = "fitImage"

FITIMAGE_PACK_RAMDISK = "1"
INITRAMFS_IMAGE = "ptc-image-minimal"
INITRAMFS_IMAGE_NAME = "ptc-image-minimal-zynqmp-ptc.rootfs"
CONFIG_BLK_DEV_INITRD = "n"
IMAGE_ROOTFS_EXTRA_SPACE = "0"
IMAGE_OVERHEAD_FACTOR = "1.0"

# -------------------------------------------------------------
# Core & Package Groups
# -------------------------------------------------------------
IMAGE_INSTALL:append = " \
    packagegroup-core-boot \
    packagegroup-core-buildessential \
    packagegroup-core-buildessential-dev \
"

# -------------------------------------------------------------
# AMD / Xilinx Hardware & FPGA Tools
# -------------------------------------------------------------
IMAGE_INSTALL:append = " \
    fpga-manager-script \
    libdfx \
    linux-xlnx-udev-rules \
    udev-extraconf \
    u-boot-tools \
    libubootenv-bin \
"

# -------------------------------------------------------------
# Networking & Connectivity Tools
# -------------------------------------------------------------
IMAGE_INSTALL:append = " \
    bridge-utils \
    can-utils \
    ethtool \
    init-ifupdown \
    iproute2 \
    netcat \
    nfs-utils \
    openssh-sftp-server \
    rsync \
    tcpdump \
"

# -------------------------------------------------------------
# System Utilities & Storage Tools
# -------------------------------------------------------------
IMAGE_INSTALL:append = " \
    acl \
    acl-dev \
    bzip2 \
    dbus \
    dbus-dev \
    dosfstools \
    e2fsprogs-mke2fs \
    file \
    git \
    grep \
    gzip \
    haveged \
    i2c-tools \
    make \
    mtd-utils \
    pciutils \
    python3 \
    run-postinsts \
    sudo \
    tar \
    tcf-agent \
    unzip \
    vim \
    zip \
"

# -------------------------------------------------------------
# Custom Migrated User Applications (from meta-user)
# -------------------------------------------------------------
IMAGE_INSTALL:append = " \
    genskeleton \
    openocd \
    regtest \
    sfp-init \
"
