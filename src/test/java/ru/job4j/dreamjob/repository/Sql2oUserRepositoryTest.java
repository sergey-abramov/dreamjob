package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.*;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository repository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        repository = new Sql2oUserRepository(sql2o);

    }

    @AfterAll
    public static void deleteUsers() {
        repository.deleteUsers();
    }

    @Test
    void dontSave() {
        repository.save(new User(1, "77email", "name", "password"));
        repository.save(new User(1, "55email", "name", "1password"));
        repository.save(new User(1, "44email", "name", "2password"));
        assertThat(repository.save(new User(1, "77email", "name", "password")))
                .isEmpty();
    }

    @Test
    void findByEmailAndPassword() {
        assertThat(repository.findByEmailAndPassword("55email", "1password").get())
                .usingRecursiveComparison().isEqualTo(new User(0, "55email", "name", "1password"));
    }
}