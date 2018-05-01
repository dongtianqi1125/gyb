package com.marveliu.app.web.modules.controllers.platform.gy;
/*
 * Copyright [2018] [Marveliu]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.marveliu.app.web.commons.utils.ShiroUtil;
import com.marveliu.framework.services.gy.GyAuthSubService;
import com.marveliu.framework.services.gy.GyFacadeService;
import com.marveliu.framework.services.gy.GyInfSubService;
import com.marveliu.framework.services.gy.GyPaySubService;
import com.marveliu.framework.services.sys.SysRoleService;
import com.marveliu.framework.services.sys.SysUserService;
import com.marveliu.framework.services.xm.XmApplyService;
import com.marveliu.framework.services.xm.XmInfService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;

/**
 * 负责和雇员端进行交互
 * 会被废弃，待采用前后端分离的模式
 *
 * @author Marveliu
 * @since 26/04/2018
 **/

@IocBean
@At("/platform/gy/person/")
public class GyPersonController {

    private static final Log log = Logs.get();

    @Inject
    @Reference
    private GyInfSubService gyInfSubSegyrvice;

    @Inject
    @Reference
    private GyAuthSubService gyAuthSubService;

    @Inject
    @Reference
    private GyPaySubService gyPaySubService;

    @Inject
    @Reference
    private GyFacadeService gyFacadeService;

    @Inject
    private XmInfService xmInfService;
    @Inject
    private XmApplyService xmApplyService;

    @Inject
    @Reference
    private SysRoleService sysRoleService;

    @Inject
    @Reference
    private SysUserService sysuserService;


    @Inject
    private Dao dao;
    @Inject
    private ShiroUtil shiroUtil;


    // 注解邮件发送
    // @Inject
    // private EmailController emailController;


    /**
     * 雇员登录
     * 雇员有且仅有一个平台角色
     *
     * @param req
     */
    // @At("")
    // @Ok("re:beetl:/platform/gy/person/index.html")
    // @RequiresPermissions("gy.person")
    // public String index(HttpServletRequest req) {
    //     //获得当前登录用户
    //     Cnd cnd = Cnd.NEW();
    //     Sys_user user = shiroUtil.getSysuser();
    //     req.setAttribute("obj", user);
    //     // 是否为未注册雇员
    //     if (shiroUtil.hasAnyRoles("gy1")) {
    //         return "beetl:/platform/gy/person/reginfo.html";
    //     }
    //     // 显示雇员当前角色
    //     req.setAttribute("role", user.getRoles().get(0));
    //
    //     gy_inf gy = gyInfSubService.getGyByUserId(user.getId());
    //     gy_auth auth = gyAuthSubService.getGyAuthByGyid(gy.getId());
    //     int apply = xmApplyService.count(Cnd.where("gyid", "=", gy.getId()));
    //     int doing = xmInfService.count(Cnd.where("status", "=", 0).and("gyid", "=", gy.getId()));
    //     int done = xmInfService.count(Cnd.where("status", "=", 1).and("gyid", "=", gy.getId()));
    //     int finish = xmInfService.count(Cnd.where("status", ">", 1).and("gyid", "=", gy.getId()));
    //
    //
    //     // 返回参数
    //     req.getSession().setAttribute("gyid", gy.getId());
    //     // 是否认证
    //     req.setAttribute("isAuth", gyAuthSubService.isAuth(gy.getId()));
    //     // 认证信息
    //     req.setAttribute("gyauth", auth);
    //     // 基础信息能否修改
    //     req.setAttribute("gyinfModify", gyFacadeService.infCheckable(gy.getId()));
    //     // 雇员信息
    //     req.setAttribute("gy", gy);
    //     // 申请信息
    //     req.setAttribute("apply", apply);
    //     // 进行中，完成，结算的任务数目
    //     req.setAttribute("doing", doing);
    //     req.setAttribute("finish", finish);
    //     req.setAttribute("final", done);
    //
    //     return null;
    // }
    //
    //
    // /**
    //  * @function: 雇员注册
    //  * @param:
    //  * @return:
    //  * @note: 仅仅是注册基本的账号信息
    //  */
    // @At
    // @Ok("json")
    // @SLog(type="gy",tag = "雇员信息完善", msg = "用户名:${userid}")
    // @AdaptBy(type = WhaleAdaptor.class)
    // public Object reginfo(
    //         @Param("userid") String userid,
    //         @Param("::gyinf.") gy_inf gyinf,
    //         @Param("::gyauth.") gy_auth gyauth,
    //         @Param("birthdayat") String birthday,
    //         @Param("regYearat") String regyear,
    //         HttpServletRequest req) {
    //     try {
    //
    //         if (gyFacadeService..checkGyRegByUsrid(userid)) {
    //             return Result.error("你注册了雇员信息！");
    //         }
    //
    //         //日期登记
    //         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //         SimpleDateFormat sdfn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    //         int birthdayat = (int) (sdf.parse(birthday).getTime() / 1000);
    //         int regyearat = (int) (sdf.parse(regyear).getTime() / 1000);
    //         int now = (int) (sdfn.parse(DateUtil.getDateTime()).getTime() / 1000);
    //         gyinf.setRegYear(regyearat);
    //         gyinf.setBirthday(birthdayat);
    //         gyauth.setReAuthTime(now);
    //         try {
    //             // 事务操作：插入用户与绑定角色,并且初始化雇员编号信息，雇员认证信息
    //             Trans.exec(new Atom() {
    //                 @Override
    //                 public void run() {
    //                     gyinf.setUserid(userid);
    //                     // 雇员认证信息
    //                     // 雇员基本信息
    //                     gyauth.setGyid(gyInfService.insertOrUpdate(gyinf).getId());
    //                     gyAuthService.insertOrUpdate(gyauth);
    //                     // 修改角色信息为gy2
    //                     gyService.updateGyRole(userid, "gy2");
    //                     // 更新shiro信息
    //                     Subject currentUser = SecurityUtils.getSubject();
    //                     CaptchaToken token = (CaptchaToken) req.getSession().getAttribute("sysUserToken");
    //                     // TODO: 2018/1/7 0007 重复login，应该会创造两个session
    //                     currentUser.login(token);
    //                     // 发送邮件
    //                     Thread t = new Thread(new Runnable() {
    //                         public void run() {
    //                             emailController.activeMail(StringUtil.getUid());
    //                         }
    //                     });
    //                     t.start();
    //                 }
    //             });
    //
    //         } catch (Exception e) {
    //             log.debug(e);
    //         }
    //         return Result.success("system.success");
    //     } catch (Exception e) {
    //         return Result.error("system.error");
    //     }
    // }
    //
    //
    // //  个人信息修改
    // @At("/infedit")
    // @Ok("beetl:/platform/gy/person/infedit.html")
    // @RequiresPermissions("gy.person")
    // public void infedit(HttpServletRequest req) {
    //     Cnd cnd = Cnd.NEW();
    //
    //     String gyid = StringUtil.getGyid();
    //     gy_inf gy = gyInfService.fetch(gyid);
    //
    //     req.setAttribute("gy", gy);
    //     req.setAttribute("email", gyInfService.getUserByGyid(gyid).getEmail());
    // }
    //
    // //  提交个人信息修改
    // @At("/infeditDo")
    // @Ok("json")
    // @RequiresPermissions("gy.person")
    // @SLog(tag = "雇员信息修改", msg = "${args[0].id}")
    // public Object infeditDo(
    //         @Param("..") gy_inf gyInf,
    //         @Param("email") String email,
    //         @Param("birthdayat") String birthday,
    //         @Param("qq") String qq,
    //         @Param("regYearat") String regyear,
    //         HttpServletRequest req) {
    //     String userid = StringUtil.getUid();
    //     gy_inf gy = gyInfService.getGyByUserId(userid);
    //     Sys_user user = sysuserService.fetch(userid);
    //
    //     //过滤字段更新QQ
    //     FieldFilter ff = FieldFilter.create(gy_inf.class, "^qq$");
    //     ff.run(new Atom() {
    //         public void run() {
    //             gy.setQq(qq);
    //             dao.update(gy);
    //         }
    //     });
    //
    //     try {
    //
    //         if (!gyService.infCheckable(StringUtil.getGyid())) {
    //             if (!gy.getQq().equals(qq)) {
    //                 return Result.success("你的qq已经成功修改为" + qq);
    //             }
    //             return Result.error("在身份审核及审核完成阶段，只可以修改个人qq");
    //         }
    //
    //         // 邮箱修改
    //
    //
    //         // 验证邮箱是否修改
    //         if (email == user.getEmail()) {
    //             gyService.changeEmail(gyInf.getId(), user.getEmail());
    //             user = new Sys_user();
    //             user.setId(userid);
    //             user.setOpBy(StringUtil.getUid());
    //             user.setOpAt((int) (System.currentTimeMillis() / 1000));
    //             sysuserService.updateIgnoreNull(user);
    //         }
    //
    //
    //         // 检验身份认证
    //         if (!gyService.checkGyAuthByUsrid(gyInf.getId())) {
    //             //修改雇员信息
    //             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //             int birthdayat = (int) (sdf.parse(birthday).getTime() / 1000);
    //             int regyearat = (int) (sdf.parse(regyear).getTime() / 1000);
    //             gyInf.setRegYear(regyearat);
    //             gyInf.setBirthday(birthdayat);
    //             gyInf.setOpBy(StringUtil.getGyid());
    //             gyInf.setOpAt((int) (System.currentTimeMillis() / 1000));
    //             gyInfService.updateIgnoreNull(gyInf);
    //             return Result.success("system.success");
    //         }
    //
    //         String msg = "你的身份已经认证，只修改了您的联系信息";
    //
    //
    //         return Result.success(msg + "system.success");
    //
    //     } catch (Exception e) {
    //         log.debug(e);
    //         return Result.error("system.error");
    //     }
    // }
    //
    //
    // // 身份信息修改
    // @At("/authedit")
    // @Ok("beetl:/platform/gy/person/authedit.html")
    // @RequiresPermissions("gy.person")
    // public void authedit(HttpServletRequest req) {
    //     Object auth = gyAuthService.getGyAuthByGyid(userInfUtil.getCurrentGyid());
    //     req.setAttribute("obj", auth);
    // }
    //
    // // 提交身份信息修改
    // @At("/autheditDo")
    // @Ok("json")
    // @RequiresPermissions("gy.person")
    // @AdaptBy(type = WhaleAdaptor.class)
    // @SLog(tag = "雇员身份信息修改", msg = "")
    // public Object autheditDo(
    //         @Param("..") gy_auth gyauth,
    //         HttpServletRequest req) {
    //     try {
    //         gyauth.setStatus(1);
    //         gyauth.setOpBy(StringUtil.getUid());
    //         gyauth.setOpAt((int) (System.currentTimeMillis() / 1000));
    //         gyAuthService.updateIgnoreNull(gyauth);
    //         Thread t = new Thread(new Runnable() {
    //             @Override
    //             public void run() {
    //                 gyService.refreshGy(StringUtil.getGyid());
    //             }
    //         });
    //         t.run();
    //         return Result.success("system.success");
    //     } catch (Exception e) {
    //         return Result.error("system.error");
    //     }
    // }
    //
    //
    // //支付方式修改
    // @At("payindex")
    // @Ok("beetl:/platform/gy/person/payindex.html")
    // @RequiresPermissions("gy.person")
    // public void payindex(HttpServletRequest req) {
    // }
    //
    // // 支付方式列表
    // @At("/paydata")
    // @Ok("json")
    // @RequiresPermissions("gy.person")
    // public Object paydata(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
    //
    //     Cnd cnd = Cnd.NEW();
    //     Subject currentUser = SecurityUtils.getSubject();
    //     Sys_user user = (Sys_user) currentUser.getPrincipal();
    //     gy_inf gy = gyInfService.fetch(Cnd.where("userid", "=", user.getId()));
    //     cnd.and("gyid", "=", gy.getId());
    //     return gyPayService.data(length, start, draw, order, columns, cnd, null);
    // }
    //
    // // 支付方式添加界面
    // @At("/payadd")
    // @Ok("beetl:/platform/gy/person/payadd.html")
    // @RequiresPermissions("gy.person")
    // public void payadd() {
    //
    // }
    //
    // // 添加支付方式
    // @At("/payaddDo")
    // @Ok("json")
    // @RequiresPermissions("gy.person")
    // @SLog(tag = "gy_pay", msg = "${args[0].id}")
    // public Object payaddDo(@Param("..") gy_pay gyPay, HttpServletRequest req) {
    //     try {
    //         Cnd cnd = Cnd.NEW();
    //         Subject currentUser = SecurityUtils.getSubject();
    //         Sys_user user = (Sys_user) currentUser.getPrincipal();
    //         gy_inf gy = gyInfService.fetch(cnd.and("userid", "=", user.getId()));
    //
    //         //检查是否已经添加了
    //         if (null != gyPayService.fetch(Cnd.where("gyid", "=", gy.getId())
    //                 .and("type", "=", gyPay.getType())
    //                 .and("payid", "=", gyPay.getPayid()))) {
    //             return Result.error("支付方式已经存在，请勿重复提交");
    //         }
    //
    //
    //         //检查是否未首要支付方式
    //         if (gyPay.isFirst()) {
    //             //取消其他首要支付
    //             gyPayService.update(Chain.make("first", false), Cnd.where("gyid", "=", gy.getId()).and("first", "=", true));
    //         }
    //
    //         gyPay.setGyid(gy.getId());
    //         gyPayService.insert(gyPay);
    //
    //
    //         return Result.success("system.success");
    //     } catch (Exception e) {
    //         return Result.error("system.error");
    //     }
    // }
    //
    // @At("/payedit/?")
    // @Ok("beetl:/platform/gy/person/payedit.html")
    // @RequiresPermissions("gy.person")
    // public void payedit(
    //         String payid,
    //         HttpServletRequest req) {
    //     gy_inf gy = gyInfService.getGyByUserId(StringUtil.getUid());
    //     req.setAttribute("gy", gy);
    //     req.setAttribute("obj", gyPayService.fetch(payid));
    //     //req.setAttribute("obj", g(StringUtil.getGyid()));
    // }
    //
    // @At("/payeditDo")
    // @Ok("json")
    // @RequiresPermissions("gy.person")
    // @SLog(tag = "gy_pay", msg = "${args[0].id}")
    // public Object payeditDo(@Param("..") gy_pay gyPay, HttpServletRequest req) {
    //     try {
    //         //检查是否未首要支付方式
    //         if (gyPay.isFirst()) {
    //             gyPayService.update(Chain.make("first", false), Cnd.where("gyid", "=", gyPay.getGyid()).and("first", "=", true));
    //         }
    //
    //         gyPay.setOpBy(StringUtil.getUid());
    //         gyPay.setOpAt((int) (System.currentTimeMillis() / 1000));
    //         gyPayService.updateIgnoreNull(gyPay);
    //         return Result.success("system.success");
    //     } catch (Exception e) {
    //         return Result.error("system.error");
    //     }
    // }
    //
    // @At({"/paydelete/?", "/paydelete"})
    // @Ok("json")
    // @RequiresPermissions("gy.person")
    // @SLog(tag = "gy_pay", msg = "${req.getAttribute('id')}")
    // public Object paydelete(@Param("id") String id, @Param("ids") String[] ids, HttpServletRequest req) {
    //     try {
    //         if (ids != null && ids.length > 0) {
    //             gyPayService.delete(ids);
    //             req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
    //         } else {
    //             gyPayService.delete(id);
    //             req.setAttribute("id", id);
    //         }
    //         return Result.success("system.success");
    //     } catch (Exception e) {
    //         return Result.error("system.error");
    //     }
    // }
    //
    // @At("/paydetail/?")
    // @Ok("beetl:/platform/gy/person/paydetail.html")
    // @RequiresPermissions("gy.person")
    // public void paydetail(String id, HttpServletRequest req) {
    //     if (!Strings.isBlank(id)) {
    //         req.setAttribute("obj", gyPayService.fetch(id));
    //     } else {
    //         req.setAttribute("obj", null);
    //     }
    // }
    //
    // @At("/payselect")
    // @Ok("beetl:/platform/gy/pay/payselect.html")
    // @RequiresPermissions("gy.person")
    // public void payselect() {
    // }


}