
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

### Choosing the petalinux and Yocto versions

In this tutorial we are using **petalinux 2020.2** and it assumes it is already installed. According to this [page](https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/18841883/Yocto), petalinux 2020.2 is associated with Yocto 3.0 (zeus) and Linux kernel 5.4. This information is important because, in the future when we need to update petalinux version, we have to change the Yocto version in `meta-fred` by changing this file `conf/layer.conf`. Currently this file says that `meta-fred` is compatible with Yocto `zeus`. 

```cmake
LAYERSERIES_COMPAT_meta-fred = "zeus"
```
In fact, 2020.1 is the oldest supported version of petalinux. The recipe `recipes-kernel/fpga-mgr-zynqmp-drv/fpga-mgr-zynqmp-drv_1.0.bb` depends on an update of the `fpga-mgr` kernel module that was only inserted in Linux kernel 5.4. Except for this recipe, meta-fred would *probably* be compatible with older petalinux versions too.

### Supported FPGA platforms 

FRED is tested in zynq and zynqmp devices, namely zcu102-zynqmp, pynq-z1, and Ultra96-zynqmp. 
Petalinux/Yocto has a variable called `MACHINE` that specifies the target hardware. This variable is automatically set when we assign a XSA or BSP file to a petalinux project. One can check the list of Xilinx machines in the directory `./components/yocto/layers/meta-xilinx/meta-xilinx-bsp/conf/machine/` under a petalinux project. In the petalinux flow, `MACHINE` can be viewed in  *petalinux-config --> Yocto settings --> () MACHINE NAME.*

### Software requirements in the host 

The host computer requires this additional packages

```
$ sudo apt install u-boot-tools
```

`u-boot-tools` install mkimage, used to patch the device tree.

## Building a Standard petalinux Project

### Building for a Zynq device

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

### Building for a ZynqMP device

This is the procedure for Zynq Ultra Scale+, in this case the zcu102 board:

```bash
 $ source /storage/Xilinx/PetaLinux/2020.2/tool/settings.sh
 $ petalinux-create -t project -s /storage/Xilinx/PetaLinux/2020.2/xilinx-zcu102-v2020.2-final.bsp
 $ cd xilinx-zcu102-2020.2/
```

The ZCU102 PetaLinux BSP file can be downloaded from [here](https://www.xilinx.com/member/forms/download/design-license-zcu102-base.html?filename=xilinx_zcu102_base_202010_1.zip).

### Configuring Petalinux 

Let's run the `petalinux-config` to configure some minimal parameters:
```bash
 $ petalinux-config
```
Select at least these options:

```
Subsystem AUTO Hardware Settings  ---> Ethernet Settings ---> Randomise MAC address
Image Packaging Configuration  ---> Root filesystem type (INITRD)  ---> EXT4 (SD/eMMC/SATA/USB) 
```

Save the configuration and press ESC until quit the application. 

When petalinux-config exits, it creates the *build directory*. It is recommemded to reuse **Yocto downloads and sstates directories** from previous petalinux projects. If these two directories do not exist, no problem. It will only take longer to download all necessary code. For reuse the directories, it is enough to execute the follwoing commands:

```
$ cd build
$ ln -s <yocto-download-dir> downloads
$ ln -s <yocto-sstate-cache> sstate-cache
```

## Kernel requirements

Luckly, all kernel parameters mentioned in this section are alredy enabled by default. So no action is required to configure the kernel. But for documentation purposes, these is the list of parameters that must be enabled on the Linux kernel:
- `CONFIG_OF_OVERLAY`, `CONFIG_OVERLAY_FS`, and `CONFIG_OF_CONFIGFS`, : Enables the use of devicetree overlay;
- `CONFIG_FPGA`: enables the FPGA manager;
- `CONFIG_XILINX_PR_DECOUPLER`: FRED uses PR decoupler;
- `CONFIG_SIGNALFD`: used by fred-server;
- `CONFIG_UIO`: [Userspace I/O drivers](https://www.kernel.org/doc/html/v4.11/driver-api/uio-howto.html);
- `CONFIG_UIO_PDRV_GENIRQ`: Userspace I/O plataform driver with generic IRW handling;
- `CONFIG_CMA`: [Contiguous Memory Allocator](https://developer.toradex.com/knowledge-base/contiguous-memory-allocator-cma-linux). [A deep dive into CMA](https://lwn.net/Articles/486301/);
- `DEVTMPFS`: Maintain a devtmpfs filesystem to mount at /dev;
- `DEVTMPFS_MOUNT`: Automount devtmpfs at /dev;
- `IKCONFIG` and `IKCONFIG_PROC`: to create the file /proc/config.gz, usefull to debug the kernel cofiguration of a working Linux image.

During design time, run to configure/check the parameters:

```
$ petalinux-config -c kernel
```

On the board, check the kernel parameters with this command:

```
$ zcat /proc/config.gz | grep OVERLAY
```

## Bootloader requirements

The original arguments for Uboot booloader are:

```
$ cat /proc/cmdline
 earlycon console=ttyPS0,115200 clk_ignore_unused root=/dev/mmcblk0p2 rw rootwait
```

But it necessary to add this segment `uio_pdrv_genirq.of_id=generic-uio` to enable the UIO drivers used by FRED. 

To perform this change, run `petalinux-config` and:
- Disable the option located in 
```
DTG Settings --> Kernel bootargs --> generate bootargs automatically
```
- Enter manually the following bootargs:
```
earlycon console=ttyPS0,115200 clk_ignore_unused root=/dev/mmcblk0p2 rw rootwait uio_pdrv_genirq.of_id=generic-uio
```

When running in the board, double check the bootargs running:

```
$ cat /proc/cmdline
 earlycon console=ttyPS0,115200 clk_ignore_unused root=/dev/mmcblk0p2 rw rootwait uio_pdrv_genirq.of_id=generic-uio
```

In case Vitis AI DPU is included into the design, then the bootargs is:

```
earlycon console=ttyPS0,115200 clk_ignore_unused root=/dev/mmcblk0p2 rw rootwait cma=512M uio_pdrv_genirq.of_id=generic-uio
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

and we should see the new layer inserted. These are some useful commands to show the new recipes:

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

This layer will install the following files into the Linux image:

```
/usr/bin/fred-server
/usr/bin/update_hw
/usr/bin/load_hw
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

In `petalinux-config`, this  option can be found in:

```
DTG Settings --> Devicetree compiler flags
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

The following options are required to enable devicetree overlay:

- In `petalinux-config`, make sure that this option is enabled:
```
DTG Settings --> Devicetree overlay
```
- In the kernel, double check if these options are enabled:
```
`CONFIG_OF_OVERLAY`: Enables the use of devivetree overlay;
`CONFIG_OVERLAY_FS`: Enables the use of devivetree overlay;
```

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

The PCAP driver is used by [FPGA manager](https://www.kernel.org/doc/html/latest/driver-api/fpga/fpga-mgr.html) driver. To check if it was correctly loaded, execute:

```
$ ls /sys/class/fpga_manager/fpga0
device              flags               name                phy_bit_addr        phy_bit_rcfg_start  power               status              uevent
firmware            key                 of_node             phy_bit_rcfg_done   phy_bit_size        state               subsystem
```

## Building the Linux Image

In the `<PetaLinux-project>` directory, build the Linux images using the following command:

```
$ petalinux-build -c <image-name>
```

This last command takes a long time when it is first executed,specially if the Yocto downloads and sstate directories were not reused. After the build process is finished, verify the images in the directory `images/linux`. 

Go to the image directory and generate the boot image `BOOT.BIN` using the following command:

```
$ cd images/linux
$ petalinux-package --boot --fsbl zynqmp_fsbl.elf --u-boot
```

Or executing :

```
$ cd images/linux
$ petalinux-package --boot --force --fsbl zynqmp_fsbl.elf --fpga system.bit --pmufw pmufw.elf --atf bl31.elf --u-boot u-boot.elf
```
If the vivado design uses the **PL** part. Note that this last option includes the .bit into the `BOOT.BIN`. 

The list of installed packages can be found in the `images/linux/rootfs.manifest` file.

## Running FRED automatically

Both FRED kernel modules are located in the `/lib/modules/<kernel-version>/kernel/drivers/fred/` directory in the generated image. These modules are automatically loaded by default during boot. To change this, comment out the lines with the `KERNEL_MODULE_AUTOLOAD` option in [`./conf/layer.conf`](./conf/layer.conf). Run the following command to confirm that the modules were loaded:

```
$ lsmod
    Tainted: G  
fred_buffctl 16384 0 - Live 0xffff800008c55000 (O)
zynqmp_fpga_fmod 16384 1 - Live 0xffff800008c50000 (O)
```

**TODO**

The recipe `recipes-kernel\bootscript` starts up FRED right after boot using `init.d`. Like in the previous section, this is already integrated into the image when running the standard build for this `meta-fred` layer. However, if the script is modified, the following commands must be executed for the image update"

https://tuxengineering.com/blog/2021/07/17/Upgrading-Petalinux-2021.html

```bash
$ petalinux-build -c bootscript -x do_install -f
$ petalinux-build -c rootfs
$ petalinux-build
```

## Suggested development workflow

When testing a new design, it's normal to have to recompile software packages or the hardware design multiple times. The suggested workflow is to add package mangement into the image, cross-compile the package with petalinux, and install the updated package into the board. Here is an example assuming that `fred-server` package was updated.

### Updating FRED framework software

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
$ dnf install -y fred-server
```

`dnf update` is only executed to update the board internal cache. So you only need to run it when the package repository changes. If you want to install the debug version of a package, then you need to run:

```
$ dnf install -y fred-server fred-server-dbg fred-server-src
```

`fred-server-dbg` and `fred-server-src` installs the additional debug symbols and the source code for debugging. In the case of libraries, like `fred-lib` you might want to install its headers, enabling compilation inside the board. In this case you would also install the package `fred-lib-dev`.

An alternative to the package repository is to download the rpm file with scp and install it with `dnf install -y <package-name>.rpm`.

#### Board and package repository not on the same network

If the board is not connected to the same network of the package repository, then you probably need to setup a ssh tunnel to forward the requests. Go to the host computer, i.e. the one with access to boath the board and to the package repository and run:

```
$ ssh -N <username>@localhost -L 8000: <repository-server-ip>:8000
$ sudo iptables -t nat -A PREROUTING -p tcp --dport 8000 -j DNAT --to-destination <repository-server-ip>:8000
```

### Petalinux SDK install

Petalinux can also create the entire toolchain (gcc, gdb, make, cmake, rootfs, etc) required for development in the generated image. For this, execute following commands:

```
$ petalinux-build --sdk
$ cd images/linux
$ source sdk.sh
```

This `sdk.sh` will install the generated SDK for the target image.


### Remote debug with VSCode

This section describes the use of [VSCode for remote debugging](https://variwiki.com/index.php?title=Yocto_Programming_with_VSCode) a petalinux project. The following setup needs to be executed only once. These instructions are assuming that:
- `dnf` package manager is working in the board;
- Package repository provides the packages generated by the petalinux project;
- The SDK has been generated and installed, as mentioned in the previous section.

Next, run these commands in the host computer to setup a password-less access to the board:

```
$ ssh-keygen
$ ssh-copy-id root@<board-ip>
```

Then test if you are able to execute ssh without password.

Open VSCode in the petalinux directory and execute `cp -r components/ext_source/meta-fred/scripts/vscode_setup .vscode`. This will copy the pre-configured VSCode json files. Next, edit the file `.vscode/settings.json` where it is marked with **NEED TO BE FIXED**. The role of these variables are commented within [`./scripts/vscode_setup/settings.json`](./scripts/vscode_setup/settings.json) itself. 

Turn on the board. Create a mounting point to the board rootfs in the petalinux directory: 

```
$ mkdir target_root
$ cd target_root
$ sshfs root@<board-ip>:/ .
```

Finally, open the debug window in VSCode and select `C++ Launch` rule, defined in `launch.json`. A detailed description of these scripts can be found in [`./scripts/vscode_setup/readme`](./scripts/vscode_setup/readme). 


## Updating the Hardware Desgin

This image includes two ways to easy the hardware update process, generated by [DART](https://github.com/fred-framework/dart). In both cases, we assume that a DART design has been generated and, then, we generate a tar.gz file:

```
$ cd <dart-proj-dir>/fred
$ tar czf  ../fred.tar.gz .
```

The first approach is to place the `fred.tar.gz` in the base petalinux directory before executing `petalinux-build`. The function [`copy_dart_design`](./classes/post-process.bbclass) will copy the hardware design to the Linux image, in the directoty `/opt/fredsys/`. This approach is more usefull whrn the design is stable, and unlike to change.

The second approach is when the user is already running in the board. There is a script [`/usr/bin/update_hw`](./scripts/update_hw) that copies `fred.tar.gz` from the host computer to the board and extracts it into `/opt/fredsys/`. This approach is more appropriate when the design in being tested and debuged.

Finally, while running in the board, the new static bitstream can be loaded into the PL with the script [`/usr/bin/load_hw`](./scripts/load_hw). Note that `dmesg` shows the devicetree messages. Always check these messages because they are a common source of mismatches in the hardware/software interface. Still in `dmesg` messages, note that the `fpga_manager` device driver is the modified one. See the message *Xilinx ZynqMP FPGA Manager Fmod*.

Checkout in this example how to compile a [simple FRED application](./recipes-example/sum-vec/readme.md) and run a newly loaded hardware design.

## Checking on the Board

This section shows a list of commands usefull to check the OS environment for FRED.

### Check the bootloader configuration

```
$ cat /proc/cmdline
 earlycon console=ttyPS0,115200 clk_ignore_unused root=/dev/mmcblk0p2 rw rootwait uio_pdrv_genirq.of_id=generic-uio
```

### Check the FRED devicetree segments were loaded

```
$ ls /proc/device-tree/amba/
#address-cells
...
pr_decoupler_p0_s0@A0010000
...
slot_p0_s0@A0000000
...
```

### Check the FPGA manager is present:

```
ls /sys/class/fpga_manager/fpga0
```

### Check the FRED kernel modules were loaded

```
# cat /sys/class/uio/uio*/name 
slot_p0_s0
pr_decoupler_p0_s0
```

## Suggested Git Branch Organization

This is suggested the git branch organization for the support of future petalinux versions. 

```
main
├── v2020.2
├── v2021.1
└── ...
```

## Auxiliar Files

### `./scripts/pt-config`

This file is the `petalinux-config` file with all options mentioned in this manual. Thus, it is possible to apply all configurations via CLI by executing:

```
$ cp components/ext_source/meta-fred/scripts/pt-config ./project-spec/configs/config
```
or 
```
$ petalinux-config --defconfig components/ext_source/meta-fred/scripts/pt-config
```

## References

 - [Zynq PL Programming With FPGA Manager](https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/18841645/Solution+Zynq+PL+Programming+With+FPGA+Manager);
 - [ZynqMP PL Programming](https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/18841847/Solution+ZynqMP+PL+Programming);
 - [U-Boot Flattened Device Tree](https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/18841676/U-Boot+Flattened+Device+Tree);
 - [GPIO and Petalinux](https://www.linkedin.com/pulse/gpio-petalinux-part-2-roy-messinger/): about using UIO driver for a GPIO;
 - [Yocto: Debugging With the GNU Project Debugger (GDB) Remotely](https://docs.yoctoproject.org/singleindex.html#debugging-with-the-gnu-project-debugger-gdb-remotely)

## TO DO

 - [x] build the correct kernel module based on the FPGA model , e.g. `zynq` or `zynqmp`;
 - [x] automatically load FRED kernel modules;
 - [x] include the device tree from DART;
 - [x] instructions for remote debug, preferably with VSCode;
 - [ ] follow the [recipe style guidelines](https://www.openembedded.org/wiki/Styleguide)
 - [ ] make a [cmake module](https://gitlab.kitware.com/cmake/community/-/wikis/doc/tutorials/How-To-Find-Libraries) for `fred-lib`;
 - [ ] use [SystemC cosim](https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/862421112/Co-simulation) for testing FRED;
 - [ ] write a [QEMU device model](https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/861569267/QEMU+Device+Model+Development) for testing FRED;
 - [ ] Add an OpenCV + FRED example using these tutorial as a starting point
    - https://opencvguide.readthedocs.io/en/latest/opencvcpp/basics.html#sobel-edge-detection
    - https://opencvguide.readthedocs.io/en/latest/opencvpetalinux/basics.html
    - and, of course, the Lena image
    - https://raw.githubusercontent.com/opencv/opencv/4.x/samples/data/lena.jpg

## Contributions

  Did you find a bug in this tutorial ? Do you have some extensions or updates to add ? Please send me a Pull Request.

## Authors

 - Alexandre Amory (January 2022), [Real-Time Systems Laboratory (ReTiS Lab)](https://retis.santannapisa.it/), [Scuola Superiore Sant'Anna (SSSA)](https://www.santannapisa.it/), Pisa, Italy.

## Funding
 
This software package has been developed in the context of the [AMPERE project](https://ampere-euproject.eu/). This project has received funding from the European Union’s Horizon 2020 research and innovation programme under grant agreement No 871669.
