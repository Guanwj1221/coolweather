package com.example.administrator.coolweather.db;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/12/12
 */

public class Province extends DataSupport {
    private int id;
    private String provinceName;
    private int princeCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getPrinceCode() {
        return princeCode;
    }

    public void setPrinceCode(int princeCode) {
        this.princeCode = princeCode;
    }
}
