package org.apache.paimon.web.server.service;

import org.apache.paimon.web.server.data.dto.CdcJobDefinitionDTO;
import org.apache.paimon.web.server.data.model.CdcJobDefinition;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.paimon.web.server.data.result.R;

/**
* @author zhongyangyang
*/
public interface CdcJobDefinitionService extends IService<CdcJobDefinition> {

    R<Void> create(CdcJobDefinitionDTO cdcJobDefinitionDTO);
}
