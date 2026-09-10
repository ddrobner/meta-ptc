SUMMARY = "U-Boot separate boot script for SD Card Boot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "u-boot-tools-native"
SRC_URI = "file://boot_sd.cmd"
S = "${WORKDIR}"

inherit deploy

do_compile() {
    mkimage -A arm -O linux -T script -C none -a 0 -e 0 \
            -n "SD Boot Script" \
            -d ${WORKDIR}/boot_sd.cmd ${B}/boot_sd.scr
}

# No need to do_install to target rootfs; just deploy it for manual copying
do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${B}/boot_sd.scr ${DEPLOYDIR}/boot_sd.scr
}

addtask deploy after do_compile before do_build
PROVIDES += "u-boot-sd-scr"