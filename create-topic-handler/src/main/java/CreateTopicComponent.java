import dagger.Component;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.util.ClientModule;
import org.cirrus.infrastructure.handler.util.Logger;

@Singleton
@Component(modules = {ClientModule.class, CreateTopicModule.class})
interface CreateTopicComponent {

  CreateTopicCommand getCreateTopicCommand();

  Logger getLogger();
}
