package dialight.clientshield.api;

public enum SysinfoType {

    // *** CPU ***
    /**
     * Gets the Processor ID. This is a hexidecimal string representing an 8-byte
     * value, normally obtained using the CPUID opcode with the EAX register set to
     * 1. The first four bytes are the resulting contents of the EAX register, which
     * is the Processor signature, represented in human-readable form by
     * {@link #CPU_SIGNATURE} . The remaining four bytes are the contents of the
     * EDX register, containing feature flags.
     * <p>
     * For processors that do not support the CPUID opcode this field is populated
     * with a comparable hex string. For example, ARM Processors will fill the first
     * 32 bytes with the MIDR. AIX PowerPC Processors will return the machine ID.
     * <p>
     * NOTE: The order of returned bytes is platform and software dependent. Values
     * may be in either Big Endian or Little Endian order.
     * <p>
     * NOTE: If OSHI is unable to determine the ProcessorID from native sources, it
     * will attempt to reconstruct one from available information in the processor
     * identifier.
     * example: ACC2FEAB102306B1
     */
    CPU_ID,

    /**
     * example: Intel(R) Core(TM)2 Duo CPU T7300 @ 2.00GHz
     */
    CPU_NAME,

    /**
     * Identifier processors, this string is populated with comparable values.
     * example: x86 Family 6 Model 15 Stepping 10. For non-Intel/AMD
     */
    CPU_SIGNATURE,


    // *** SYS ***

    /**
     * Get the computer system model.
     * example: Aspire E5-475 (version: V1.03)
     * example: VMware7,1
     */
    SYS_MODEL,

    /**
     * Get the computer system serial number, if available.
     * <P>
     * Performs a best-effort attempt to retrieve a unique serial number from the
     * computer system. This may originate from the baseboard, BIOS, processor,
     * hardware UUID, etc.
     * <P>
     * This value is provided for information only. Caution should be exercised if
     * using this result to "fingerprint" a system for licensing or other purposes,
     * as the result may change based on program permissions or installation of
     * software packages. Specifically, on Linux and FreeBSD, this requires either
     * root permissions, or installation of the (deprecated) HAL library (lshal
     * command). Linux also attempts to read the dmi/id serial number files in
     * sysfs, which are read-only root by default but may have permissions altered
     * by the user.
     *
     * return: the System Serial Number, if available, otherwise returns "unknown"
     * example: VMware-56 4d aa 6c 51 c8 aa 1e-09 e2 94 b4 97 2e 8e c8
     */
    SYS_SERIAL,

    /**
     * Get the computer system manufacturer.
     * example: Acer
     */
    SYS_MANUFACTURER,


    // *** BOARD ***
    /**
     * Get the baseboard model.
     * example: Ironman_SK
     */
    BOARD_MODEL,
    /**
     * Get the baseboard version.
     */
    BOARD_VERSION,
    /**
     * Get the baseboard manufacturer.
     * example: Acer
     */
    BOARD_MANUFACTURER,
    /**
     * Get the baseboard serial number.
     */
    BOARD_SERIAL,


    // *** MEM ***
    /**
     * The amount of actual physical memory, in bytes.
     * example: 12430843904
     */
    MEM_TOTAL,
    /**
     * The amount of physical memory currently available, in bytes.
     * example: 3345141760
     */
    MEM_AVAILABLE,


    // *** OS ***
    /**
     * Operating system family.
     * example: Ubuntu
     */
    OS_FAMILY,
    /**
     * Operating system version information.
     * example: 20.04.1 LTS (Focal Fossa) build 5.4.0-54-generic
     */
    OS_VERSION,
    /**
     * Gets the bitness (32 or 64) of the operating system.
     * example: 64
     */
    OS_BITNESS,
    /**
     * example: GNU/Linux
     */
    OS_MANUFACTURER,


    // *** JSON ***
    /**
     * Gets the json list of objects:
     * deviseId: Retrieves the card's Device ID
     * name: Retrieves the full name of the card.
     * vendor: The vendor of the card as human-readable text if possible, or the Vendor ID (VID) otherwise
     * versionInfo: Retrieves a list of version/revision data from the card. Users may need to further parse this list to identify specific GPU capabilities. A comma-delimited list of version/revision data.
     * vram: Retrieves the Video RAM (VRAM) available on the GPU. Total number of bytes.
     * example: [{"deviseId":"0x1214","name":"Skylake GT2 [HD Graphics 510]","vendor":"Intel Corporation (0x8086)","versionInfo":"unknown","vram":268435456}]
     */
    JSON_GPUS,
    /**
     * Gets the json list of objects:
     * name: The disk name
     * model: The disk model
     * serial: The disk serial number, if available.
     * size: The disk size, in bytes
     * example: [{"name":"/dev/sda","model":"TOSHIBA-TR100","serial":"14EA523BKBSN","size":480103981056}]
     */
    JSON_DISKS,
    /**
     * Gets the json list of objects:
     * name: The interface name
     * mac: The Media Access Control (MAC) address
     * display_name: The description of the network interface. On some platforms, this is identical to the name
     * example: [{"name":"docker0","mac":"02:26:57:00:1f:02","display_name":"docker0"},{"name":"wlp3s0","mac":"00:26:57:00:1f:02","display_name":"wlp3s0"}]
     */
    JSON_NETWORKS,
    /**
     * Gets the json list of objects:
     * edid: (Extended Display Identification Data) The original unparsed EDID byte array hex string
     * example: [{"edid":"00FFFFFFFFFFFF0006AF2D200000000000160104901D117802BC05A2554C9A250E5054000000010101010101010101010101010101011D3680A070381E4030208E0025A5100000181D3680087238664030208E0025A510000018000000FE0041554F0A202020202020202020000000FE004231333348414E30322E30200A0043"}]
     */
    JSON_DISPLAYS,
    /**
     * Gets the json list of objects:
     * name: Name of the power source at the Operating System level
     * device_name: Name of the power source at the device leve
     * serial: The battery's serial number.
     *   Some battery manufacturers encode the manufacture date in the serial number.
     *   Parsing this value is operating system and battery manufacturer dependent,
     *   and is left to the user.
     * example: [{"name":"BAT1","device_name":"AS23A4J","serial":"14351"},{"name":"hidpp_battery_0","device_name":"Wireless Mouse M560","serial":"412c-ba-cb-14-b6"}]
     */
    JSON_POWERS,

}
