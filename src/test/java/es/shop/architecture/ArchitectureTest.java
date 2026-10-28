package es.shop.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "es.shop", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_spring =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework..");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_jpa =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..", "org.hibernate..");

    @ArchTest
    static final ArchRule domain_should_be_isolated =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("..controller..", "..persistence..");

    @ArchTest
    static final ArchRule controller_should_only_depend_on_allowed_packages =
            classes().that().resideInAPackage("..controller..")
                    .should().onlyDependOnClassesThat().resideInAnyPackage(
                            "..domain..",
                            "java..",
                            "jakarta.validation..",
                            "jakarta.annotation..",
                            "org.springframework.web..",
                            "org.springframework.http..",
                            "org.springframework.stereotype..",
                            "org.springframework.validation..",
                            "..controller.api..",
                            "..controller.model..",
                            "..controller.mapper..",
                            "io.swagger.v3.oas.annotations..",
                            "org.slf4j.."
                    )
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule persistence_should_only_depend_on_allowed_packages =
            classes().that().resideInAPackage("..persistence..")
                    .should().onlyDependOnClassesThat().resideInAnyPackage(
                            "..domain..",
                            "java..",
                            "org.springframework.jdbc..",
                            "org.springframework.dao..",
                            "org.springframework.stereotype.."
                    )
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controller_should_not_depend_on_persistence =
            noClasses().that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..persistence..");

    @ArchTest
    static final ArchRule persistence_should_not_depend_on_controller =
            noClasses().that().resideInAPackage("..persistence..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule productdaojdbc_must_implement_productdao =
            classes().that().haveName("ProductDaoJdbc")
                    .should().implement("es.shop.domain.ProductDao");

    @ArchTest
    static final ArchRule services_in_domain_must_be_pure =
            noClasses().that().resideInAPackage("..domain..").and().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith("org.springframework.stereotype.Service")
                    .orShould().beAnnotatedWith("org.springframework.stereotype.Component")
                    .orShould().beAnnotatedWith("org.springframework.stereotype.Repository");

    @ArchTest
    static final ArchRule no_autowired_fields =
            noClasses().should().beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired");

    @ArchTest
    static final ArchRule no_javax =
            noClasses().should().dependOnClassesThat().resideInAPackage("javax..");
}
