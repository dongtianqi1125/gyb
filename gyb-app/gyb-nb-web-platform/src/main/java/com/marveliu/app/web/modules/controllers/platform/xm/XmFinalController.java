package com.marveliu.app.web.modules.controllers.platform.xm;
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

import com.alibaba.dubbo.config.annotation.Reference;
import com.marveliu.app.web.commons.slog.annotation.SLog;
import com.marveliu.app.web.commons.utils.ShiroUtil;
import com.marveliu.app.web.commons.utils.StringUtil;
import com.marveliu.framework.model.base.Result;
import com.marveliu.framework.model.xm.xm_feedback;
import com.marveliu.framework.page.datatable.DataTableColumn;
import com.marveliu.framework.page.datatable.DataTableOrder;
import com.marveliu.framework.services.gy.GyPayService;
import com.marveliu.framework.services.sys.SysUserinfService;
import com.marveliu.framework.services.xm.*;
import com.marveliu.framework.util.ConfigUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Marveliu
 * @since 08/05/2018
 **/
@IocBean
@At("/platform/xm/final")
public class XmFinalController {
    private static final Log log = Logs.get();

    @Inject
    @Reference
    private XmTaskService xmTaskService;

    @Inject
    @Reference
    private XmInfService xmInfService;


    @Inject
    @Reference
    private XmFeedbackService xmFeedbackService;


    @Inject
    @Reference
    private XmEvaluationService xmEvaluationService;

    @Inject
    @Reference
    private XmBillService xmBillService;

    @Inject
    @Reference
    private XmFacadeService xmFacadeService;

    @Inject
    @Reference
    private GyPayService gyPayService;

    @Inject
    @Reference
    private SysUserinfService sysUserinfService;

    @Inject
    private ShiroUtil shiroUtil;

    @At("")
    @Ok("beetl:/platform/xm/final/index.html")
    @RequiresPermissions("platform.xm.final")
    public void index(HttpServletRequest req) {
        int total = 0;
        int doing = 0;
        int done = 0;
        int finish = 0;
        total = xmInfService.count();
        doing = xmInfService.count(Cnd.where("status", "=", 0));
        done = xmInfService.count(Cnd.where("status", "=", 1));
        finish = xmInfService.count(Cnd.where("status", ">", 1));
        req.setAttribute("total", total);
        req.setAttribute("doing", doing);
        req.setAttribute("final", done);
        req.setAttribute("finish", finish);
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.xm.final")
    public Object data(
            @Param("xminfid") String xminfd,
            @Param("xminfstatus") int status,
            @Param("length") int length,
            @Param("start") int start,
            @Param("draw") int draw,
            @Param("::order") List<DataTableOrder> order,
            @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        cnd.and("status", ">=", ConfigUtil.XM_INF_DONE);
        if (!Strings.isEmpty(xminfd)) {
            cnd.and("id", "=", xminfd);
        }

        if (status != 0) {
            cnd.and("status", "=", status);
        }

        if (!shiroUtil.isSuper()) {
            cnd.and("author", "=", sysUserinfService.getSysuserinfid(StringUtil.getPlatformUid()));
        }
        return xmInfService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/detail/?")
    @Ok("beetl:/platform/xm/final/detail.html")
    @RequiresPermissions("platform.xm.final")
    // @SLog(type = "xm", tag = "", msg = "")
    public void detail(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            req.setAttribute("obj", xmInfService.fetch(Cnd.where("id", "=", id)));
        } else {
            req.setAttribute("obj", null);
        }
    }

    @At("/item/?")
    @Ok("beetl:/platform/xm/final/item.html")
    @RequiresPermissions("platform.xm.final")
    public void item(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            req.setAttribute("obj", xmInfService.fetch(id));
        } else {
            req.setAttribute("obj", null);
        }
    }

    @At("/commit")
    @Ok("json")
    @RequiresPermissions("platform.xm.final")
    @SLog(type = "xm", tag = "后台项目结算", msg = "结算任务编号${args[0]},评分:${args[1]},结算金额:${args[2]}")
    public Object commit(
            @Param("id") String id,
            @Param("grade") float grade,
            @Param("paysum") float paysum,
            @Param("evanote") String evanote,
            @Param("billnote") String billnote,
            HttpServletRequest req) {
        try {
            if (xmFacadeService.initXmFinal(id, grade, paysum, evanote, billnote, StringUtil.getPlatformUid())) {
                return Result.success("system.success");
            }
        } catch (Exception e) {
            return Result.error("system.error");
        }
        return Result.error("操作失败，请注意不能重复进行结算！");
    }


    /**
     * 强制打开一个已经完结的任务
     *
     * @param xminfid
     * @return
     */
    @At("/reset")
    @Ok("json")
    @RequiresPermissions("platform.xm.inf")
    @SLog(type = "xm", tag = "项目重置", msg = "重置编号${args[0]}")
    public Object reset(
            @Param("xminfid") String xminfid) {

        if (Strings.isNotBlank(xminfid)) {
            if (!shiroUtil.isSuper()) {
                return Result.error("你没有权限");
            }
            try {
                Trans.exec(
                        new Atom() {
                            @Override
                            public void run() {
                                String regexid = "%"+xminfid.split("_")[1];
                                Cnd cnd = Cnd.where("id","like",regexid);
                                xmTaskService.update(Chain.make("status",ConfigUtil.XM_TASK_DOING),cnd);
                                xmInfService.update(Chain.make("status",ConfigUtil.XM_INF_DOING),cnd);
                                xm_feedback xmFeedback = xmFeedbackService.getLatestXmfeedback(xminfid);
                                xmFeedback.setStatus(ConfigUtil.XM_FEEDBACK_CHECKING);
                                xmFeedbackService.update(xmFeedback);
                                xmBillService.update(Chain.make("status",ConfigUtil.XM_BILL_INIT),cnd);
                            }
                        }
                );
                return  Result.success("system.success");
            } catch (Exception e) {
                log.error("重新打开完结任务失败",e);
            }
        }
        return Result.error("system.error");
    }


    /**
     * 强制删除一个任务
     *
     * @param xminfid
     * @return
     */
    @At("/clear")
    @Ok("json")
    @RequiresPermissions("platform.xm.inf")
    @SLog(type = "xm", tag = "项目删除", msg = "删除编号${args[0]}")
    public Object clear(
            @Param("xminfid") String xminfid) {

        if (Strings.isNotBlank(xminfid)) {
            if (!shiroUtil.isSuper()) {
                return Result.error("你没有权限");
            }
            try {
                Trans.exec(
                        new Atom() {
                            @Override
                            public void run() {
                                String regexid = "%"+xminfid.split("_")[1];
                                Cnd cnd = Cnd.where("id","like",regexid);
                                xmTaskService.clear(cnd);
                                xmInfService.clear(cnd);
                                xmFeedbackService.clear(Cnd.where("xminfid","=",xminfid));
                                xmBillService.clear(cnd);
                                xmEvaluationService.clear(cnd);
                            }
                        }
                );
                return  Result.success("system.success");
            } catch (Exception e) {
                log.error("重新打开完结任务失败",e);
            }
        }
        return Result.error("system.error");
    }


}
