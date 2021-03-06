package com.marveliu.framework.services.library;


import com.marveliu.framework.model.library.lib_skill;
import com.marveliu.framework.model.library.lib_task;
import com.marveliu.framework.services.base.BaseService;
import org.nutz.mvc.annotation.Param;

import java.util.List;

/**
 * Created by wiz on 2016/12/22.
 */
public interface LibTaskService extends BaseService<lib_task> {
    List<lib_skill> getMenusAndButtons(String taskId);

    List<lib_skill> getDatas(String taskid);

    List<lib_skill> getDatas();

    void save(lib_task skill, String pid);

    void deleteAndChild(lib_task skill);;

    boolean editSkillsForTask(String skillIds,String taskid);
}
