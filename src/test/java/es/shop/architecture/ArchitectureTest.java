package es.shop.architecture;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AnalyzeClasses(packages = "es.shop", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    private static final String BASE = "es.shop";

    /** Capas permitidas en cada módulo. */
    private static final Map<String, List<String>> MODULOS = Map.of(
            "producto", List.of("api", "apln", "config", "controller", "domain", "persistence"),
            "carrito", List.of("api", "apln", "config", "controller", "domain", "persistence"),
            "pedido", List.of("api", "apln", "config", "controller", "domain", "persistence"),
            "auth", List.of("api", "apln", "service", "config", "controller", "domain", "persistence"));

    private static final List<String> PAQUETES_EXTERNOS_PERSISTENCIA = List.of(
            "java",
            "org.springframework.jdbc",
            "org.springframework.dao",
            "org.springframework.stereotype");

    // ---------------------------------------------------------------- Reglas por capa (vigentes)

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
                    .should().dependOnClassesThat().resideInAnyPackage("..controller..", "..persistence..", "..apln..");

    @ArchTest
    static final ArchRule controller_should_only_depend_on_allowed_packages =
            classes().that().resideInAPackage("..controller..")
                    .should().onlyDependOnClassesThat().resideInAnyPackage(
                            "..domain..",
                            "..api..",
                            "..apln..",
                            "..dto..",
                            "..controller..",
                            "java..",
                            "jakarta.validation..",
                            "jakarta.annotation..",
                            "jakarta.servlet..",
                            "org.springframework.web..",
                            "org.springframework.http..",
                            "org.springframework.stereotype..",
                            "org.springframework.validation..",
                            "io.swagger.v3.oas.annotations..",
                            "com.fasterxml.jackson..",
                            "org.slf4j.."
                    )
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controllers_must_not_depend_on_persistence =
            noClasses().that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..persistence..");

    @ArchTest
    static final ArchRule persistence_should_not_depend_on_controller =
            noClasses().that().resideInAPackage("..persistence..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule persistence_must_only_depend_on_own_domain_and_allowed =
            classes().that().resideInAPackage("..persistence..")
                    .should(dependerSoloDeSuDominioYPaquetesPermitidos())
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule apln_should_not_depend_on_controller_or_persistence =
            noClasses().that().resideInAPackage("..apln..")
                    .should().dependOnClassesThat().resideInAnyPackage("..controller..", "..persistence..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule productdaojdbc_must_implement_productdao =
            classes().that().haveSimpleName("ProductDaoJdbc")
                    .should().implement("es.shop.producto.domain.ProductDao");

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

    // ---------------------------------------------------------------- Monolito modular (SolidString)

    @ArchTest
    static final ArchRule modules_must_only_access_via_api =
            classes().that().resideInAPackage(BASE + "..")
                    .should(dependerDeOtrosModulosSoloSi(
                            "acceder a otros módulos solo vía su paquete api (nunca api.impl)",
                            (modulo, paquete) -> estaEn(paquete, BASE + "." + modulo + ".api")
                                    && !estaEn(paquete, BASE + "." + modulo + ".api.impl")));

    @ArchTest
    static final ArchRule no_cross_module_domain_or_persistence =
            classes().that().resideInAPackage(BASE + "..")
                    .should(dependerDeOtrosModulosSoloSi(
                            "no acceder a domain ni persistence de otro módulo",
                            (modulo, paquete) -> !estaEn(paquete, BASE + "." + modulo + ".domain")
                                    && !estaEn(paquete, BASE + "." + modulo + ".persistence")));

    @ArchTest
    static final ArchRule auth_isolation =
            noClasses().that().resideOutsideOfPackage("es.shop.auth..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "es.shop.auth.apln..",
                            "es.shop.auth.service..",
                            "es.shop.auth.domain..",
                            "es.shop.auth.persistence..",
                            "es.shop.auth.controller..",
                            "es.shop.auth.config..",
                            "es.shop.auth.api.impl..")
                    .because("Only auth.api accessible from outside auth");

    @ArchTest
    static final ArchRule carrito_must_only_call_auth_api =
            noClasses().that().resideInAPackage("es.shop.carrito..")
                    .should().dependOnClassesThat().resideInAnyPackage("es.shop.producto..", "es.shop.pedido..")
                    .because("carrito solo puede llamar a auth.api");

    @ArchTest
    static final ArchRule producto_must_not_depend_on_other_modules =
            noClasses().that().resideInAPackage("es.shop.producto..")
                    .should().dependOnClassesThat().resideInAnyPackage("es.shop.carrito..", "es.shop.pedido..", "es.shop.auth..");

    @ArchTest
    static final ArchRule shared_must_not_depend_on_modules =
            noClasses().that().resideInAPackage("es.shop.shared..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "es.shop.producto..", "es.shop.carrito..", "es.shop.pedido..", "es.shop.auth..");

    @ArchTest
    static final ArchRule modules_should_be_free_of_cycles =
            slices().matching("es.shop.(*)..").should().beFreeOfCycles();

    @ArchTest
    static final ArchRule module_classes_must_reside_in_known_layers =
            classes().that().resideInAnyPackage(
                            "es.shop.producto..", "es.shop.carrito..", "es.shop.pedido..", "es.shop.auth..")
                    .should(residirEnUnaCapaPermitida());

    @ArchTest
    static void producto_carrito_pedido_must_have_core_packages(JavaClasses clases) {
        assertPaquetesExisten(clases, List.of("producto", "carrito", "pedido"),
                List.of("controller", "domain", "persistence"));
    }

    @ArchTest
    static void auth_must_have_its_packages(JavaClasses clases) {
        assertPaquetesExisten(clases, List.of("auth"),
                List.of("api", "apln", "service", "controller", "domain", "persistence"));
    }

    // ---------------------------------------------------------------- Utilidades

    private static boolean estaEn(String paquete, String raiz) {
        return paquete.equals(raiz) || paquete.startsWith(raiz + ".");
    }

    private static Optional<String> moduloDe(JavaClass clase) {
        return MODULOS.keySet().stream()
                .filter(modulo -> estaEn(clase.getPackageName(), BASE + "." + modulo))
                .findFirst();
    }

    private static JavaClass destino(Dependency dependencia) {
        return dependencia.getTargetClass().getBaseComponentType();
    }

    /**
     * Comprueba cada dependencia hacia un módulo distinto del de la clase origen con {@code permitida(moduloDestino, paqueteDestino)}.
     */
    private static ArchCondition<JavaClass> dependerDeOtrosModulosSoloSi(String descripcion,
                                                                        BiPredicate<String, String> permitida) {
        return new ArchCondition<>(descripcion) {
            @Override
            public void check(JavaClass origen, ConditionEvents events) {
                Optional<String> moduloOrigen = moduloDe(origen);
                for (Dependency dependencia : origen.getDirectDependenciesFromSelf()) {
                    JavaClass destino = destino(dependencia);
                    Optional<String> moduloDestino = moduloDe(destino);
                    if (moduloDestino.isEmpty() || moduloDestino.equals(moduloOrigen)) {
                        continue;
                    }
                    if (!permitida.test(moduloDestino.get(), destino.getPackageName())) {
                        events.add(SimpleConditionEvent.violated(dependencia, dependencia.getDescription()));
                    }
                }
            }
        };
    }

    private static ArchCondition<JavaClass> dependerSoloDeSuDominioYPaquetesPermitidos() {
        return new ArchCondition<>("depender solo de su propio domain/persistence y de " + PAQUETES_EXTERNOS_PERSISTENCIA) {
            @Override
            public void check(JavaClass origen, ConditionEvents events) {
                Optional<String> modulo = moduloDe(origen);
                for (Dependency dependencia : origen.getDirectDependenciesFromSelf()) {
                    JavaClass destino = destino(dependencia);
                    if (destino.isPrimitive()) {
                        continue;
                    }
                    String paquete = destino.getPackageName();
                    boolean externoPermitido = PAQUETES_EXTERNOS_PERSISTENCIA.stream().anyMatch(p -> estaEn(paquete, p));
                    boolean propio = modulo
                            .map(m -> estaEn(paquete, BASE + "." + m + ".domain") || estaEn(paquete, BASE + "." + m + ".persistence"))
                            .orElse(false);
                    if (!externoPermitido && !propio) {
                        events.add(SimpleConditionEvent.violated(dependencia, dependencia.getDescription()));
                    }
                }
            }
        };
    }

    private static ArchCondition<JavaClass> residirEnUnaCapaPermitida() {
        return new ArchCondition<>("residir en una capa permitida de su módulo " + MODULOS) {
            @Override
            public void check(JavaClass clase, ConditionEvents events) {
                moduloDe(clase).ifPresent(modulo -> {
                    boolean enCapa = MODULOS.get(modulo).stream()
                            .anyMatch(capa -> estaEn(clase.getPackageName(), BASE + "." + modulo + "." + capa));
                    if (!enCapa) {
                        events.add(SimpleConditionEvent.violated(clase,
                                clase.getName() + " no está en ninguna capa permitida de " + modulo));
                    }
                });
            }
        };
    }

    private static void assertPaquetesExisten(JavaClasses clases, List<String> modulos, List<String> capas) {
        List<String> faltan = new ArrayList<>();
        for (String modulo : modulos) {
            for (String capa : capas) {
                String paquete = BASE + "." + modulo + "." + capa;
                if (clases.stream().noneMatch(clase -> estaEn(clase.getPackageName(), paquete))) {
                    faltan.add(paquete);
                }
            }
        }
        assertTrue(faltan.isEmpty(), "Faltan paquetes obligatorios: " + faltan);
    }
}
