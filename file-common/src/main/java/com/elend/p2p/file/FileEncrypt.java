package com.elend.p2p.file;


/**
 * 文件是否加密标识
 * @author liyongquan
 *
 */
public enum FileEncrypt {
    NO((short)0, "不加密"),
    YES((short)1,"加密");
    private short value;
    private String desc;
    private FileEncrypt(short value, String desc) {
        this.setValue(value);
        this.desc = desc;
    }
    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public static FileEncrypt from(short value) {
        for (FileEncrypt one : values()) {
            if (one.getValue() == value) {
                return one;
            }
        }
        throw new IllegalArgumentException("illegal type:"+ value);
    }
}
