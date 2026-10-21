package com.showticket.show.config;

import com.showticket.show.service.IShowDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShowSearchInitRunner implements ApplicationRunner {

    private final IShowDocumentService documentService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            documentService.createIndexIfNotExists();
            if (documentService.count() > 0) {
                log.info("ES索引已有数据，跳过导入");
                return;
            }
            documentService.importAll();
        } catch (Exception e) {
            log.warn("ES初始化失败，搜索将降级为数据库查询", e);
        }
    }
}
