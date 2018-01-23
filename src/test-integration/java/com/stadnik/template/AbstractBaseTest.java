package com.stadnik.template;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

/**
 * Base class for running integration tests,
 * uses {@link org.testcontainers.utility.TestcontainersConfiguration} to setup integration environment
 * <p>
 * Configuration for docker-compose:
 * <a href="file:../src/main/resources/docker-compose.yml"></a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TemplateServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = AbstractBaseTest.Initializer.class)
@ActiveProfiles("test")
public abstract class AbstractBaseTest {

    private static final String POSTGRES_CONTAINER_NAME = "postgres_1";
    private static final Integer POSTGRES_PORT = 5432;

    private static final DockerComposeContainer DOCKER_COMPOSE_CONTAINER = new DockerComposeContainer(
            new File("src/main/resources/docker-compose.yml"))
            .withExposedService(POSTGRES_CONTAINER_NAME, POSTGRES_PORT);

    static {
        DOCKER_COMPOSE_CONTAINER.starting(null);
    }

    /**
     * Custom Spring initializer to override environment variables in case docker runs inside virtual machine
     */
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        /**
         * Overrides Spring properties
         *
         * @param configurableApplicationContext Spring context
         */
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            EnvironmentTestUtils.addEnvironment("testcontainers", configurableApplicationContext.getEnvironment(),
                    "db.host:" + DOCKER_COMPOSE_CONTAINER.getServiceHost(POSTGRES_CONTAINER_NAME, POSTGRES_PORT)
            );
        }
    }

}
