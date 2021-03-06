package com.marveliu.app.cms.modules.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.marveliu.framework.model.cms.Cms_site;
import com.marveliu.framework.services.base.BaseServiceImpl;
import com.marveliu.framework.services.cms.CmsSiteService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

@IocBean(args = {"refer:dao"})
@Service(interfaceClass = CmsSiteService.class)
@CacheDefaults(cacheName = "cms_site")
public class CmsSiteServiceImpl extends BaseServiceImpl<Cms_site> implements CmsSiteService {
    public CmsSiteServiceImpl(Dao dao) {
        super(dao);
    }

    @CacheResult
    @Override
    public Cms_site getSite(String code) {
        return this.fetch(Cnd.where("id", "=", code));
    }

    @CacheRemoveAll
    @Override
    public void clearCache() {

    }
}
