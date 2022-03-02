# FRED application to sum two vectors

A very simple FRED application that sums two vectors, i.e `C_out = A_in + B_in`. It uses [`fred-lib`](https://github.com/fred-framework/fred-linux-client-lib) to access [`fred-server`](https://github.com/fred-framework/fred-linux). 

From the hardware perpective, this application uses the hw IP [`sum_vec`](https://github.com/fred-framework/dart_ips/tree/master/ips/sum_vec) to perform the FPGA offloading. The IP is supposed to have `hw ID = 100`. The devicetree file [`files/example-frag.dts`](./recipes-example/sum-vec/files/example-frag.dts) is a minimal example considering a Linux image built with the recommended [`retis-meta`](https://github.com/fred-framework/meta-retis) Yocto Layer.

This application can be compiled either with Yocto or in the board, if these requirements are installed into the board:
 - build-essential;
 - cmake;
 - fred-lib-dev;

To compile it directly in the board, just copy the files under `./files` to the board, compile it with cmake, and compile the devicetree segment. These are the expected commands for compilation and execution:

```
$ wget https://raw.githubusercontent.com/fred-framework/meta-fred/main/recipes-example/sum-vec/files/CMakeLists.txt
$ wget https://raw.githubusercontent.com/fred-framework/meta-fred/main/recipes-example/sum-vec/files/sum-vec.c
$ wget https://raw.githubusercontent.com/fred-framework/meta-fred/main/recipes-example/sum-vec/files/example-frag.dts
$ mkdir build; cd build
$ cmake ..
$ make
$ cp example-frag.dts /opt/fredsys/static.dts
$ dtc -O dtb -o /opt/fredsys/static.dtbo -b 0 -@ /opt/fredsys/static.dts
$ load_hw
$ fred-server &
```

In another terminal run: 

```
$ ./sum-vec
sum-vec 
 starting vector sum 
fred_lib: connected to fred server!
buff: buffer mapped at addresses: 0xffff89bc1000, length:32768 
buff: buffer mapped at addresses: 0xffff89a15000, length:32768 
buff: buffer mapped at addresses: 0xffff89a0d000, length:32768 
Match!
Content of A[0:9]:
[ 0 0 0 0 0 0 0 0 0 0 ] 
Content of B[0:9]:
[ 1 1 1 1 1 1 1 1 1 1 ] 
Content of C[0:9]:
[ 1 1 1 1 1 1 1 1 1 1 ] 
Fred finished
```

It sums a vector with zeros with a vector with ones. Thus, the expected output is a vector with ones with a print similar to this, including the word *Match*.