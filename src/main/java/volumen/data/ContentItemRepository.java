package volumen.data;

import org.springframework.data.repository.CrudRepository;

import volumen.model.ContentItem;

public interface ContentItemRepository extends CrudRepository<ContentItem, Long> {

}
