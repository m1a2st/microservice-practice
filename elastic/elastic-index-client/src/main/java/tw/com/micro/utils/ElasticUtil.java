package tw.com.micro.utils;

import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;
import tw.com.micro.IndexModel;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElasticUtil<T extends IndexModel> {

    public List<IndexQuery> getIndexQueryList(List<T> documents) {
        return documents
                .stream()
                .map(document -> new IndexQueryBuilder()
                        .withId(document.getId())
                        .withObject(document)
                        .build())
                .collect(Collectors.toList());
    }
}
