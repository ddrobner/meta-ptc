SUMMARY = "Network DHCP configuration for SFP interface"
DESCRIPTION = "Configures DHCP for the SFP ethernet interface. Generates \
a systemd-networkd configuration file or a busybox ifupdown interface file \
automatically based on DISTRO_FEATURES."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Network interface configuration - can be overridden in local.conf
NETWORK_IFACE ?= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'end1', 'eth1', d)}"

S = "${WORKDIR}"

do_compile() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        # Generate systemd-networkd DHCP configuration
        cat << EOF > ${B}/10-${NETWORK_IFACE}.network
[Match]
Name=${NETWORK_IFACE}

[Network]
DHCP=yes
LinkOnline=yes

[DHCPv4]
RouteMetric=100
EOF
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        # Generate busybox ifupdown interfaces configuration
        cat << EOF > ${B}/interfaces.${NETWORK_IFACE}
auto ${NETWORK_IFACE}
iface ${NETWORK_IFACE} inet dhcp
    metric 100
EOF
    fi
}

do_install() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        # Install systemd-networkd DHCP configuration
        install -d ${D}${sysconfdir}/systemd/network
        install -m 0644 ${B}/10-${NETWORK_IFACE}.network ${D}${sysconfdir}/systemd/network/10-${NETWORK_IFACE}.network
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        # Install busybox interfaces configuration
        install -d ${D}${sysconfdir}/network/interfaces.d
        install -m 0644 ${B}/interfaces.${NETWORK_IFACE} ${D}${sysconfdir}/network/interfaces.d/${NETWORK_IFACE}
    fi
}

FILES:${PN} += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${sysconfdir}/systemd/network/10-${NETWORK_IFACE}.network', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/network/interfaces.d/${NETWORK_IFACE}', '', d)} \
"