FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://bsp.cfg"
KERNEL_FEATURES:append = " bsp.cfg"
SRC_URI += "file://linux-merged.cfg \
            file://0001-macb-Add-Marvell-88E111-Auto-Neg-Bypass-fixup-via-I2.patch \
            "
