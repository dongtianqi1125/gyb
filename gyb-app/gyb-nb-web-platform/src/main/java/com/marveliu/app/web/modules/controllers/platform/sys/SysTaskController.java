package com.marveliu.app.web.modules.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.marveliu.app.web.commons.slog.annotation.SLog;
import com.marveliu.app.web.commons.utils.StringUtil;
import com.marveliu.framework.model.base.Result;
import com.marveliu.framework.model.sys.Sys_task;
import com.marveliu.framework.page.datatable.DataTableColumn;
import com.marveliu.framework.page.datatable.DataTableOrder;
import com.marveliu.framework.services.sys.SysTaskService;
import com.marveliu.framework.services.task.TaskPlatformService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/sys/tmsg")
public class SysTaskController {
    private static final Log log = Logs.get();

    @Inject
    @Reference
    private SysTaskService sysTaskService;

    @Inject
    @Reference
    private TaskPlatformService taskPlatformService;

    @At("")
    @Ok("beetl:/platform/sys/tmsg/index.html")
    @RequiresPermissions("sys.manager.tmsg")
    public void index() {

    }

    @At
    @Ok("json:full")
    @RequiresPermissions("sys.manager.tmsg")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        return sysTaskService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/sys/tmsg/add.html")
    @RequiresPermissions("sys.manager.tmsg")
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建任务", msg = "任务名:${args[0].name}")
    @RequiresPermissions("sys.manager.tmsg.add")
    public Object addDo(@Param("..") Sys_task task, HttpServletRequest req) {
        try {
            Sys_task sysTask=sysTaskService.insert(task);
            taskPlatformService.addCron(sysTask.getId(), sysTask.getId(), sysTask.getJobClass(), sysTask.getCron(),
                    sysTask.getNote(), sysTask.getData());
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/sys/tmsg/edit.html")
    @RequiresPermissions("sys.manager.tmsg")
    public Object edit(String id) {
        return sysTaskService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改任务", msg = "任务名:${args[0].name}")
    @RequiresPermissions("sys.manager.tmsg.edit")
    public Object editDo(@Param("..") Sys_task task, HttpServletRequest req) {
        try {
            task.setOpBy(StringUtil.getPlatformUid());
            task.setOpAt(Times.getTS());
            sysTaskService.updateIgnoreNull(task);
            if (taskPlatformService.isExist(task.getId(), task.getId())) {
                taskPlatformService.delete(task.getId(), task.getId());
            }
            if (!task.isDisabled()) {
                taskPlatformService.addCron(task.getId(), task.getId(), task.getJobClass(), task.getCron(),
                        task.getNote(), task.getData());
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }


    @At({"/delete", "/delete/?"})
    @Ok("json")
    @SLog(tag = "删除任务", msg = "任务ID:${args[2].getAttribute('id')}")
    @RequiresPermissions("sys.manager.tmsg.delete")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                List<Sys_task> taskList = sysTaskService.query(Cnd.where("id", "in", ids));
                for (Sys_task sysTask : taskList) {
                    try {
                        if (taskPlatformService.isExist(sysTask.getId(), sysTask.getId())) {
                            taskPlatformService.delete(sysTask.getId(), sysTask.getId());
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
                sysTaskService.delete(ids);
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
                Sys_task sysTask = sysTaskService.fetch(id);
                try {
                    if (taskPlatformService.isExist(sysTask.getId(), sysTask.getId())) {
                        taskPlatformService.delete(sysTask.getId(), sysTask.getId());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                sysTaskService.delete(id);
                req.setAttribute("id", id);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/enable/?")
    @Ok("json")
    @RequiresPermissions("sys.manager.tmsg.edit")
    @SLog(tag = "启用任务", msg = "任务名:${args[1].getAttribute('name')}")
    public Object enable(String id, HttpServletRequest req) {
        try {
            Sys_task sysTask = sysTaskService.fetch(id);
            req.setAttribute("name", sysTask.getName());
            sysTaskService.update(org.nutz.dao.Chain.make("disabled", false), Cnd.where("id", "=", id));
            if (!taskPlatformService.isExist(sysTask.getId(), sysTask.getId())) {
                taskPlatformService.addCron(sysTask.getId(), sysTask.getId(), sysTask.getJobClass(), sysTask.getCron(),
                        sysTask.getNote(), sysTask.getData());
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/disable/?")
    @Ok("json")
    @RequiresPermissions("sys.manager.tmsg.edit")
    @SLog(tag = "禁用任务", msg = "任务名:${args[1].getAttribute('name')}")
    public Object disable(String id, HttpServletRequest req) {
        try {
            Sys_task sysTask = sysTaskService.fetch(id);
            req.setAttribute("name", sysTask.getName());
            sysTaskService.update(org.nutz.dao.Chain.make("disabled", true), Cnd.where("id", "=", id));
            if (taskPlatformService.isExist(sysTask.getId(), sysTask.getId())) {
                taskPlatformService.delete(sysTask.getId(), sysTask.getId());
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
}
