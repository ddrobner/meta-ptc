echo "========================================="
echo "  Booting Local OS from SD Card...       "
echo "========================================="

setenv bootargs console=ttyPS0,115200 root=/dev/mmcblk0p2 rootwait rw

if load mmc 0:1 0x40000000 image.ub; then
    echo "image.ub loaded successfully. Booting..."
    
    # Boot the FIT image
    bootm 0x40000000
else
    echo "ERROR: Failed to load image.ub from SD card!"
    shell
fi