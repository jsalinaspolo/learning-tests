package con.jspcore.kafka.producer


import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster
import org.junit.ClassRule
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import java.time.Duration
import java.time.temporal.ChronoUnit

class KafkaProducerShould extends Specification {

  public static final String TOPIC = "topic-1"
  @ClassRule
  @Shared
  EmbeddedKafkaCluster cluster = new EmbeddedKafkaCluster(3)

  @Shared
  def log = LoggerFactory.getLogger(getClass())

  def kafkaProducer = new KafkaProducer<String, String>(producerProperties())
  def kafkaConsumer = providesKafkaConsumer()

  def setupSpec() {
    cluster.start()
  }

  def cleanupSpec() {
    cluster.stop()
  }

  def "send events to kafka topic"() {
    given:
    cluster.createTopic(TOPIC, 8, 3)
    kafkaConsumer.subscribe([TOPIC])

    when:
    kafkaProducer.send(new ProducerRecord<String, String>(TOPIC, "key-1", "value-1"))

    then:
    new PollingConditions(timeout: 5.0).eventually {
      def events = kafkaConsumer.poll(Duration.ofMillis(50))
      assert events.size() == 1
    }
  }

  def producerProperties() {
    Properties properties = new Properties()
    properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = cluster.bootstrapServers()
    properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer.class
    properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer.class
    return properties
  }

  def Consumer<String, String> providesKafkaConsumer() {
    def props = new Properties()
    props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = cluster.bootstrapServers()
    props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer.class.name
    props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer.class.name
    props[ConsumerConfig.GROUP_ID_CONFIG] = "group-id"
    props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "FALSE"
    props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

    props[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 2
    props[ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG] = Duration.of(3600, ChronoUnit.SECONDS).toMillis().toInteger()
    props[ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG] = Duration.of(3600, ChronoUnit.SECONDS).toMillis().toInteger() + 5000

    return new KafkaConsumer<String, String>(props)
  }

}
