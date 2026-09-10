#
# This file is the genskeleton recipe.
#

SUMMARY = "Generate requisite empty directories in rootfs"
SECTION = "PETALINUX/apps"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://genskeleton \
	"

S = "${WORKDIR}"

do_install() {
		 install -d -m 0777 ${D}/mnt/persist
}

FILES:${PN} += "/mnt/persist"