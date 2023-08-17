package org.apache.paimon.web.server.controller;

import org.apache.paimon.web.server.data.dto.PageDto;
import org.apache.paimon.web.server.data.model.CdcManage;
import org.apache.paimon.web.server.data.properties.FlinkEnv;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.service.CdcManageService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flinkAction")
@AllArgsConstructor
@CrossOrigin("*")
public class FlinkActionController {
    final CdcManageService cdcMangeService;
    final FlinkEnv flinkEnv;

    @PostMapping("run")
    public R<CdcManage> run(Long id) throws ClassNotFoundException {
        return R.succeed(cdcMangeService.run(id));
    }

    @PostMapping("stop")
    public R<CdcManage> stop(Long id) throws ClassNotFoundException {
        return R.succeed(cdcMangeService.stop(id));
    }

    @PutMapping("saveOrUpdate")
    public void saveOrUpdate(CdcManage cdcManage) {
        System.out.println(cdcManage);
    }

    @PostMapping("/query")
    public PageR<CdcManage> query(@RequestBody PageDto<Void> pageDto) {
        return PageR.pageToR(cdcMangeService.queryTable(pageDto));
    }

    @DeleteMapping("/deleteById")
    public R<Void> deleteById(Long id) {
        cdcMangeService.removeById(id);
        return R.succeed();
    }
}
