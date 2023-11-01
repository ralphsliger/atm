package co.com.atm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvent;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.elements.MembersShouldConjunction;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

// Please do not modify this file
@Log4j2
class ArchitectureTest {
    private static JavaClasses allClasses;
    private static JavaClasses domainClasses;
    private static JavaClasses useCaseClasses;
    private static final ConcurrentMap<String, Utils.IssuesReport> issues = new ConcurrentHashMap<>();

    private final Map<String, Utils.JavaFile> files = Utils.findFiles();

    @BeforeAll
    static void importClasses() {
        allClasses = new ClassFileImporter().importPackages("co.com.atm");
        domainClasses = new ClassFileImporter().importPackages("co.com.atm.usecase", "co.com.atm.model");
        useCaseClasses = new ClassFileImporter().importPackages("co.com.atm.usecase");
    }

     @AfterAll
    static void exportIssues() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List.of("/Users/nymeria/Documents/pr/atm/","/Users/nymeria/Documents/pr/atm/infrastructure/entry-points/api-rest/","/Users/nymeria/Documents/pr/atm/applications/app-service/","/Users/nymeria/Documents/pr/atm/infrastructure/driven-adapters/jpa-repository/","/Users/nymeria/Documents/pr/atm/domain/model/","/Users/nymeria/Documents/pr/atm/domain/usecase/").forEach(path -> {
                try {
                    Files.write(Path.of(path, "build/issues.json"), mapper.writeValueAsBytes(issues.getOrDefault(path, new Utils.IssuesReport())));
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
            });
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    @Test
    void domainClassesShouldNotBeNamedWithTechSuffixes() {
        ArchRule rule = Stream.of("dto", "request", "response")
                .flatMap(tool -> Stream.of(tool, tool.toUpperCase(), classCase(tool)))
                .reduce(classes().should().haveSimpleNameNotEndingWith("Dto"),
                        (cj, tool) -> cj.andShould().haveSimpleNameNotEndingWith(tool),
                        (a, b) -> b)
                .allowEmptyShould(true)
                .as("Rule_2.2: Domain classes should not be named with technology suffixes");

        checkWithWarning(() -> rule.check(domainClasses));
    }

    @Test
    void domainClassesShouldNotBeNamedWithToolNames() {
        ArchRule rule = Stream.of("rabbit", "sqs", "sns", "ibm", "dynamo", "aws", "mysql", "postgres",
                        "redis", "mongo", "rsocket", "r2dbc", "http", "kms", "s3", "graphql")
                .flatMap(tool -> Stream.of(tool, tool.toUpperCase(), classCase(tool)))
                .reduce(classes().should().haveSimpleNameNotContaining("rabbit"),
                        (cj, tool) -> cj.andShould().haveSimpleNameNotContaining(tool),
                        (a, b) -> b)
                .allowEmptyShould(true)
                .as("Rule_2.4: Domain classes should not be named with technology names");

        checkWithWarning(() -> rule.check(domainClasses));
    }

    @Test
    void useCaseFinalFields() {
        ArchRule rule = classes()
                .that()
                .haveSimpleNameEndingWith("UseCase")
                .should()
                .haveOnlyFinalFields()
                .allowEmptyShould(true)
                .as("Rule_2.5: UseCases should only have final attributes to avoid concurrency issues");

        rule.check(useCaseClasses);
    }

    @Test
    void domainClassesShouldNotHaveFieldsNamedWithToolNames() {
        ArchRule rule = Stream.of("rabbit", "sqs", "sns", "ibm", "dynamo", "aws", "mysql", "postgres",
                        "redis", "mongo", "rsocket", "r2dbc", "http", "kms", "s3", "graphql")
                .flatMap(tool -> Stream.of(tool, tool.toUpperCase(), classCase(tool)))
                .reduce((MembersShouldConjunction<?>) fields().should().haveNameNotContaining("rabbit"),
                        (cj, tool) -> cj.andShould().haveNameNotContaining(tool),
                        (a, b) -> b)
                .allowEmptyShould(true)
                .as("Rule_2.7: Domain classes should not have fields named with technology names");

        checkWithWarning(() -> rule.check(domainClasses));
    }

    @Test
    void beansShouldOnlyHaveFinalFields() {
        ArchRule rule = classes()
                .that()
                .areNotAnnotatedWith(ConfigurationProperties.class)
                .and()
                .areAnnotatedWith(Configuration.class)
                .or().areAnnotatedWith(Component.class)
                .or().areAnnotatedWith(Controller.class)
                .or().areAnnotatedWith(Repository.class)
                .or().areAnnotatedWith(Service.class)
                .should()
                .haveOnlyFinalFields()
                .allowEmptyShould(true)
                .as("Rule_2.7: Beans classes should only have final attributes (injection by constructor) to avoid concurrency issues");

        checkWithWarning(() -> rule.check(allClasses));
    }

    // Utilities

    private String classCase(String tool) {
        return tool.substring(0, 1).toUpperCase() + tool.substring(1);
    }


    private void checkWithWarning(Runnable runnable) {
        try {
            runnable.run();
        } catch (AssertionError e) {
            Utils.ArchitectureRule rule = Utils.parseToRule(e.getMessage());
            rule.getLocations().forEach(location -> {
                Utils.JavaFile file = files.get(location.getClassName());
                if (file != null) {
                    issues.computeIfAbsent(file.getModulePath(), key -> new Utils.IssuesReport())
                            .add(Utils.Issue.from(rule.getRuleId(), Utils.Issue.Severity.MAJOR, Utils.Issue.Type.CODE_SMELL,
                                    Utils.Issue.Location.from(rule.getDescription() + location.getDescription(),
                                            file.getPath(), Utils.Issue.TextRange.from(
                                                    Utils.resolveFinalLocation(file, location))), 5));
                }
            });
            log.warn("ARCHITECTURE_RULE_VIOLATED: This will cause a build error in future.\nPlease review our wiki at https://github.com/bancolombia/scaffold-clean-architecture/wiki/Architecture-Rules", e);
        }
    }
}
