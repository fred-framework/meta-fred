ROOTFS_POSTPROCESS_COMMAND += "deploy_manifest;"
ROOTFS_POSTPROCESS_COMMAND += "copy_scripts;"
IMAGE_POSTPROCESS_COMMAND += "patch_pcap_devicetree;"

deploy_manifest () {
    if [ -e "${IMAGE_MANIFEST}" ]; then
        cp ${IMAGE_MANIFEST} ${IMAGE_ROOTFS}/home/root/rootfs.manifest
    fi
}

copy_scripts () {
    META_FRED_DIR="${TOPDIR}/../components/ext_source/meta-fred/
    cp ${META_FRED_DIR}/scripts/update_hw ${IMAGE_ROOTFS}/usr/bin/update_hw
}

# executes the procedure recommended in
# https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/18841676/U-Boot+Flattened+Device+Tree
# to patch the devicetree
patch_pcap_devicetree () {
    PETA_DEPLOY_DIR="${TOPDIR}/../images/linux"
    cd ${PETA_DEPLOY_DIR}
    cp ../../components/ext_source/meta-fred/recipes-kernel/fpga-mgr-zynqmp-drv/image.its .
    if [ -e "${PETA_DEPLOY_DIR}/system.dtb" ]; then
        #cp system.dtb system-bkp.dtb
        #cp image.ub image-bkp.ub
        dtc -O dts -o system.dts -b 0 -@ system.dtb
        sed -i 's/zynqmp-pcap-fpga/zynqmp-pcap-fpga-fmod/g' system.dts
        dtc -O dtb -o system.dtb -b 0 -@ system.dts    
        mkimage -f image.its image.ub
    fi
}