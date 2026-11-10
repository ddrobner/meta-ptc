SUMMARY = "PTC QC Client"

# Ignore the license checks... This is our own software after all
LICENSE = "CLOSED"

SRCREV = "${AUTOREV}"
SRC_URI = " \
            git://github.com/anikolica/PTC-firmware.git;protocol=https;branch=qc_soft;subpath=board_qc \
            file://wheels/ \
            file://requirements.txt \
        "

S = "${WORKDIR}/board_qc"

DEPENDS += "python3-pip-native"

inherit python3native

do_install() {
    # 1. Copy application files
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    cp -r ${S}/ptctestclient ${D}${PYTHON_SITEPACKAGES_DIR}/
    cp -r ${S}/ptctestsuite ${D}${PYTHON_SITEPACKAGES_DIR}/

    # 2. Install wheels
    ${STAGING_BINDIR_NATIVE}/python3-native/python3 -m pip install \
        --no-deps \
        --no-index \
        --break-system-packages \
        --find-links=${WORKDIR}/wheels \
        --target=${D}${PYTHON_SITEPACKAGES_DIR} \
        -r ${WORKDIR}/requirements.txt

    # 3. Move executables to /usr/bin and rewrite shebangs to target python3
    if [ -d ${D}${PYTHON_SITEPACKAGES_DIR}/bin ]; then
        install -d ${D}${bindir}
        mv ${D}${PYTHON_SITEPACKAGES_DIR}/bin/* ${D}${bindir}/
        rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/bin
        sed -i -e '1s|^#!.*python.*|#!/usr/bin/env python3|' ${D}${bindir}/*
    fi
}

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}/*"


# Packages that are in meta-python
RDEPENDS:${PN} += " \
    bash \
    python3-core \
    python3-cryptography \
    python3-cffi \
    python3-asyncio \
    python3-pyyaml \
    python3-prompt-toolkit \
    python3-pyserial-asyncio \
    python3-aiofiles \
    python3-httpx \
"
