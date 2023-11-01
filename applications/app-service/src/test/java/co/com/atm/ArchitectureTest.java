package co.com.atm;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

// Please do not modify this file
@Log4j2
class ArchitectureTest {
    private static JavaClasses allClasses;
    private static JavaClasses domainClasses;
    private static JavaClasses useCaseClasses;

    @BeforeAll
    static void importClasses() {
        allClasses = new ClassFileImporter().importPackages("co.com.atm");
        domainClasses = new ClassFileImporter().importPackages("co.com.atm.usecase", "co.com.atm.model");
        useCaseClasses = new ClassFileImporter().importPackages("co.com.atm.usecase");
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
            log.warn("ARCHITECTURE_RULE_VIOLATED: This will cause a build error in future.\nPlease review our wiki at https://github.com/bancolombia/scaffold-clean-architecture/wiki/Architecture-Rules", e);
        }
    }
}
