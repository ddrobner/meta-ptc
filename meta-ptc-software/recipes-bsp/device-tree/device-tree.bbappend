FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Ensure the kernel shared workdir is populated before compiling the device tree
do_configure[depends] += "virtual/kernel:do_shared_workdir"

# Add the correct header search paths (no wildcards)
KERNEL_INCLUDE:append = " \
    ${STAGING_KERNEL_DIR}/include \
    ${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts \
    ${STAGING_KERNEL_DIR}/scripts/dtc/include-prefixes \
"

SRC_URI:append = " \
    file://system-user.dtsi \
    file://net-conf.dtsi \
    file://sd-conf.dtsi \
    file://pwr-conf.dtsi \
    file://mtd-conf.dtsi \
    file://i2c-conf.dtsi \
"

require ${@'device-tree-sdt.inc' if d.getVar('SYSTEM_DTFILE') != '' else ''}

do_configure:append() {
    # Resolve unpack directory for both older and newer Yocto versions
    SRC_DIR="${UNPACKDIR}"
    if [ -z "${SRC_DIR}" ] || [ ! -d "${SRC_DIR}" ]; then
        SRC_DIR="${WORKDIR}"
    fi

    # Determine target DT directory (fallback to ${B} if DT_FILES_PATH is unset)
    TARGET_DIR="${DT_FILES_PATH}"
    if [ -z "${TARGET_DIR}" ]; then
        TARGET_DIR="${B}"
    fi

    # Copy all custom dtsi files to the build/DT directory
    for dts_file in ${SRC_DIR}/*.dtsi; do
        if [ -f "$dts_file" ]; then
            cp -f "$dts_file" "${TARGET_DIR}/"
        fi
    done

    # Ensure system-user.dtsi is explicitly included in system-top.dts
    # (Assuming system-user.dtsi includes the other net/sd/pwr/mtd/i2c .dtsi files,
    # or you can append each one here directly)
    if [ -f "${TARGET_DIR}/system-top.dts" ]; then
        if ! grep -q 'system-user.dtsi' "${TARGET_DIR}/system-top.dts"; then
            echo '#include "system-user.dtsi"' >> "${TARGET_DIR}/system-top.dts"
        fi
    fi
}