SUMMARY = "Custom Replicated PetaLinux Image"
LICENSE = "MIT"

inherit core-image

KERNEL_CLASSES += "kernel-fitimage"
KERNEL_IMAGETYPE = "fitImage"

DEPLOY_DIR_IMAGE = "${DEPLOY_DIR}/images/${MACHINE}/full"
BASE_DEPLOY_DIR_IMAGE = "${DEPLOY_DIR}/images/${MACHINE}"

FITIMAGE_PACK_RAMDISK = "1"
INITRAMFS_IMAGE = "ptc-image-full"
INITRAMFS_IMAGE_NAME = "ptc-image-full-zynqmp-ptc.rootfs"
CONFIG_BLK_DEV_INITRD = "n"
IMAGE_ROOTFS_EXTRA_SPACE = "0"
IMAGE_OVERHEAD_FACTOR = "1.0"

IMAGE_INSTALL:remove = "gstreamer-vcu-examples libvcu-omxil"

IMAGE_INSTALL:append = " \
    packagegroup-core-boot \
    packagegroup-core-buildessential \
"

IMAGE_INSTALL:append = " \
    fpga-manager-script \
    libdfx \
    linux-xlnx-udev-rules \
    udev-extraconf \
    u-boot-tools \
    libubootenv-bin \
"

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

IMAGE_INSTALL:append = " \
    acl \
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

IMAGE_INSTALL:append = " \
    genskeleton \
    openocd \
    sfp-init \
    peekpoke \
    ptc-scripts \
"
