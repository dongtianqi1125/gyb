package com.marveliu.framework.model.sys;

import com.marveliu.framework.model.base.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by 89792 on 2017/11/10 0010.
 */

//系统员工
@Table("sys_userinf")
public class Sys_userinf extends BaseModel implements Serializable {

    @Column
    @Name
    @Comment("员工编号")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String id;

    @Column
    @Comment("登陆名")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String userid;

    @Column
    @Comment("真实姓名")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String realname;

    @Column
    @Comment("qq")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String qq;

    @Column
    @Comment("电话")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String phone;

    @Column
    @Comment("出生日期")
    @ColDefine(type = ColType.INT)
    private Integer birthday;

    @Column
    @Comment("性别")
    @ColDefine(type = ColType.INT)
    private Integer sex;

    @Column
    @Comment("状态")
    @ColDefine(type = ColType.INT)
    private int status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getBirthday() {
        return birthday;
    }

    public void setBirthday(Integer birthday) {
        this.birthday = birthday;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
