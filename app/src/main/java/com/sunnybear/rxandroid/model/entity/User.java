package com.sunnybear.rxandroid.model.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录model
 */
public class User implements Serializable {
    private Long id;// 自增型ID
    private String userid;
    private String mobileLoginNumber;// 员工编号
    private String userLoginNumber;// 员工登陆用帐号
    private String userLoginPasswd;// 员工登陆用密码
    private String userName;// 员工姓名
    private String userEngName;// 员工英文名
    private String supervisorNumber;// 上级编号
    private String userEmailAddress;// 员工邮箱地址
    private String userContactInfo;// 员工联系方式
    private String userStatus;// 员工状态  active or inactive-  inactive 时，如果登陆，则删除相关数据
    private String userPasswdFlag;// Y- 必须修改密码，初次使用或者重置密码后， N- 无需强制修改密码
    private String createBy;// 创建人员
    private String createDate;// 创建时间
    private String updateBy;// 修改人员
    private String updateDate;// 修改时间
    private Date mobileLogTime;//离线状态下手机端操作发生的时间
    private String allRoleName;// 该user拥有的role字符串，每个role之间用"&"间隔
    private String statusFlag;// 员工状态  active or inactive-  inactive 时，如果登陆，则删除相关数据

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userid='" + userid + '\'' +
                ", mobileLoginNumber='" + mobileLoginNumber + '\'' +
                ", userLoginNumber='" + userLoginNumber + '\'' +
                ", userLoginPasswd='" + userLoginPasswd + '\'' +
                ", userName='" + userName + '\'' +
                ", userEngName='" + userEngName + '\'' +
                ", supervisorNumber='" + supervisorNumber + '\'' +
                ", userEmailAddress='" + userEmailAddress + '\'' +
                ", userContactInfo='" + userContactInfo + '\'' +
                ", userStatus='" + userStatus + '\'' +
                ", userPasswdFlag='" + userPasswdFlag + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createDate='" + createDate + '\'' +
                ", updateBy='" + updateBy + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", mobileLogTime=" + mobileLogTime +
                ", allRoleName='" + allRoleName + '\'' +
                ", statusFlag='" + statusFlag + '\'' +
                '}';
    }

    public String getStatusFlag() {
        return this.statusFlag;
    }

    public void setStatusFlag(String statusFlag) {
        this.statusFlag = statusFlag;
    }

    public String getAllRoleName() {
        return this.allRoleName;
    }

    public void setAllRoleName(String allRoleName) {
        this.allRoleName = allRoleName;
    }

    public Date getMobileLogTime() {
        return this.mobileLogTime;
    }

    public void setMobileLogTime(Date mobileLogTime) {
        this.mobileLogTime = mobileLogTime;
    }

    public String getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUserPasswdFlag() {
        return this.userPasswdFlag;
    }

    public void setUserPasswdFlag(String userPasswdFlag) {
        this.userPasswdFlag = userPasswdFlag;
    }

    public String getUserStatus() {
        return this.userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserContactInfo() {
        return this.userContactInfo;
    }

    public void setUserContactInfo(String userContactInfo) {
        this.userContactInfo = userContactInfo;
    }

    public String getUserEmailAddress() {
        return this.userEmailAddress;
    }

    public void setUserEmailAddress(String userEmailAddress) {
        this.userEmailAddress = userEmailAddress;
    }

    public String getSupervisorNumber() {
        return this.supervisorNumber;
    }

    public void setSupervisorNumber(String supervisorNumber) {
        this.supervisorNumber = supervisorNumber;
    }

    public String getUserEngName() {
        return this.userEngName;
    }

    public void setUserEngName(String userEngName) {
        this.userEngName = userEngName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserLoginPasswd() {
        return this.userLoginPasswd;
    }

    public void setUserLoginPasswd(String userLoginPasswd) {
        this.userLoginPasswd = userLoginPasswd;
    }

    public String getUserLoginNumber() {
        return this.userLoginNumber;
    }

    public void setUserLoginNumber(String userLoginNumber) {
        this.userLoginNumber = userLoginNumber;
    }

    public String getMobileLoginNumber() {
        return this.mobileLoginNumber;
    }

    public void setMobileLoginNumber(String mobileLoginNumber) {
        this.mobileLoginNumber = mobileLoginNumber;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
