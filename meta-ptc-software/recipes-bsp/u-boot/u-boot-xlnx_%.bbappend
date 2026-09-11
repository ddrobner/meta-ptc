FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://platform-top.h file://bsp.cfg"
SRC_URI += "file://u-boot-merged.cfg \
            file://0001-Take-i2c-switches-out-of-reset-in-board_late_init.patch \
            file://0002-Read-MAC-address-from-DIP-switches-in-late-init.patch \
            file://menu_env.txt \
            "

DEPENDS += " u-boot-sd-scr"
DEPENDS += " u-boot-xlnx-scr" 
do_configure:prepend() {
    install -d ${S}/include/configs
    cp ${WORKDIR}/platform-top.h ${S}/include/configs/platform-top.h
    cp ${WORKDIR}/menu_env.txt ${S}/menu_env.txt
}
