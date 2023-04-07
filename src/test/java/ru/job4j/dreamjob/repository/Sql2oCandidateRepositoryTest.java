package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;

class Sql2oCandidateRepositoryTest {

    private static Sql2oCandidateRepository candidateRepository;
    private static Sql2oFileRepository fileRepository;
    private static File file;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oCandidateRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        candidateRepository = new Sql2oCandidateRepository(sql2o);
        fileRepository = new Sql2oFileRepository(sql2o);

        file = new File("test", "test");
        fileRepository.save(file);
    }

    @AfterAll
    public static void deleteFile() {
        fileRepository.deleteById(file.getId());
    }

    @AfterEach
    public void clearCandidates() {
        var candidates = candidateRepository.findAll();
        for (var candidate : candidates) {
            candidateRepository.deleteById(candidate.getId());
        }
    }

    @Test
    void whenSaveThenGetSame() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = candidateRepository.save(new Candidate(0, "name",
                "description", creationDate, 1, file.getId()));
        var savedCandidate = candidateRepository.findById(candidate.getId()).get();
        assertThat(savedCandidate).usingRecursiveComparison().isEqualTo(candidate);
    }

    @Test
    void whenSaveSeveralThenGetAll() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate1 = candidateRepository.save(new Candidate(0, "name1",
                "description1", creationDate, 1, file.getId()));
        var candidate2 = candidateRepository.save(new Candidate(0, "name2",
                "description2", creationDate, 1, file.getId()));
        var candidate3 = candidateRepository.save(new Candidate(0, "name3",
                "description3", creationDate, 1, file.getId()));
        var result = candidateRepository.findAll();
        assertThat(result).isEqualTo(List.of(candidate1, candidate2, candidate3));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(candidateRepository.findAll()).isEqualTo(emptyList());
        assertThat(candidateRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = candidateRepository.save(new Candidate(0, "name",
                "description", creationDate, 1, file.getId()));
        var isDeleted = candidateRepository.deleteById(candidate.getId());
        var savedCandidate = candidateRepository.findById(candidate.getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedCandidate).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(candidateRepository.deleteById(2)).isFalse();
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = candidateRepository.save(new Candidate(0, "name",
                "description", creationDate, 1, file.getId()));
        var updatedCandidate = new Candidate(candidate.getId(), "new name",
                "new description", creationDate, 1, file.getId());
        var isUpdated = candidateRepository.update(updatedCandidate);
        var savedCandidate = candidateRepository.findById(updatedCandidate.getId()).get();
        assertThat(isUpdated).isTrue();
        assertThat(savedCandidate).usingRecursiveComparison().isEqualTo(updatedCandidate);
    }

    @Test
    public void whenUpdateUnExistingVacancyThenGetFalse() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = new Candidate(0, "name",
                "description", creationDate, 1, file.getId());
        assertThat(candidateRepository.update(candidate)).isFalse();
    }
}