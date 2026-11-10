SUMMARY = "RAM fallback for missing SD card persistent storage"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://overlay-persist-fallback.service"

inherit systemd

SYSTEMD_SERVICE:${PN} = "overlay-persist-fallback.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/overlay-persist-fallback.service ${D}${systemd_system_unitdir}/
}