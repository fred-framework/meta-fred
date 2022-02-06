
# FRED Yocto Layer

This is a Yocto layer to include [FRED](http://fred.santannapisa.it/) into to a Linux image generated by petalinux.

## Dependencies

This layer depends on:

* URI: git://git.yoctoproject.org/poky
  * branch: master
  * revision: HEAD

* URI: git://git.openembedded.org/meta-openembedded
  * layers: meta-oe
  * branch: master
  * revision: HEAD

## Choosing the petalinux and Yocto versions

In this tutorial we are using **petalinux 2020.2** and it assumes it is already installed. According to this [page](https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/18841883/Yocto), petalinux 2020.2 is associated with Yocto 3.0 (zeus) and Linux kernel 5.4. This information is important because, in the future when we need to update petalinux version, we have to change the Yocto version in `meta-fred` by changing this file `conf/layer.conf`. Currently this file says that `meta-fred` is compatible with Yocto `zeus`. 

```cmake
LAYERSERIES_COMPAT_meta-fred = "zeus"
```
In fact, 2020.1 is the oldest supported version of petalinux. The recipe `recipes-kernel/fpga-mgr-zynqmp-drv/fpga-mgr-zynqmp-drv_1.0.bb` depends on an update of the `fpga-mgr` kernel module that was only inserted in Linux kernel 5.4. Except for this recipe, meta-fred would *probably* be compatible with older petalinux versions too.

## Supported FPGA platforms 

FRED is tested in zynq and zynqmp devices, namely zcu102-zynqmp, pynq-z1, and Ultra96-zynqmp. 
Petalinux/Yocto has a variable called `MACHINE` that specifies the target hardware. This variable is automatically set when we assign a XSA or BSP file to a petalinux project. One can check the list of Xilinx machines in the directory `./components/yocto/layers/meta-xilinx/meta-xilinx-bsp/conf/machine/` under a petalinux project. In the petalinux flow, `MACHINE` can be viewed in  *petalinux-config --> Yocto settings --> () MACHINE NAME.*

## Building a Standard petalinux Project for a Zynq device

Follow the standard steps to build a petalinux project for a zynq device, in this case the Pynq board:

```bash
 $ source /storage/Xilinx/PetaLinux/2020.2/tool/settings.sh
 $ petalinux-create --type project --template zynq --name pynq-2020.2
 $ cd pynq-2020.2/
 $ petalinux-config --get-hw-description /storage/Xilinx/PetaLinux/2020.2/pynq-z1-2020.2.xsa
```
The reference XSA file for Zynq board is built based on this [repository](https://github.com/Xilinx/PYNQ.git) using the same petalinux version.

Let's run the `petalinux-config` to configure some minimal parameters:
```bash
 $ petalinux-config
```
Select at least these options:

```
Subsystem AUTO Hardware Settings  ---> Ethernet Settings ---> Randomise MAC address
Image Packaging Configuration  ---> Root filesystem type (INITRD)  ---> EXT4 (SD/eMMC/SATA/USB) 
```

Save the configuration and press ESC until quit the application. Finally, build the image with the following command:

```bash
 $ petalinux-build
```

This last command takes a long time. Go take a coffee !

## Building a Standard petalinux Project for a ZynqMP device

This is the procedure for Zynq Ultra Scale+, in this case the zcu102 board:

```bash
 $ source /storage/Xilinx/PetaLinux/2020.2/tool/settings.sh
 $ petalinux-create -t project -s /storage/Xilinx/PetaLinux/2020.2/xilinx-zcu102-v2020.2-final.bsp
 $ cd xilinx-zcu102-2020.2/
```

The ZCU102 PetaLinux BSP file can be downloaded from [here](https://www.xilinx.com/member/forms/download/design-license-zcu102-base.html?filename=xilinx_zcu102_base_202010_1.zip).

Let's run the `petalinux-config` to configure some minimal parameters:
```bash
 $ petalinux-config
```
Select at least these options:

```
Subsystem AUTO Hardware Settings  ---> Ethernet Settings ---> Randomise MAC address
Image Packaging Configuration  ---> Root filesystem type (INITRD)  ---> EXT4 (SD/eMMC/SATA/USB) 
```

Save the configuration and press ESC until quit the application. Finally, build the image with the following command:

```bash
 $ petalinux-build
```

This last command takes a long time. Go take a coffee !

# Kernel requirements

The Linux kernel must be compiled with **overlay filesystem** and **FPGA Manager** enabled. On the board one can check it with this command:

```
$ zcat /proc/config.gz | grep OVERLAY
CONFIG_OF_OVERLAY=y
CONFIG_OVERLAY_FS=y
# CONFIG_OVERLAY_FS_REDIRECT_DIR is not set
CONFIG_OVERLAY_FS_REDIRECT_ALWAYS_FOLLOW=y
# CONFIG_OVERLAY_FS_INDEX is not set
# CONFIG_OVERLAY_FS_XINO_AUTO is not set
# CONFIG_OVERLAY_FS_METACOPY is not set
```

```
~# zcat /proc/config.gz | grep FPGA
CONFIG_FPGA=y
# CONFIG_FPGA_MGR_DEBUG_FS is not set
...
CONFIG_FPGA_MGR_ZYNQMP_FPGA=y
CONFIG_FPGA_MGR_VERSAL_FPGA=y
```

The following parameters must be enabled:
- `CONFIG_UIO`: [Userspace I/O drivers](https://www.kernel.org/doc/html/v4.11/driver-api/uio-howto.html);
- `CONFIG_UIO_PCI_GENERIC`: Generic driver for PCI 2.3 and PCI Express cards;
- `DEVTMPFS`: Maintain a devtmpfs filesystem to mount at /dev;
- `DEVTMPFS_MOUNT`: Automount devtmpfs at /dev;

CONFIG_UIO_PCI_GENERIC=y
# CONFIG_UIO_XILINX_APM is not set

During design time, run:

```
$ petalinux-config -c kernel
```

## Adding meta-fred Layer to a petalinux Project

Now we add the meta-fred Yocto layer, following similar steps as in this [link](https://www.zachpfeffer.com/single-post/Add-a-Yocto-Layer-to-a-PetaLinux-Project-and-Build-a-Recipe-in-the-Layer-with-PetaLinux-Tools), with some simplications:

```bash
$ mkdir -p components/ext_source
$ cd components/ext_source
$ git clone https://github.com/fred-framework/meta-fred.git
$ cd ../..
$ petalinux-config
```
In `petalinux-config`, follow these steps util reaching `user layer 0`:

```
Yocto Settings ---> User Layers ---> user layer 0
```

Then write the path to the layer:

```bash
${PROOT}/components/ext_source/meta-fred
```

Back in the terminal, run: 

```bash
 cat build/conf/bblayers.conf 
 ...
  /<basedir>/pynq-2020.2/components/ext_source/meta-fred \
  "
```

and we should see the new layer inserted.

Finally, we build the image again, this time including meta-fred recipes:

```bash
$ petalinux-build
```

This time the build process will take only a couple of minutes.

These are some useful commands to show the new recipes:

```bash
$ source ./components/yocto/layers/core/oe-init-build-env
$ bitbake-layers show-layers
NOTE: Starting bitbake server...
layer                 path                                      priority
==========================================================================
meta                  /ssd/work/petalinux/2020.2/pynq-2020.2/pynq-2020.2/components/yocto/layers/core/meta  5
...
workspace             /ssd/work/petalinux/2020.2/pynq-2020.2/pynq-2020.2/components/yocto/workspace  99
meta-fred             /ssd/work/petalinux/2020.2/pynq-2020.2/pynq-2020.2/components/ext_source/meta-fred  7

$ bitbake-layers show-recipes > recipes.txt
$ grep -i mgr recipes.txt
efibootmgr:
fpga-mgr-zynqmp-drv:
$ grep -i fred recipes.txt
  meta-fred            1.0
fred-buffctl:
  meta-fred            1.0
fred-lib:
  meta-fred            1.0
fred-server:
  meta-fred            1.0
fred-tutorial:
  meta-fred            1.0
```

In a zynq device you should see `fpga-mgr-zynq-drv` instead of `fpga-mgr-zynqmp-drv` when running `grep -i mgr recipes.txt`. 


```bash
$ bitbake -e fred-lib | grep ^WORKDIR=
WORKDIR="/<basedir>/xilinx-zcu102-2020.2/build/tmp/work/aarch64-xilinx-linux/fred-lib/1.0-r0"
```

The following command shows the list of packages installed into the image.

```bash
$ cat images/linux/rootfs.manifest | grep fred
fred-buffctl zynqmp_generic 1.0
fred-buffctl-lic zynqmp_generic 1.0
fred-lib aarch64 1.0
fred-lib-lic aarch64 1.0
fred-server aarch64 1.0
fred-server-lic aarch64 1.0
fred-tutorial aarch64 1.0
fred-tutorial-lic aarch64 1.0
kernel-module-fred-buffctl-5.4.0-xilinx-v2020.2 zynqmp_generic 1.0
$ cat images/linux/rootfs.manifest | grep mgr
fpga-mgr-zynqmp-drv zynqmp_generic 1.0
fpga-mgr-zynqmp-drv-lic zynqmp_generic 1.0
$ cat rootfs.manifest | grep fmod
kernel-module-zynqmp-fpga-fmod-5.4.0-xilinx-v2020.2 zynqmp_generic 1.0
```

## Installed files

This layer will install the following file into the Linux image:

```
/usr/bin/fred-server
/lib/modules/${KERNEL_VERSION}/kernel/drivers/fred/fred-buffctl.ko
/lib/modules/${KERNEL_VERSION}/kernel/drivers/fred/zynqmp-fpga-fmod.ko
/usr/bin/fred-test-cli
/usr/lib/libfred.so
```
where `KERNEL_VERSION=5.4.0-xilinx-v2020.2`.

The `rootfs` is mounted in `build/tmp/work/zynqmp_generic-xilinx-linux/<image-name>/1.0-r0/rootfs`.



## Compiling the Device Tree for the Reconfigurable Regions - design time approach

The device tree must be generated with embedded labels so these labels can be referenced later in the device tree overlays in runtime. To do so, `dtc` must be executed the `-@` argument. This will make dtc retain information about labels when generating a dtb file, which will allow Linux to figure out at runtime what device tree node a label was referring to. To embed the labels in the devicetree using petalinux, the `CONFIG_SUBSYSTEM_DEVICETREE_COMPILER_FLAGS` option must be set. In the base directory run the following command:

```
$ grep -r CONFIG_SUBSYSTEM_DEVICETREE_COMPILER_FLAGS --include="config" .
./project-spec/configs/config:CONFIG_SUBSYSTEM_DEVICETREE_COMPILER_FLAGS="-@"
./pre-built/linux/images/config:CONFIG_SUBSYSTEM_DEVICETREE_COMPILER_FLAGS="-@"
```

This step inserts the device-tree segment related to FRED reconfigurable regions (RR) into the Linux device-tree. If the device tree was not changed during these previous steps, the merge has already been done. Otherwise, execute the following commands in the petalinux project directory:

```bash
$ petalinux-build -c device-tree -x cleansstate
$ petalinux-build -c device-tree
```

Once the image has been built, execute the following commands to check that the devicetree merge was succesful. One can see the `decoupler` and the `slot` devices required by FRED. In this case there is only one reconfigurable region.

```bash
$ cd images/linux
$ dtc -I dtb -O dts -o system.dts system.dtb
$ cat system.dts | grep decoupler
		pr_decoupler_p0_s0@A0010000 {
$ cat system.dts | grep slot
		slot_p0_s0@A0000000 {
```

Once in the board, the device tree can be checked running:

```
$ ls /proc/device-tree/amba/
#address-cells
...
pr_decoupler_p0_s0@A0010000
...
slot_p0_s0@A0000000
...
```

## Compiling the Device Tree for the Reconfigurable Regions - overlay approach

Using device tree overlay it is possible to patch the device tree in while running the system.

```
$ dtc -O dtb -o fred.dtbo -b 0 -@ fred.dts
$ mkdir -p /sys/kernel/config/device-tree/overlays/fred-static
$ cat fred.dtbo > /sys/kernel/config/device-tree/overlays/fred-static/dtbo
```

If there are no messages, proceed to check the device tree executing:

```
$ ls /proc/device-tree/amba/
#address-cells
...
pr_decoupler_p0_s0@A0010000
...
slot_p0_s0@A0000000
...
```

When running the image, the devicetree data is available at:

```
/boot/devicetree/system-top.dtb
/sys/firmware/devicetree/base/__symbols__/
/sys/kernel/config/device-tree/overlays/
/proc/device-tree
```

## Replacing `zynqmp-pcap-fpga` driver

FRED replaces the `zynqmp-pcap-fpga` driver for the PCAP interface by a custom driver called `zynqmp-pcap-fpga-fmod`. Thus, the PCAP segment of the device tree must change the PCAP `compatible` attribute to use the modified driver. See the following example of the original device tree segment:

```
zynqmp_pcap: pcap {
        compatible = "xlnx,zynqmp-pcap-fpga";
        ...
};
```
and the modified one:

```
zynqmp_pcap: pcap {
        compatible = "xlnx,zynqmp-pcap-fpga-fmod";
        ...
};
```

This replacement is executed after the image is completly generated, running the following commands:

```
$ cd images/linux
$ cp system.dtb system-bkp.dtb
$ cp image.ub image-bkp.ub
$ dtc -O dts -o system.dts -b 0 -@ system.dtb
$ sed -i 's/zynqmp-pcap-fpga/zynqmp-pcap-fpga-fmod/g' system.dts
$ dtc -O dtb -o system.dtb -b 0 -@ system.dts
$ mkimage -f image.its image.ub
```

These steps, defined in [`classes/post-process.bbclass`](./classes/post-process.bbclass),
retrieve the DTS file, patch it with the modified driver name, regenerate the DTB, and merge again the `Image` and  `system.dtb`, forming the boot image file `image.ub` with the modifed driver name. 

Finally, perform the usual data copy to the SD card. When running in the board, please check whether PCAP is using the modified driver.

```
$ cat /sys/firmware/devicetree/base/firmware/zynqmp-firmware/pcap/compatible 
xlnx,zynqmp-pcap-fpga-fmod
```

Note that the PCAP driver can only be configured prior the system execution. Thus, it's not possible to use the overlay approach to load it since it is loaded during boot and it cannot be replaced in runtime.

## Running FRED manually

Both FRED kernel modules are located in the `/lib/modules/<kernel-version>/kernel/drivers/fred/` directory in the generated image. These modules are automatically loaded by default during boot. To change this, comment out the lines with the `KERNEL_MODULE_AUTOLOAD` option in [`./conf/layer.conf`](./conf/layer.conf).

## FRED startup Script

The recipe `recipes-kernel\bootscript` starts up FRED right after boot using `init.d`. Like in the previous section, this is already integrated into the image when running the standard build for this `meta-fred` layer. However, if the script is modified, the following commands must be executed for the image update"

https://tuxengineering.com/blog/2021/07/17/Upgrading-Petalinux-2021.html

```bash
$ petalinux-build -c bootscript -x do_install -f
$ petalinux-build -c rootfs
$ petalinux-build
```

## Suggested development workflow

When testing a new design, it's normal to have to recompile packages multiple times. The suggested workflow is to add package mangement into the image, cross-compile the package with petalinux, and install the updated package into the board. Here is an example assuming that `fred-server` package was updated.

In the host computer:

 - Push the modification to fred-server repository;
 - Change the fred-server recipe to `SRCREV = "${AUTOREV}"`, making petalinux fetch the latest code revision;
 - Add the following commands to the `./conf/layer.conf` file:
```
EXTRA_IMAGE_FEATURES += " package-management " 
PACKAGE_FEED_URIS = "http://<host-ip>:8000" 
```
The first command installs the package-management tool (opkg, dnf, apt), depending on the format selected next. The default is rpm. The second points to the package repository.
 - Start the package repository webserver running:
```
$ cd ./build/tmp/deploy/rpm
$ python -m SimpleHTTPServer
```

These steps above are done only once. The next ones are executed every time the package is updated:

 - Clean the local source code with `$ petalinux -c fred-server -x cleanall`;
 - Fetch the new source code and compile it with `$ petalinux -c fred-server`;
 - Update the package repository running `petalinux-build -c package-index`;
 - 

Back to the board, check the content of `/etc/yum.repos.d/` to see if it is pointing to the package repository. For example:

```
$ cat /etc/yum.repos.d/oe-remote-repo.repo 
[oe-remote-repo]
name=OE Remote Repo:
baseurl=http://<host-ip>:8000
gpgcheck=0
```

Then, execute:

```
$ dnf update
$ dnf install fred-server
```

### Board and package repository not on the same network


3123  ssh -N ubuntu@localhost -L 8000: 10.30.3.59:8000
 3124  ssh -N ubuntu@localhost -L 8001: 10.30.3.59:8000
 3135  ping 10.30.3.59:8000
 3137  ping https://10.30.3.59:8000
 3138  ping http://10.30.3.59:8000
 3139  traceroute6 https://10.30.3.59:8000
 3140  iptables -t nat -A PREROUTING -p tcp --dport 8000 -j DNAT --to-destination 10.30.3.59:8000
 3141  sudo iptables -t nat -A PREROUTING -p tcp --dport 8000 -j DNAT --to-destination 10.30.3.59:8000


## Suggested Git Branch Organization

This is suggested the git branch organization for the support of future petalinux versions. 

```
main
├── v2020.2
├── v2021.1
└── ...
```

## References

 - [Zynq PL Programming With FPGA Manager](https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/18841645/Solution+Zynq+PL+Programming+With+FPGA+Manager);
 - [ZynqMP PL Programming](https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/18841847/Solution+ZynqMP+PL+Programming);
 - [U-Boot Flattened Device Tree](https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/18841676/U-Boot+Flattened+Device+Tree);
 - 

## TO DO

 - [ ] build the correct kernel module based on the FPGA model , e.g. `zynq` or `zynqmp`;
 - [ ] automatically load FRED kernel modules;
 - [ ] include the device tree from DART;
 - [ ] make a [cmake module](https://gitlab.kitware.com/cmake/community/-/wikis/doc/tutorials/How-To-Find-Libraries) for `fred-lib`;
 - [ ] Use [SystemC cosim](https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/862421112/Co-simulation) for testing FRED;
 - [ ] write a [QEMU device model](https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/861569267/QEMU+Device+Model+Development) for testing FRED;

## Contributions

  Did you find a bug in this tutorial ? Do you have some extensions or updates to add ? Please send me a Pull Request.

## Authors

 - Alexandre Amory (April 2021), ReTiS Lab, Scuola Sant'Anna, Pisa, Italy.

## Funding
 
This software package has been developed in the context of the [AMPERE project](https://ampere-euproject.eu/). This project has received funding from the European Union’s Horizon 2020 research and innovation programme under grant agreement No 871669.
