# In your base image include (or inside each image recipe)

# Ensure U-Boot mkimage and the kernel are built before assembling the image
DEPENDS += "u-boot-tools-native virtual/kernel"

# After the cpio.gz is built, assemble the custom fitImage for THIS image
IMAGE_POSTPROCESS_COMMAND += "generate_custom_fitimage; "

generate_custom_fitimage() {
    cd ${DEPLOY_DIR_IMAGE}

    # Generate a standard U-Boot FIT Image Source (.its) file dynamically
    # bundling Image + system.dtb + ${PN}-${MACHINE}.cpio.gz
    
    cat << 'EOF' > fit-image.its
/dts-v1/;

/ {
    description = "U-Boot FIT Image";
    #address-cells = <1>;

    images {
        kernel-1 {
            description = "Linux Kernel";
            data = /incbin/("Image");
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
            data = /incbin/("system.dtb");
            type = "flat_dt";
            arch = "arm64";
            compression = "none";
            hash-1 {
                algo = "sha256";
            };
        };
        ramdisk-1 {
            description = "Initramfs Rootfs";
            data = /incbin/("${PN}-${MACHINE}.rootfs.cpio.gz");
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

    # Substitute the image name dynamically
    sed -i "s/\${PN}/${PN}/g" fit-image.its
    sed -i "s/\${MACHINE}/${MACHINE}/g" fit-image.its

    # Assemble into image.ub / fitImage
    mkimage -f fit-image.its ${DEPLOY_DIR_IMAGE}/fitImage-${PN}-${MACHINE}.bin
    
    # Symlink to image.ub for convenience/TFTP booting
    ln -sf fitImage-${PN}-${MACHINE}.bin ${DEPLOY_DIR_IMAGE}/image-${PN}.ub
}