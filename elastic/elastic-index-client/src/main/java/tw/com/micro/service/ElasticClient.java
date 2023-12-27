package tw.com.micro.service;

import org.springframework.stereotype.Repository;
import tw.com.micro.IndexModel;

import java.util.List;

public interface ElasticClient<T extends IndexModel> {

    List<String> save(List<T> documents);
}
