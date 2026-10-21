package com.showticket.show.service.impl;

import com.showticket.show.domain.po.Show;
import com.showticket.show.service.IShowDocumentService;
import com.showticket.show.service.IShowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowDocumentServiceImpl implements IShowDocumentService {

    public static final String INDEX_NAME = "shows";

    private final RestHighLevelClient client;
    private final IShowService showService;

    @Override
    public void createIndexIfNotExists() {
        try {
            if (client.indices().exists(new GetIndexRequest(INDEX_NAME), RequestOptions.DEFAULT)) {
                return;
            }
            CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
            request.source(MAPPING_TEMPLATE, XContentType.JSON);
            client.indices().create(request, RequestOptions.DEFAULT);
            log.info("ES索引[{}]创建成功", INDEX_NAME);
        } catch (IOException e) {
            log.error("创建ES索引[{}]失败", INDEX_NAME, e);
        }
    }

    @Override
    public long reindex() {
        try {
            if (client.indices().exists(new GetIndexRequest(INDEX_NAME), RequestOptions.DEFAULT)) {
                client.indices().delete(new DeleteIndexRequest(INDEX_NAME), RequestOptions.DEFAULT);
            }
            CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
            request.source(MAPPING_TEMPLATE, XContentType.JSON);
            client.indices().create(request, RequestOptions.DEFAULT);
            importAll();
            return count();
        } catch (IOException e) {
            log.error("重建ES索引[{}]失败", INDEX_NAME, e);
            throw new RuntimeException("重建ES索引失败", e);
        }
    }

    @Override
    public void importAll() {
        List<Show> shows = showService.lambdaQuery().eq(Show::getStatus, 1).list();
        if (shows == null || shows.isEmpty()) {
            return;
        }
        BulkRequest bulkRequest = new BulkRequest();
        for (Show show : shows) {
            bulkRequest.add(new IndexRequest(INDEX_NAME)
                    .id(show.getId().toString())
                    .source(buildSource(show), XContentType.JSON));
        }
        try {
            BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (response.hasFailures()) {
                log.error("ES演出数据导入存在失败项：{}", response.buildFailureMessage());
            } else {
                log.info("ES演出数据导入完成，共{}条", shows.size());
            }
            // ES 是近实时搜索，bulk 后立即 count 可能为 0，这里强制刷新
            client.indices().refresh(new RefreshRequest(INDEX_NAME), RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("ES演出数据导入失败", e);
        }
    }

    /**
     * 构建ES文档。日期字段显式转成epoch毫秒，避免序列化器输出ES不认的日期格式
     */
    private Map<String, Object> buildSource(Show show) {
        Map<String, Object> source = new HashMap<>();
        source.put("id", show.getId().toString());
        source.put("name", show.getName());
        source.put("price", show.getPrice());
        source.put("poster", show.getPoster());
        source.put("genre", show.getGenre());
        source.put("venue", show.getVenue());
        source.put("sold", show.getSold());
        source.put("reviewCount", show.getReviewCount());
        source.put("isFeatured", show.getIsFeatured());
        if (show.getShowTime() != null) {
            source.put("showTime", show.getShowTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (show.getUpdateTime() != null) {
            source.put("updateTime", show.getUpdateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return source;
    }

    @Override
    public long count() {
        SearchRequest request = new SearchRequest(INDEX_NAME);
        request.source().size(0).trackTotalHits(true);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            return hits.getTotalHits() == null ? 0 : hits.getTotalHits().value;
        } catch (IOException e) {
            log.error("查询ES文档总数失败", e);
            return 0;
        }
    }

    private static final String MAPPING_TEMPLATE = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"name\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"price\": {\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"poster\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"genre\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"venue\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"sold\": {\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"reviewCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"isFeatured\": {\n" +
            "        \"type\": \"boolean\"\n" +
            "      },\n" +
            "      \"showTime\": {\n" +
            "        \"type\": \"date\"\n" +
            "      },\n" +
            "      \"updateTime\": {\n" +
            "        \"type\": \"date\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
