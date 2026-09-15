do_compile() {
    cat << 'EOF' > ${S}/boot.cmd
setenv autoload no
setenv netretry no


echo "=== Starting HTTP Netboot ==="
setenv netbootip ${serverip}
if test "x$httpip" != "x"; then 
    echo "Using HTTP server IP loaded from environment for initramfs";
    setenv netbootip ${httpip}; 
fi

echo "Server IP is: ${netbootip}"
ping ${netbootip}

setenv bootargs "console=ttyPS0,115200 clk_ignore_unused earlycon"

# Loop until wget succeeds
echo "Fetching image.ub over HTTP..."
until wget 0x40000000 ${netbootip}:/ptc/${net_img_dir}/image.ub; do
    echo ">>> wget timed out or failed! Retrying in 1s... <<<"
    sleep 1
done

echo "Download complete! Booting Linux..."
bootm 0x40000000


EOF

    uboot-mkimage -A arm -O linux -T script -C none -a 0 -e 0 -n "HTTP Boot Script" -d ${S}/boot.cmd ${B}/boot.scr

    touch ${S}/pxeboot.pxe
    touch ${B}/pxeboot.pxe
}