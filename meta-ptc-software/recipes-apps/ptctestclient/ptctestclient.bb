SUMMARY = "PTC QC Client"

# Ignore the license checks... This is our own software after all
LICENSE = "CLOSED"

SRCREV = "${AUTOREV}"
SRC_URI = " \
            git://github.com/anikolica/PTC-firmware.git;protocol=https;branch=qc_soft;subpath=board_qc/ptctestclient \
            file://wheels/ \
            file://requirements.txt \
        "

S = "${WORKDIR}/ptctestclient"

DEPENDS += "python3-pip-native"

inherit python3native

do_install() {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}/ptctestclient
    cp -r ${S}/* ${D}${PYTHON_SITEPACKAGES_DIR}/ptctestclient

    # Install wheels
    ${STAGING_BINDIR_NATIVE}/python3-native/python3 -m pip install \
        --no-deps \
        --no-index \
        --break-system-packages \
        --find-links=${WORKDIR}/wheels \
        --target=${D}${PYTHON_SITEPACKAGES_DIR} \
        -r ${WORKDIR}/requirements.txt

}

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}/*"


# Packages that are in meta-python
RDEPENDS:${PN} += " \
    python3-websockets \
    python3-asyncio \
"
