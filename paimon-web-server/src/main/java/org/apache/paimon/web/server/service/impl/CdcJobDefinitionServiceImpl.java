package org.apache.paimon.web.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.paimon.web.server.data.model.CdcJobDefinition;
import org.apache.paimon.web.server.service.CdcJobDefinitionService;
import org.apache.paimon.web.server.mapper.CdcJobDefinitionMapper;
import org.springframework.stereotype.Service;

/**
* @author zhongyangyang
*/
@Service
public class CdcJobDefinitionServiceImpl extends ServiceImpl<CdcJobDefinitionMapper, CdcJobDefinition>
    implements CdcJobDefinitionService{

}




