#
# This is the openocd application recipe
#
#

SUMMARY = "Open On-Chip Debugger"
SECTION = "PETALINUX/apps"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=599d2d1ee7fc84c0467b3d19801db870"

PV = "0.12.0"
SRCREV = "ptc-v0.12.0"
SRC_URI = "git://github.com/ddrobner/openocd.git;protocol=https;branch=ptc-v0.12.0;submodules=1"

S = "${WORKDIR}/git"
B = "${S}"

AUTOTOOLS_BROKEN = "1"
INHIBIT_AUTOTOOLS_DEPS = "1"


DEPENDS = "libusb1 libftdi autoconf-native automake-native libtool-native"

inherit pkgconfig

EXTRA_OECONF = "--enable-ptc"

do_configure[network] = "1"


do_configure:prepend() {
    # generate configure + jimtcl files
    export GIT_SSL_NO_VERIFY=1
    cd ${S}

    # need to override default behaviour for jimtcl to build correctly
    ./bootstrap

    ./configure --host=${HOST_SYS} --build=${BUILD_SYS} \
        --prefix=/usr ${EXTRA_OECONF}

    # After bootstrap, configure jimtcl subsystem manually
    if [ -d ${S}/jimtcl ]; then
        echo "Configuring jimtcl (submodule)..."
        cd ${S}/jimtcl
        autoreconf -fi || true
        ./configure --host=${HOST_SYS} --build=${BUILD_SYS}
    fi
}

do_compile[dirs] = "${S}"
do_compile() {
    oe_runmake -C ${S}
}
do_install:append() {
    echo ">>> compiling inside ${PWD}"
    # program binaries
    install -d ${D}${bindir}
    install -m 0755 ${S}/src/openocd ${D}${bindir}/

    # configuration / script files
    install -d ${D}${datadir}/openocd/scripts
    cp -r ${S}/tcl/* ${D}${datadir}/openocd/scripts/
}
