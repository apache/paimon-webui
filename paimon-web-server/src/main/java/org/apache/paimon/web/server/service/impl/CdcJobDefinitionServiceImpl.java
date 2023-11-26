package org.apache.paimon.web.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.paimon.web.server.data.dto.CdcJobDefinitionDTO;
import org.apache.paimon.web.server.data.model.CdcJobDefinition;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.service.CdcJobDefinitionService;
import org.apache.paimon.web.server.mapper.CdcJobDefinitionMapper;
import org.springframework.stereotype.Service;

/**
* @author zhongyangyang
*/
@Service
public class CdcJobDefinitionServiceImpl extends ServiceImpl<CdcJobDefinitionMapper, CdcJobDefinition>
    implements CdcJobDefinitionService{

    @Override
    public R<Void> create(CdcJobDefinitionDTO cdcJobDefinitionDTO) {
        String title = cdcJobDefinitionDTO.getTitle();
        QueryWrapper<CdcJobDefinition> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title",title);
        if(baseMapper.exists(queryWrapper)){
            return R.failed(Status.CDC_EXIST_ERROR);
        }
        CdcJobDefinition cdcJobDefinition = CdcJobDefinition.builder().title(cdcJobDefinitionDTO.getTitle())
                .build();
        baseMapper.insert(cdcJobDefinition);
        return R.succeed();
    }
}




