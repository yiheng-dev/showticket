package com.showticket.show.controller;


import com.showticket.api.dto.ShowDTO;
import com.showticket.common.domain.PageDTO;
import com.showticket.show.domain.query.ShowPageQuery;
import com.showticket.show.service.IShowDocumentService;
import com.showticket.show.service.IShowSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "演出搜索相关接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final IShowSearchService showSearchService;
    private final IShowDocumentService showDocumentService;

    @ApiOperation("搜索演出")
    @GetMapping("/list")
    public PageDTO<ShowDTO> searchShows(ShowPageQuery query) {
        return showSearchService.search(query);
    }

    @ApiOperation("重建演出搜索索引")
    @PostMapping("/reindex")
    public long reindex() {
        return showDocumentService.reindex();
    }
}
