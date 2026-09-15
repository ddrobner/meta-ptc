SUMMARY = "PTC Production Minimal Image"
require recipes-core/images/core-image-minimal.bb
DEPENDS += "u-boot-tools-native virtual/kernel"

DEPLOY_DIR_IMAGE = "${DEPLOY_DIR}/images/${MACHINE}/minimal"

# Keep a reference to the default directory to fetch the shared Kernel/DTB
BASE_DEPLOY_DIR_IMAGE = "${DEPLOY_DIR}/images/${MACHINE}"

# Stripping rules
IMAGE_FEATURES = "read-only-rootfs ssh-server-dropbear empty-root-password allow-empty-password allow-root-login debug-tweaks"
IMAGE_LINGUAS = ""
USE_NLS = "no"
NO_RECOMMENDATIONS = "1"
IMAGE_FSTYPES = "cpio.gz"

KERNEL_CLASSES += "kernel-fitimage"
KERNEL_IMAGETYPE = "fitImage"

FITIMAGE_PACK_RAMDISK = "1"
INITRAMFS_IMAGE = "ptc-image-minimal"
INITRAMFS_IMAGE_NAME = "ptc-image-minimal-zynqmp-ptc.rootfs"
CONFIG_BLK_DEV_INITRD = "n"
IMAGE_ROOTFS_EXTRA_SPACE = "0"
IMAGE_OVERHEAD_FACTOR = "1.0"

# Add only runtime dependencies
IMAGE_INSTALL:append = " \
    mtd-utils \
    sfp-init \
    genskeleton \
    devmem2 \
    ptc-scripts \
    peekpoke \
"

# Direct systemd to act as /init
ROOTFS_POSTPROCESS_COMMAND += "link_systemd_init; "
link_systemd_init() {
    ln -sf /lib/systemd/systemd ${IMAGE_ROOTFS}/init
}

IMAGE_POSTPROCESS_COMMAND += "build_fitimage_ub; "

build_fitimage_ub() {
    # Check inputs using BASE_DEPLOY_DIR_IMAGE
    if [ ! -f "${BASE_DEPLOY_DIR_IMAGE}/Image" ] || [ ! -f "${BASE_DEPLOY_DIR_IMAGE}/system.dtb" ]; then
        bbwarn "Kernel Image or system.dtb missing in BASE_DEPLOY_DIR_IMAGE, skipping fitImage generation."
        return 0
    fi

    # Create ITS source file inside the image working dir
    cat << EOF > ${WORKDIR}/fit-image.its
/dts-v1/;

/ {
    description = "U-Boot FIT Image for ${PN}";
    #address-cells = <1>;

    images {
        kernel-1 {
            description = "Linux Kernel";
            data = /incbin/("${BASE_DEPLOY_DIR_IMAGE}/Image");
            type = "kernel";
            arch = "arm64";
            os = "linux";
            compression = "none";
            load = <0x200000>;
            entry = <0x200000>;
            hash-1 {
                algo = "sha256";
            };
        };
        fdt-1 {
            description = "Device Tree";
            data = /incbin/("${BASE_DEPLOY_DIR_IMAGE}/system.dtb");
            type = "flat_dt";
            arch = "arm64";
            compression = "none";
            hash-1 {
                algo = "sha256";
            };
        };
        ramdisk-1 {
            description = "Initramfs Rootfs";
            data = /incbin/("${IMGDEPLOYDIR}/${PN}-${MACHINE}.rootfs.cpio.gz");
            type = "ramdisk";
            arch = "arm64";
            os = "linux";
            compression = "gzip";
            hash-1 {
                algo = "sha256";
            };
        };
    };

    configurations {
        default = "config-1";
        config-1 {
            description = "Standard Boot";
            kernel = "kernel-1";
            fdt = "fdt-1";
            ramdisk = "ramdisk-1";
        };
    };
};
EOF

    # Assemble fitImage using native mkimage tool directly into IMGDEPLOYDIR
    mkimage -f ${WORKDIR}/fit-image.its ${IMGDEPLOYDIR}/image-${PN}.ub
    
    # Create convenience symlink image.ub
    ln -sf image-${PN}.ub ${IMGDEPLOYDIR}/image.ub
}