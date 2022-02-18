import org.cirrus.infrastructure.handler.NodeRecord;
import org.cirrus.infrastructure.util.Logger;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@ExtendWith(MockitoExtension.class)
class CreateNodeHandlerTests {

  private static final String NODE_ID = "nodeId";
  private static final String FUNCTION_ID = "functionId";
  private static final String QUEUE_ID = "queueId";

  CreateNodeHandlerTests(
      @Mock LambdaAsyncClient lambdaClient,
      @Mock SqsAsyncClient sqsClient,
      @Mock DynamoDbAsyncTable<NodeRecord> nodeRegistry,
      @Mock(stubOnly = true) Logger logger) {}
}
