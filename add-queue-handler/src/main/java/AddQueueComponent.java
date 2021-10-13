import dagger.Component;
import javax.inject.Singleton;
import org.cirrus.infrastructure.handler.util.ClientModule;
import org.cirrus.infrastructure.handler.util.Logger;

@Singleton
@Component(modules = {ClientModule.class, AddQueueModule.class})
interface AddQueueComponent {

  AddQueueCommandFactory getAddQueueCommandFactory();

  Logger getLogger();
}
