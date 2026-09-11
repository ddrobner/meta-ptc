#
# This file is the ptc-scripts recipe.
#

SUMMARY = "PTC Python utility scripts"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://start_i2c \
    file://setup_timing \
    file://power_on_wib \
    file://ecat_test1b \
    file://read_dip_switches \
"

S = "${WORKDIR}"

INHIBIT_DEFAULT_DEPS = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/start_i2c ${D}${bindir}/
    install -m 0755 ${S}/setup_timing ${D}${bindir}/
    install -m 0755 ${S}/power_on_wib ${D}${bindir}/
    install -m 0755 ${S}/ecat_test1b ${D}${bindir}/
    install -m 0755 ${S}/read_dip_switches ${D}${bindir}/
}

RDEPENDS:${PN} = "python3"

