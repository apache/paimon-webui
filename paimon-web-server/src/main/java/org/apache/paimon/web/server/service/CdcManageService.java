package org.apache.paimon.web.server.service;

import org.apache.paimon.web.server.data.dto.PageDto;
import org.apache.paimon.web.server.data.model.CdcManage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CdcManageService extends IService<CdcManage> {
    Page<CdcManage> queryTable(PageDto<Void> pageDto);

    CdcManage run(Long id);

    CdcManage stop(Long id);
}
