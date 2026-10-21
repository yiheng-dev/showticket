package com.showticket.show.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.showticket.api.dto.ShowDTO;
import com.showticket.common.domain.PageDTO;
import com.showticket.common.utils.BeanUtils;
import com.showticket.show.domain.po.Show;
import com.showticket.show.domain.query.ShowPageQuery;
import com.showticket.show.service.IShowSearchService;
import com.showticket.show.service.IShowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowSearchServiceImpl implements IShowSearchService {

    private static final Map<String, String> SORT_FIELD_MAP = new HashMap<>();

    static {
        SORT_FIELD_MAP.put("update_time", "updateTime");
        SORT_FIELD_MAP.put("sold", "sold");
        SORT_FIELD_MAP.put("price", "price");
    }

    private final RestHighLevelClient client;
    private final IShowService showService;

    @Override
    public PageDTO<ShowDTO> search(ShowPageQuery query) {
        try {
            return searchByEs(query);
        } catch (Exception e) {
            log.warn("ES检索失败，降级为数据库查询", e);
            return searchByDb(query);
        }
    }

    private PageDTO<ShowDTO> searchByEs(ShowPageQuery query) throws IOException {
        // 1.构建bool查询：key走match全文检索，场馆/类型走term精确过滤，价格走range
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StrUtil.isNotBlank(query.getKey())) {
            boolQuery.must(QueryBuilders.matchQuery("name", query.getKey()));
        }
        if (StrUtil.isNotBlank(query.getVenue())) {
            boolQuery.filter(QueryBuilders.termQuery("venue", query.getVenue()));
        }
        if (StrUtil.isNotBlank(query.getGenre())) {
            boolQuery.filter(QueryBuilders.termQuery("genre", query.getGenre()));
        }
        if (query.getMaxPrice() != null) {
            RangeQueryBuilder range = QueryBuilders.rangeQuery("price").lte(query.getMaxPrice());
            if (query.getMinPrice() != null) {
                range.gte(query.getMinPrice());
            }
            boolQuery.filter(range);
        }
        // 2.构建搜索请求
        SearchRequest request = new SearchRequest(ShowDocumentServiceImpl.INDEX_NAME);
        SearchSourceBuilder source = request.source();
        source.query(boolQuery)
                .from(query.from())
                .size(query.getPageSize())
                .trackTotalHits(true)
                .sort(buildSort(query));
        // 3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        // 4.解析结果
        List<ShowDTO> list = new ArrayList<>(hits.getHits().length);
        for (SearchHit hit : hits.getHits()) {
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            list.add(BeanUtils.copyBean(sourceMap, ShowDTO.class));
        }
        long total = hits.getTotalHits() == null ? list.size() : hits.getTotalHits().value;
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        return new PageDTO<>(total, pages, list);
    }

    private PageDTO<ShowDTO> searchByDb(ShowPageQuery query) {
        Page<Show> result = showService.lambdaQuery()
                .like(StrUtil.isNotBlank(query.getKey()), Show::getName, query.getKey())
                .eq(StrUtil.isNotBlank(query.getVenue()), Show::getVenue, query.getVenue())
                .eq(StrUtil.isNotBlank(query.getGenre()), Show::getGenre, query.getGenre())
                .eq(Show::getStatus, 1)
                .between(query.getMaxPrice() != null, Show::getPrice, query.getMinPrice(), query.getMaxPrice())
                .page(query.toMpPage("update_time", false));
        return PageDTO.of(result, ShowDTO.class);
    }

    private SortBuilder<?> buildSort(ShowPageQuery query) {
        String field = SORT_FIELD_MAP.get(query.getSortBy());
        if (field != null) {
            boolean asc = Boolean.TRUE.equals(query.getIsAsc());
            return SortBuilders.fieldSort(field).order(asc ? SortOrder.ASC : SortOrder.DESC);
        }
        return SortBuilders.fieldSort("updateTime").order(SortOrder.DESC);
    }
}
