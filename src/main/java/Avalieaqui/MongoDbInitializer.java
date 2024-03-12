package Avalieaqui;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import Avalieaqui.user.User;

@Component
public class MongoDbInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final MongoTemplate mongoTemplate;

    public MongoDbInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        mongoTemplate.indexOps(User.class)
                .ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
    }
}
