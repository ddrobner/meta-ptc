SUMMARY = "PTC Production Ultra-Minimal BusyBox Image"

# Inherit the base image class directly to avoid systemd/glibc extra bloat
inherit core-image

DEPENDS += "u-boot-tools-native virtual/kernel"

# Stripping and size reduction rules
IMAGE_FEATURES = "read-only-rootfs empty-root-password allow-empty-password"
IMAGE_LINGUAS = ""
USE_NLS = "no"
NO_RECOMMENDATIONS = "1"
IMAGE_FSTYPES = "cpio.gz"
IMAGE_ROOTFS_EXTRA_SPACE = "0"
IMAGE_OVERHEAD_FACTOR = "1.0"

# Explicitly exclude standard base-files packagegroup defaults if needed
PACKAGE_INSTALL = " \
    packagegroup-core-boot \
    dropbear \
    mtd-utils \
    sfp-init \
    genskeleton \
"

KERNEL_CLASSES += "kernel-fitimage"
KERNEL_IMAGETYPE = "fitImage"

FITIMAGE_PACK_RAMDISK = "1"
CONFIG_BLK_DEV_INITRD = "n"

ROOTFS_POSTPROCESS_COMMAND += "create_interfaces_file; "

create_interfaces_file() {
    install -d ${IMAGE_ROOTFS}/etc/network
    cat << 'EOF' > ${IMAGE_ROOTFS}/etc/network/interfaces
auto lo
iface lo inet loopback

source-directory /etc/network/interfaces.d
EOF
}


# Hook up the FIT image packaging step
IMAGE_POSTPROCESS_COMMAND += "build_fitimage_ub; "

build_fitimage_ub() {
    # Check that inputs exist
    if [ ! -f "${DEPLOY_DIR_IMAGE}/Image" ] || [ ! -f "${DEPLOY_DIR_IMAGE}/system.dtb" ]; then
        bbwarn "Kernel Image or system.dtb missing in DEPLOY_DIR_IMAGE, skipping fitImage generation."
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
            data = /incbin/("${DEPLOY_DIR_IMAGE}/Image");
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
            data = /incbin/("${DEPLOY_DIR_IMAGE}/system.dtb");
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

    # Assemble fitImage using native mkimage tool
    mkimage -f ${WORKDIR}/fit-image.its ${IMGDEPLOYDIR}/image-${PN}.ub

    # Create convenience symlink image.ub
    ln -sf image-${PN}.ub ${IMGDEPLOYDIR}/image.ub
}