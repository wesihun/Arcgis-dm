package com.wt.arcgis.pojo;

import java.util.Date;

public class TB_YJJBNT {//行政区表（记录服务更新的版本和地址以TB_开头的都是）
    private int id;
    private String serviceaddr;
    private String databasename;
    private String tablename;
    private int type;
    private Date updatetime;
    private int run;
    private Date createtime;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceaddr() {
        return serviceaddr;
    }

    public void setServiceaddr(String serviceaddr) {
        this.serviceaddr = serviceaddr;
    }

    public String getDatabasename() {
        return databasename;
    }

    public void setDatabasename(String databasename) {
        this.databasename = databasename;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public int getRun() {
        return run;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}
